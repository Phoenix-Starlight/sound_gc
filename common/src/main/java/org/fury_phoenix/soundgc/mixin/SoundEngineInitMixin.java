package org.fury_phoenix.soundgc.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineInitMixin {
    @Shadow
    @Final
    private SoundBufferLibrary soundBuffers;

    @Shadow
    private int tickCount;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void injectAudioTicker(CallbackInfo ci)
    {
        soundBuffers.soundgc$setAudioTicker(() -> this.tickCount);
    }

    @ModifyArg(method = "tickNonPaused", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object cacheSound(Object key) {
        var instance = (SoundInstance) key;
        Sound sound = instance.getSound();
        // We want to manage sound effects which are unmanaged, not music (streamed)
        if (sound.shouldStream()) return key;
        soundBuffers.soundgc$cacheSoundBuffer(sound.getPath());
        return key;
    }

}
