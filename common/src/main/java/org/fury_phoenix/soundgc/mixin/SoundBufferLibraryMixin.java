package org.fury_phoenix.soundgc.mixin;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.fury_phoenix.soundgc.cache.SoundExpiryPolicy;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin {
    @Shadow
    @Final
    @Mutable
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Caffeine.newBuilder()
        .expireAfter(new SoundExpiryPolicy())
        .evictionListener(SoundExpiryPolicy::onSoundEviction)
        .buildAsync()
        .asMap();

}
