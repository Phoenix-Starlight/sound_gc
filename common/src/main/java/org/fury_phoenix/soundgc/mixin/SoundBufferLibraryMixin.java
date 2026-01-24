package org.fury_phoenix.soundgc.mixin;

import com.github.benmanes.caffeine.cache.*;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.Util;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.fury_phoenix.soundgc.cache.SoundExpiryPolicy;
import org.fury_phoenix.soundgc.injection.InjectableSoundBufferLibrary;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin implements InjectableSoundBufferLibrary {
    @Shadow
    @Final
    private Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache;

    @Unique
    private Ticker soundgc$audioTicker;

    @Unique
    private final SoundExpiryPolicy soundgc$policy = new SoundExpiryPolicy();

    @Unique
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> soundgc$cache = Caffeine.newBuilder()
        .ticker(() -> this.soundgc$audioTicker == null ? 0 : this.soundgc$audioTicker.read())
        .expireAfter(soundgc$policy)
        .evictionListener((k, v, c) -> soundgc$policy.onSoundEviction(k, v, c, cache))
        .executor(Util.backgroundExecutor())
        .buildAsync()
        .asMap();

    SoundBufferLibraryMixin() {}

    @Override
    @Unique
    public void soundgc$setAudioTicker(Ticker audioTicker) {
        soundgc$audioTicker = audioTicker;
    }

    @Unique
    private static final Logger soundgc$LOGGER = LogManager.getLogger();

    @Unique
    private static final Marker soundgc$MARKER = MarkerManager.getMarker("SOUNDS");

    @Shadow public abstract CompletableFuture<SoundBuffer> getCompleteBuffer(ResourceLocation arg);

    @Override
    @Unique
    public void soundgc$cacheSoundBuffer(ResourceLocation location) {
        soundgc$LOGGER.debug(soundgc$MARKER, "Caching sound {}", location);
        soundgc$policy.startRead(location);
        soundgc$cache.compute(location, (k, v) -> {
            soundgc$LOGGER.debug(soundgc$MARKER, "COMPUTE");
            return v == null ? getCompleteBuffer(k) : v;
        });
        soundgc$policy.endRead();
    }
}
