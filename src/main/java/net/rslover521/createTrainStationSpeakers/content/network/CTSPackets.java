package net.rslover521.createTrainStationSpeakers.content.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;
import net.rslover521.createTrainStationSpeakers.content.station_speakers.menu.UpdateStationSpeakerConfigPacket;

public class CTSPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CreateTrainStationSpeakers.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    // Utility class only; no instances are needed.
    private CTSPackets() {
    }

    // Register all custom packets used by the speaker configuration flow.
    public static void register() {
        int packetId = 0;
        CHANNEL.messageBuilder(UpdateStationSpeakerConfigPacket.class, packetId++)
                .encoder(UpdateStationSpeakerConfigPacket::encode)
                .decoder(UpdateStationSpeakerConfigPacket::new)
                .consumerMainThread(UpdateStationSpeakerConfigPacket::handle)
                .add();
    }
}
