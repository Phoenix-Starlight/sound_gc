package org.fury_phoenix.soundgc.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.fury_phoenix.soundgc.SoundGC;

@Mod(SoundGC.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(SoundGC.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        SoundGC.init();
    }
}
