package net.rslover521.createTrainStationSpeakers.content.modclasses;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;

public class CTSCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, CreateTrainStationSpeakers.MODID
    );

    public static final RegistryObject<CreativeModeTab> STATION_SPEAKER_TAB = CREATIVE_MODE_TAB.register(
            CreateTrainStationSpeakers.MODID, () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + CreateTrainStationSpeakers.MODID))
                    .icon(() -> new ItemStack(CTSBlocks.SPEAKER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(CTSBlocks.SPEAKER.get());
                    }).build()
    );
}
