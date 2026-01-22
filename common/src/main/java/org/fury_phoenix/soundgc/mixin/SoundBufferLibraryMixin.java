package org.fury_phoenix.soundgc.mixin;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.fury_phoenix.soundgc.cache.SoundExpiryPolicy;
import org.fury_phoenix.soundgc.cache.SoundExpiryPolicy.InjectableChannelAccess;
import org.fury_phoenix.soundgc.clock.AudioTicker;
import org.fury_phoenix.soundgc.clock.InjectableAudioTicker;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin implements InjectableAudioTicker, InjectableChannelAccess {
    @Unique
    private AudioTicker soundgc$audioTicker;

    @Unique
    private final SoundExpiryPolicy soundgc$policy = new SoundExpiryPolicy();

    @Shadow
    @Final
    @Mutable
    private Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;cache:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private void initCache(SoundBufferLibrary instance, Map<ResourceLocation, CompletableFuture<SoundBuffer>> value) {
        this.cache = Caffeine.newBuilder()
            .ticker(() -> this.soundgc$audioTicker == null ? 0 : this.soundgc$audioTicker.read())
            .expireAfter(this.soundgc$policy)
            .evictionListener(this.soundgc$policy::onSoundEviction)
            .buildAsync()
            .asMap();
    }

    @Override
    @Unique
    public void soundgc$setAudioTicker(AudioTicker audioTicker) {
        soundgc$audioTicker = audioTicker;
    }

    @Unique
    public void soundgc$setChannelAccess(ChannelAccess channelAccess) {
        soundgc$policy.setChannelAccess(channelAccess);
    }

}
