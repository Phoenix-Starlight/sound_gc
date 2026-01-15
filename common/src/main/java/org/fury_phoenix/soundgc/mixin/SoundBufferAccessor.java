package org.fury_phoenix.soundgc.mixin;

import com.mojang.blaze3d.audio.SoundBuffer;
import org.fury_phoenix.soundgc.cache.SoundBufferDuration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

@Mixin(SoundBuffer.class)
public abstract class SoundBufferAccessor implements SoundBufferDuration {
    @Shadow
    private ByteBuffer data;

    @Shadow
    @Final
    private AudioFormat format;

    @Override public long sound_gc$milliseconds_duration() {
        int audioSize = data.capacity();
        return (int) Math.ceil(audioSize / (format.getFrameSize() * format.getFrameRate()) * 1000);
    }
}
