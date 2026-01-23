package org.fury_phoenix.soundgc.injection;

import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundManager;
import org.fury_phoenix.soundgc.clock.AudioTicker;

public interface InjectableSoundBufferLibrary {
    void soundgc$setChannelAccess(ChannelAccess channelAccess);

    void soundgc$setAudioTicker(AudioTicker audioTicker);

    void soundgc$setSoundManager(SoundManager soundManager);
}
