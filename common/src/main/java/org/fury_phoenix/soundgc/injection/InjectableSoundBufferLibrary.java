package org.fury_phoenix.soundgc.injection;

import com.github.benmanes.caffeine.cache.Ticker;
import net.minecraft.resources.ResourceLocation;

public interface InjectableSoundBufferLibrary {
    default void soundgc$setAudioTicker(Ticker audioTicker) { throw new AssertionError(); }

    default void soundgc$cacheSoundBuffer(ResourceLocation location) { throw new AssertionError(); }
}
