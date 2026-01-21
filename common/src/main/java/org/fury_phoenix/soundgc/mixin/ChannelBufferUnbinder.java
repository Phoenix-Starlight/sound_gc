package org.fury_phoenix.soundgc.mixin;

import com.mojang.blaze3d.audio.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Channel.class)
public interface ChannelBufferUnbinder {
    @Invoker("removeProcessedBuffers")
    int soundgc$unbindUsedBuffers();
}
