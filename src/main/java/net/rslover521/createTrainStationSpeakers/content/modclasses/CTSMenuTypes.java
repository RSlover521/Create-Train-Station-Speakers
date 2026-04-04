package net.rslover521.createTrainStationSpeakers.content.modclasses;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;
import net.rslover521.createTrainStationSpeakers.content.station_speakers.menu.StationSpeakerConfigMenu;

public class CTSMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CreateTrainStationSpeakers.MODID);

    public static final RegistryObject<MenuType<StationSpeakerConfigMenu>> STATION_SPEAKER_CONFIG =
            MENU_TYPES.register("station_speaker_config", () -> IForgeMenuType.create(StationSpeakerConfigMenu::new));

    // Utility class only; no instances are needed.
    private CTSMenuTypes() {
    }

    // Register this mod's menu types so screens can open from server-side menu providers.
    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
