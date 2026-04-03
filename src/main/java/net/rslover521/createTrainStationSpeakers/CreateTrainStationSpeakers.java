package net.rslover521.createTrainStationSpeakers;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateTrainStationSpeakers.MODID)
public class CreateTrainStationSpeakers {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_train_station_speakers";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateTrainStationSpeakers(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        CTSBlocks.register(modEventBus);
        CTSBlockEntityTypes.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(CTSBlocks.SPEAKER_BLOCK_ITEM);
        }
    }
}
