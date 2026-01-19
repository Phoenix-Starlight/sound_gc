package org.fury_phoenix.soundgc.clock;

import com.github.benmanes.caffeine.cache.Ticker;

///
/// A wall clock for the sound engine, which accounts for it being paused.
/// @author Fury_Phoenix
///
public interface AudioTicker extends Ticker {
    /// Idempotent.
    void pause();
    /// Idempotent.
    void resume();
    static AudioTicker ticker() {
        return new AudioTicker() {
            private long pauseDuration = 0;
            private long pauseStartTimestamp = 0;
            private boolean paused = false;
            @Override
            public void pause() {
                if (paused) return;
                paused = true;
                pauseStartTimestamp = System.nanoTime();
            }

            @Override public void resume() {
                if (!paused) return;
                paused = false;
                pauseDuration = System.nanoTime() - pauseStartTimestamp + pauseDuration;
                pauseStartTimestamp = 0;
            }

            @Override public long read() {
                return paused ? pauseStartTimestamp : System.nanoTime() - pauseDuration;
            }
        };
    }
}
