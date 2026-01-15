package org.fury_phoenix.soundgc.mixin;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fury_phoenix.soundgc.SoundGC;
import org.fury_phoenix.soundgc.cache.SoundExpiryPolicy;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin {
    @Shadow
    @Final
    @Mutable
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Caffeine.newBuilder()
        .evictionListener(this::sound_gc$onSoundEviction)
        .expireAfter(new SoundExpiryPolicy())
        .buildAsync()
        .asMap();

    @Unique
    private void sound_gc$onSoundEviction(@Nullable ResourceLocation k, @NotNull SoundBuffer buf, RemovalCause cause) {
        buf.discardAlBuffer();
        if (k == null) return;
        SoundGC.LOGGER.debug("Evicted {}", k);
    }

}
