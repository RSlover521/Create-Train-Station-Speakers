package net.rslover521.createTrainStationSpeakers.content.station_speakers;

import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.rslover521.createTrainStationSpeakers.CreateTrainStationSpeakers;
import net.rslover521.createTrainStationSpeakers.content.modclasses.CTSBlockEntityTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StationSpeakerBlockEntity extends SmartBlockEntity {
    private static final int ANNOUNCEMENT_COOLDOWN_TICKS = 40;
    private String speakerName = "Station Speaker";
    private long lastAnnouncementTick = Long.MIN_VALUE;
    private BlockPos linkedStationPos;
    private ResourceKey<Level> linkedStationDimension;
    private String linkedStationName;

    // Attach this block entity instance to the registered speaker block entity type.
    public StationSpeakerBlockEntity(BlockPos pos, BlockState state) {
        super(CTSBlockEntityTypes.STATION_SPEAKER.get(), pos, state);
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

    // Return the saved speaker name so menus can preload the current value into their text fields.
    public String getSpeakerName() {
        return speakerName;
    }

    // Save a link to a live Create station block entity for later announcement lookups.
    public void setLinkedStation(StationBlockEntity station) {
        linkedStationPos = station.getBlockPos().immutable();
        linkedStationDimension = station.getLevel() != null ? station.getLevel().dimension() : null;
        linkedStationName = station.getStation() != null ? station.getStation().name : null;
        setChanged();
    }

    // Save a link from raw values so future menus or packets can configure the speaker without a live BE reference.
    public void setLinkedStation(BlockPos stationPos, ResourceKey<Level> stationDimension, String stationName) {
        linkedStationPos = stationPos == null ? null : stationPos.immutable();
        linkedStationDimension = stationDimension;
        linkedStationName = stationName;
        setChanged();
    }

    // Remove any saved station link when the player clears the speaker configuration.
    public void clearLinkedStation() {
        linkedStationPos = null;
        linkedStationDimension = null;
        linkedStationName = null;
        setChanged();
    }

    // Return the saved station position so menu and debug code can inspect the current binding.
    public BlockPos getLinkedStationPos() {
        return linkedStationPos;
    }

    // Return the saved dimension so callers know which world the speaker expects the station to be in.
    public ResourceKey<Level> getLinkedStationDimension() {
        return linkedStationDimension;
    }

    // Return the last known station name to display useful feedback even if the station is unloaded.
    public String getLinkedStationName() {
        return linkedStationName;
    }

    // Store a user-entered target station name and clear any exact block link until it is resolved later.
    public void setConfiguredStationName(String stationName) {
        linkedStationName = stationName == null || stationName.isBlank() ? null : stationName.trim();
        linkedStationPos = null;
        linkedStationDimension = level != null && linkedStationName != null ? level.dimension() : null;
        setChanged();
    }

    // Tell callers whether the speaker has enough saved data to attempt a station lookup.
    public boolean hasLinkedStation() {
        return linkedStationPos != null && linkedStationDimension != null;
    }

    // Resolve the saved link back into a live StationBlockEntity in the current level when possible.
    public Optional<StationBlockEntity> getLinkedStation() {
        if (level == null || !hasLinkedStation() || !Objects.equals(level.dimension(), linkedStationDimension)) {
            return Optional.empty();
        }

        if (!(level.getBlockEntity(linkedStationPos) instanceof StationBlockEntity station)) {
            return Optional.empty();
        }

        return Optional.of(station);
    }

    // Check whether the saved link currently points at a loaded Create station block entity.
    public boolean isLinkedStationValid() {
        return getLinkedStation().isPresent();
    }

    // Build a readable link label that can be reused in chat output, menus, and debug messages.
    public String describeLinkedStation() {
        if (!hasLinkedStation()) {
            return "No station linked";
        }

        String stationLabel = linkedStationName != null && !linkedStationName.isBlank()
                ? linkedStationName
                : linkedStationPos.toShortString();
        return stationLabel + " @ " + linkedStationDimension.location();
    }

    // Fire a simple global test message so we can validate the block works end-to-end.
    public void triggerManualAnnouncement(Player player) {
        String actor = player == null ? "An operator" : player.getName().getString();
        announce(actor + " is testing " + speakerName + ". " + describeLinkedStation() + ".");
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
        Component text = Component.literal("[Station Speaker] " + message);

        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(text);
        }

        serverLevel.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_CHIME.get(), SoundSource.BLOCKS,
                1.0F, 1.0F);
        CreateTrainStationSpeakers.LOGGER.info("Speaker announcement at {}: {}", worldPosition, message);
        setChanged();
    }

    // Persist speaker data to NBT so it survives chunk unloads and world restarts.
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("SpeakerName", speakerName);
        tag.putLong("LastAnnouncementTick", lastAnnouncementTick);
        if (linkedStationPos != null) {
            tag.putLong("LinkedStationPos", linkedStationPos.asLong());
        }
        if (linkedStationDimension != null) {
            tag.putString("LinkedStationDimension", linkedStationDimension.location().toString());
        }
        if (linkedStationName != null) {
            tag.putString("LinkedStationName", linkedStationName);
        }
    }

    // Restore speaker data from NBT when the block entity is loaded again.
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("SpeakerName")) {
            speakerName = tag.getString("SpeakerName");
        }
        lastAnnouncementTick = tag.getLong("LastAnnouncementTick");
        linkedStationPos = tag.contains("LinkedStationPos") ? BlockPos.of(tag.getLong("LinkedStationPos")) : null;
        linkedStationDimension = tag.contains("LinkedStationDimension")
                ? ResourceKey.create(Registries.DIMENSION,
                new ResourceLocation(tag.getString("LinkedStationDimension")))
                : null;
        linkedStationName = tag.contains("LinkedStationName") ? tag.getString("LinkedStationName") : null;
    }
}
