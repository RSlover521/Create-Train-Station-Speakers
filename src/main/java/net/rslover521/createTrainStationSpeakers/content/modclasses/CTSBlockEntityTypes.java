package net.rslover521.createTrainStationSpeakers.content.modclasses;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;
import net.rslover521.createTrainStationSpeakers.content.station_speakers.StationSpeakerBlockEntity;

public class CTSBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateTrainStationSpeakers.MODID);

    public static final RegistryObject<BlockEntityType<StationSpeakerBlockEntity>> STATION_SPEAKER =
            BLOCK_ENTITY_TYPES.register("station_speaker", () -> BlockEntityType.Builder
                    .of(StationSpeakerBlockEntity::new, CTSBlocks.STATION_SPEAKER.get())
                    .build(null));

    // Utility class only; no instances are needed.
    private CTSBlockEntityTypes() {
    }

    // Register this mod's block entity types on the mod event bus.
    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
