package org.fury_phoenix.soundgc.mixin;

import net.minecraft.TracingExecutor;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Util.class)
public interface UtilAccessor {
    @Accessor("DOWNLOAD_POOL")
    static TracingExecutor executor() { throw new AssertionError(); }
}
