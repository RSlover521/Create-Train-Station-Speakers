package net.rslover521.createTrainStationSpeakers.content.modclasses;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;
import net.rslover521.createTrainStationSpeakers.content.speaker.SpeakerBlock;

public class CTSBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CreateTrainStationSpeakers.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CreateTrainStationSpeakers.MODID);

    public static final RegistryObject<SpeakerBlock> SPEAKER = BLOCKS.register("speaker",
            () -> new SpeakerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Item> SPEAKER_BLOCK_ITEM = ITEMS.register("speaker",
            () -> new BlockItem(SPEAKER.get(), new Item.Properties()));

    // Utility class only; no instances are needed.
    private CTSBlocks() {
    }

    // Register this mod's block and item registries on the mod event bus.
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
