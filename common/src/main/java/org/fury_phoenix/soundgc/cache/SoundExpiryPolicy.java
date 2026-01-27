package org.fury_phoenix.soundgc.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.fury_phoenix.soundgc.mixin.OpenAlErrorChecker;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SoundExpiryPolicy implements Expiry<ResourceLocation, SoundBuffer> {

    private final static long SOUND_RETENTION_DURATION = 360;

    private final static Logger LOGGER = LogManager.getLogger();

    private final static Marker EVICTION = MarkerManager.getMarker("EVICTION");

    private final static Marker TIMESTAMP = MarkerManager.getMarker("TIMESTAMP");

    private ResourceLocation reading;

    public void startRead(ResourceLocation location) {
        reading = location;
    }

    public void endRead() {
        reading = null;
    }

    public void onSoundEviction(
        @NotNull ResourceLocation k,
        @NotNull SoundBuffer buf,
        RemovalCause cause,
        Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache
    ) {
        LOGGER.debug(EVICTION, "Delete cache entry {}", k);

        if (!cause.wasEvicted() || k.equals(reading)) return;

        OpenAlErrorChecker.sound_gc$checkALError("Flush errors");
        LOGGER.debug(EVICTION, "Destroy {}", k);
        cache.remove(k);
        buf.discardAlBuffer();
    }

    @Override
    public long expireAfterCreate(@NotNull ResourceLocation key, @NotNull SoundBuffer value, long currentTime) {
        return calculateExpiration(key, value, currentTime, "CREATE");
    }

    private long calculateExpiration(ResourceLocation key, SoundBuffer value, long currentTime, String mode) {
        LOGGER.debug(TIMESTAMP, mode);
        long expiration = value.sound_gc$duration() + TimeUnit.SECONDS.toNanos(SOUND_RETENTION_DURATION);
        long expirationInTicks = TimeUnit.NANOSECONDS.toSeconds(expiration) * 20;
        LOGGER.debug(TIMESTAMP, "Resource Location: {}, Current time: {}, Expiration time: {}", key, currentTime, expirationInTicks);
        return expirationInTicks;
    }

    @Override
    public long expireAfterUpdate(
        @NotNull ResourceLocation key,
        @NotNull SoundBuffer value,
        long currentTime,
        long currentDuration
    ) {
        return calculateExpiration(key, value, currentTime, "UPDATE");
    }

    @Override
    public long expireAfterRead(
        @NotNull ResourceLocation key,
        @NotNull SoundBuffer value,
        long currentTime,
        long currentDuration
    ) {
        return calculateExpiration(key, value, currentTime, "READ");
    }
}
