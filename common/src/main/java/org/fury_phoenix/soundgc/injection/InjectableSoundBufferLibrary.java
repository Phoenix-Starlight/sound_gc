package org.fury_phoenix.soundgc.injection;

import com.github.benmanes.caffeine.cache.Ticker;
import net.minecraft.resources.ResourceLocation;

public interface InjectableSoundBufferLibrary {

    void soundgc$setAudioTicker(Ticker audioTicker);

    void soundgc$cacheSoundBuffer(ResourceLocation location);
}
