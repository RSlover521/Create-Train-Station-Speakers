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
import net.minecraft.world.level.block.state.BlockState;
import net.rslover521.createTrainStationSpeakers.content.modclasses.CTSBlockEntityTypes;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;

import java.util.List;

public class SpeakerBlockEntity extends SmartBlockEntity {
    private static final int ANNOUNCEMENT_COOLDOWN_TICKS = 40;
    private String speakerName = "Station Speaker";
    private long lastAnnouncementTick = Long.MIN_VALUE;

    // Attach this block entity instance to the registered speaker block entity type.
    public SpeakerBlockEntity(BlockPos pos, BlockState state) {
        super(CTSBlockEntityTypes.SPEAKER.get(), pos, state);
    }

    // Placeholder hook for future Create behaviours such as station binding or configuration.
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    // Update the speaker's stored display name and mark it dirty for saving.
    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
        setChanged();
    }

    // Fire a simple global test message so we can validate the block works end-to-end.
    public void triggerManualAnnouncement(Player player) {
        String actor = player == null ? "An operator" : player.getName().getString();
        announce(actor + " is testing " + speakerName + ". Full train announcements coming soon.");
    }

    // Fire a simple redstone-driven message while real station logic is still being built.
    public void triggerRedstoneAnnouncement() {
        announce(speakerName + " received a redstone pulse.");
    }

    // Send the current announcement to every online player and play a local feedback sound.
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

    // Persist speaker data to NBT so it survives chunk unloads and world restarts.
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("SpeakerName", speakerName);
        tag.putLong("LastAnnouncementTick", lastAnnouncementTick);
    }

    // Restore speaker data from NBT when the block entity is loaded again.
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("SpeakerName")) {
            speakerName = tag.getString("SpeakerName");
        }
        lastAnnouncementTick = tag.getLong("LastAnnouncementTick");
    }
}
