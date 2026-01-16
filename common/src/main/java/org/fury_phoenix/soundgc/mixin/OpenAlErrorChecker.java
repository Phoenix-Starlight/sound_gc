package org.fury_phoenix.soundgc.mixin;

import com.mojang.blaze3d.audio.OpenAlUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnusedReturnValue")
@Mixin(OpenAlUtil.class)
public interface OpenAlErrorChecker {
    @Invoker("checkALError")
    static boolean sound_gc$checkALError(String string) {
        throw new AssertionError();
    }
}
