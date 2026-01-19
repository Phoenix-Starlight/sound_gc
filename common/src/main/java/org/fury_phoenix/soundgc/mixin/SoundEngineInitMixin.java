package org.fury_phoenix.soundgc.mixin;

import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import org.fury_phoenix.soundgc.clock.AudioTicker;
import org.fury_phoenix.soundgc.clock.InjectableAudioTicker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineInitMixin {
    @Shadow
    @Final
    private SoundBufferLibrary soundBuffers;

    @Unique
    private final AudioTicker soundgc$audioTicker;

    SoundEngineInitMixin() {
        this.soundgc$audioTicker = AudioTicker.ticker();
        ((InjectableAudioTicker)this.soundBuffers).soundgc$setAudioTicker(this.soundgc$audioTicker);
    }

    @Inject(method = "pause", at = @At("RETURN"))
    private void pauseAudioTicker(CallbackInfo ci) {
        soundgc$audioTicker.pause();
    }

    @Inject(method = "resume", at = @At("RETURN"))
    private void resumeAudioTicker(CallbackInfo ci) {
        soundgc$audioTicker.resume();
    }

}
