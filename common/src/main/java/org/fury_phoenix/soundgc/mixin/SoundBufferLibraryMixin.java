package org.fury_phoenix.soundgc.mixin;

import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.fury_phoenix.soundgc.cache.Cache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class) public abstract class SoundBufferLibraryMixin {
    @Shadow
    @Final
    @Mutable
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = new Cache();
}
