package net.rslover521.createTrainStationSpeakers;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rslover521.createTrainStationSpeakers.content.speaker.SpeakerBlockEntity;

public class CTSBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateTrainStationSpeakers.MODID);

    public static final RegistryObject<BlockEntityType<SpeakerBlockEntity>> SPEAKER =
            BLOCK_ENTITY_TYPES.register("speaker", () -> BlockEntityType.Builder
                    .of(SpeakerBlockEntity::new, CTSBlocks.SPEAKER.get())
                    .build(null));

    private CTSBlockEntityTypes() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
