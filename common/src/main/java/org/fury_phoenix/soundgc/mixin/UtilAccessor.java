package org.fury_phoenix.soundgc.mixin;

import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ExecutorService;

@Mixin(Util.class)
public interface UtilAccessor {
    @Accessor("BACKGROUND_EXECUTOR")
    static ExecutorService executor() { throw new AssertionError(); }
}
