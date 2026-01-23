package org.fury_phoenix.soundgc.injection;

import com.github.benmanes.caffeine.cache.Ticker;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundManager;

public interface InjectableSoundBufferLibrary {
    void soundgc$setChannelAccess(ChannelAccess channelAccess);

    void soundgc$setAudioTicker(Ticker audioTicker);

    void soundgc$setSoundManager(SoundManager soundManager);
}
