package net.rslover521.createTrainStationSpeakers.content.station_speakers.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.rslover521.createTrainStationSpeakers.content.station_speakers.StationSpeakerBlockEntity;

import java.util.function.Supplier;

public class UpdateStationSpeakerConfigPacket {
    private final BlockPos speakerPos;
    private final String speakerName;
    private final String stationName;

    // Decode the packet payload coming from the client-side configuration screen.
    public UpdateStationSpeakerConfigPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readUtf(64), buffer.readUtf(128));
    }

    // Bundle the player's edited speaker values before sending them to the server.
    public UpdateStationSpeakerConfigPacket(BlockPos speakerPos, String speakerName, String stationName) {
        this.speakerPos = speakerPos;
        this.speakerName = speakerName;
        this.stationName = stationName;
    }

    // Serialize the packet so the server can rebuild the edit request.
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(speakerPos);
        buffer.writeUtf(speakerName, 64);
        buffer.writeUtf(stationName, 128);
    }

    // Apply the edited values to the speaker block entity on the logical server.
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (!(player.level().getBlockEntity(speakerPos) instanceof StationSpeakerBlockEntity speaker)) {
                return;
            }

            if (player.distanceToSqr(
                    speakerPos.getX() + 0.5D,
                    speakerPos.getY() + 0.5D,
                    speakerPos.getZ() + 0.5D
            ) > 64.0D) {
                return;
            }

            speaker.setSpeakerName(speakerName.trim().isEmpty() ? "Station Speaker" : speakerName.trim());
            speaker.setConfiguredStationName(stationName.trim());
        });
        context.setPacketHandled(true);
    }
}
