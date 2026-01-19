package org.fury_phoenix.soundgc.mixin;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.fury_phoenix.soundgc.cache.SoundExpiryPolicy;
import org.fury_phoenix.soundgc.clock.AudioTicker;
import org.fury_phoenix.soundgc.clock.InjectableAudioTicker;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin implements InjectableAudioTicker {
    @Unique
    private AudioTicker soundgc$audioTicker;

    @Shadow
    @Final
    @Mutable
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Caffeine.newBuilder()
        .ticker(() -> soundgc$audioTicker == null ? 0 : soundgc$audioTicker.read())
        .expireAfter(new SoundExpiryPolicy())
        .evictionListener(SoundExpiryPolicy::onSoundEviction)
        .buildAsync()
        .asMap();

    @Override
    @Unique
    public void soundgc$setAudioTicker(AudioTicker audioTicker) {
        soundgc$audioTicker = audioTicker;
    }

}
