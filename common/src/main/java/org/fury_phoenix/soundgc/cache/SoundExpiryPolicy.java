package org.fury_phoenix.soundgc.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fury_phoenix.soundgc.mixin.ChannelBufferUnbinder;
import org.fury_phoenix.soundgc.mixin.OpenAlErrorChecker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SoundExpiryPolicy implements Expiry<ResourceLocation, SoundBuffer> {

    private final static long SOUND_RETENTION_DURATION = 120;

    private final static Logger LOGGER = LogManager.getLogger();

    private final static Marker EVICTION = MarkerManager.getMarker("EVICTION");

    private final static Marker TIMESTAMP = MarkerManager.getMarker("TIMESTAMP");

    public interface InjectableChannelAccess {
        void soundgc$setChannelAccess(ChannelAccess channelAccess);
    }

    private ChannelAccess channelAccess;

    public void setChannelAccess(ChannelAccess channelAccess) {
        this.channelAccess = channelAccess;
    }

    public void onSoundEviction(
        @Nullable ResourceLocation k,
        @NotNull SoundBuffer buf,
        RemovalCause cause
    ) {
        // remove buffer from source somehow, or don't remove if not unqueue'd?
        if (!cause.wasEvicted()) return;
        OpenAlErrorChecker.sound_gc$checkALError("Flush errors");
        var handle = channelAccess.createHandle(Library.Pool.STATIC).join();
        handle.execute(c -> ((ChannelBufferUnbinder) c).soundgc$unbindUsedBuffers());
        LOGGER.debug(EVICTION, "Evicting {}", k);
        buf.discardAlBuffer();
    }

    @Override
    public long expireAfterCreate(@NonNull ResourceLocation key, @NonNull SoundBuffer value, long currentTime) {
        long expiration = ((SoundBufferDuration) value).sound_gc$duration() + TimeUnit.SECONDS.toNanos(SOUND_RETENTION_DURATION);
        LOGGER.debug(TIMESTAMP, "Resource Location: {}, Current time: {}, Expiration time: {}", key, currentTime, expiration);
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
