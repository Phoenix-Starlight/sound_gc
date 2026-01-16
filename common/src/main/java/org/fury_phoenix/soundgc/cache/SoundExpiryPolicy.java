package org.fury_phoenix.soundgc.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fury_phoenix.soundgc.SoundGC;
import org.fury_phoenix.soundgc.mixin.OpenAlErrorChecker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SoundExpiryPolicy implements Expiry<ResourceLocation, SoundBuffer> {

    private final static long SOUND_RETENTION_DURATION = 120;

    public static void onSoundEviction(
        @Nullable ResourceLocation k,
        @NotNull SoundBuffer buf,
        RemovalCause cause
    ) {
        // remove buffer from source somehow, or don't remove if not unqueue'd?
        OpenAlErrorChecker.sound_gc$checkALError("Flush errors");
        if (cause != RemovalCause.EXPIRED) return;
        if (k != null) SoundGC.LOGGER.debug("Evicting {}", k);
        buf.discardAlBuffer();
    }

    @Override
    public long expireAfterCreate(@NonNull ResourceLocation key, @NonNull SoundBuffer value, long currentTime) {
        long expiration = ((SoundBufferDuration) value).sound_gc$duration() + TimeUnit.SECONDS.toNanos(SOUND_RETENTION_DURATION);
        SoundGC.LOGGER.debug("Resource Location: {}, Current time: {}, Expiration time: {}", key, currentTime, expiration);
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
