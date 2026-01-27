package org.fury_phoenix.soundgc.cache;

public interface SoundBufferDuration {
    default long sound_gc$duration() { throw new AssertionError(); }
}
