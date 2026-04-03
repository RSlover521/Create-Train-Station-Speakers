package net.rslover521.createTrainStationSpeakers.content.speaker;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.rslover521.createTrainStationSpeakers.CTSBlockEntityTypes;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;

import java.util.List;

public class SpeakerBlockEntity extends SmartBlockEntity {
    private static final int ANNOUNCEMENT_COOLDOWN_TICKS = 40;
    private String speakerName = "Station Speaker";
    private long lastAnnouncementTick = Long.MIN_VALUE;

    public SpeakerBlockEntity(BlockPos pos, BlockState state) {
        super(CTSBlockEntityTypes.SPEAKER.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
        setChanged();
    }

    public void triggerManualAnnouncement(Player player) {
        String actor = player == null ? "An operator" : player.getName().getString();
        announce(actor + " is testing " + speakerName + ". Full train announcements coming soon.");
    }

    public void triggerRedstoneAnnouncement() {
        announce(speakerName + " received a redstone pulse.");
    }

    private void announce(String message) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        long gameTime = serverLevel.getGameTime();
        if (gameTime - lastAnnouncementTick < ANNOUNCEMENT_COOLDOWN_TICKS) {
            return;
        }

        lastAnnouncementTick = gameTime;
        Component text = Component.literal("[Speaker] " + message);

        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(text);
        }

        serverLevel.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_CHIME.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        CreateTrainStationSpeakers.LOGGER.info("Speaker announcement at {}: {}", worldPosition, message);
        setChanged();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("SpeakerName", speakerName);
        tag.putLong("LastAnnouncementTick", lastAnnouncementTick);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("SpeakerName")) {
            speakerName = tag.getString("SpeakerName");
        }
        lastAnnouncementTick = tag.getLong("LastAnnouncementTick");
    }
}
