package net.rslover521.createTrainStationSpeakers.content.station_speakers.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.rslover521.createTrainStationSpeakers.content.modclasses.CTSMenuTypes;

public class StationSpeakerConfigMenu extends AbstractContainerMenu {
    private final BlockPos speakerPos;
    private final String speakerName;
    private final String linkedStationName;

    // Rebuild the menu from network data that the server wrote when opening the screen.
    public StationSpeakerConfigMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory, buffer.readBlockPos(), buffer.readUtf(64), buffer.readUtf(128));
    }

    // Build a lightweight menu that only carries the speaker's editable configuration fields.
    public StationSpeakerConfigMenu(int containerId, Inventory inventory, BlockPos speakerPos, String speakerName,
                             String linkedStationName) {
        super(CTSMenuTypes.STATION_SPEAKER_CONFIG.get(), containerId);
        this.speakerPos = speakerPos;
        this.speakerName = speakerName;
        this.linkedStationName = linkedStationName;
    }

    // Expose the target speaker position so save packets know which block entity to update.
    public BlockPos getSpeakerPos() {
        return speakerPos;
    }

    // Expose the initial speaker name so the client screen can seed its text box.
    public String getSpeakerName() {
        return speakerName;
    }

    // Expose the initial station name so the client screen can seed its text box.
    public String getLinkedStationName() {
        return linkedStationName;
    }

    // Keep the menu open only while the player stays close to the speaker block.
    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                speakerPos.getX() + 0.5D,
                speakerPos.getY() + 0.5D,
                speakerPos.getZ() + 0.5D
        ) <= 64.0D;
    }

    // Disable shift-click transfer because this configuration menu does not expose any inventory slots.
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
