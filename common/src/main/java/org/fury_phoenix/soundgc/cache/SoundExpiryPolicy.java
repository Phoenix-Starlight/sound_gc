package org.fury_phoenix.soundgc.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.fury_phoenix.soundgc.SoundGC;

import java.util.concurrent.TimeUnit;

public class SoundExpiryPolicy implements Expiry<ResourceLocation, SoundBuffer> {

    private final static long SOUNd_RETENTION_DURATION = 30;

    @Override
    public long expireAfterCreate(@NonNull ResourceLocation key, @NonNull SoundBuffer value, long currentTime) {
        long expiration = currentTime + ((SoundBufferDuration) value).sound_gc$milliseconds_duration() + TimeUnit.SECONDS.toMillis(SOUNd_RETENTION_DURATION);
        SoundGC.LOGGER.debug("Current time: {}, Expiration time: {}", currentTime, expiration);
        return expiration;
    }

    @Override
    public long expireAfterUpdate(
        @NonNull ResourceLocation key,
        @NonNull SoundBuffer value,
        long currentTime,
        @NonNegative long currentDuration
    ) {
        return expireAfterCreate(key, value, currentTime);
    }

    @Override
    public long expireAfterRead(
        @NonNull ResourceLocation key,
        @NonNull SoundBuffer value,
        long currentTime,
        @NonNegative long currentDuration
    ) {
        return expireAfterCreate(key, value, currentTime);
    }
}
