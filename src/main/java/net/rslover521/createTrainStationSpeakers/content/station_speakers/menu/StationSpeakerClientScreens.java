package net.rslover521.createTrainStationSpeakers.content.station_speakers.menu;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;
import net.rslover521.createTrainStationSpeakers.content.modclasses.CTSMenuTypes;

@Mod.EventBusSubscriber(modid = CreateTrainStationSpeakers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StationSpeakerClientScreens {
    // Register the screen factory that renders the speaker configuration menu on the client.
    @SubscribeEvent
    public static void registerScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(
                CTSMenuTypes.STATION_SPEAKER_CONFIG.get(),
                StationSpeakerConfigScreen::new
        ));
    }
}
