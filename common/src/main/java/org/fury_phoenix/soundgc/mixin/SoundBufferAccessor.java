package org.fury_phoenix.soundgc.mixin;

import com.mojang.blaze3d.audio.SoundBuffer;
import org.fury_phoenix.soundgc.cache.SoundBufferDuration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@Mixin(SoundBuffer.class)
public abstract class SoundBufferAccessor implements SoundBufferDuration {
    @Shadow
    private ByteBuffer data;

    @Shadow
    @Final
    private AudioFormat format;

    @Unique
    private final long sound_gc$duration;

    SoundBufferAccessor() {
        int audioSize = data.capacity();
        float bytesPerSecond = format.getFrameSize() * format.getFrameRate();
        sound_gc$duration = TimeUnit.MILLISECONDS.toNanos((int) Math.ceil(audioSize / bytesPerSecond * 1000));
    }

    @Override public long sound_gc$duration() {
        return sound_gc$duration;
    }
}
