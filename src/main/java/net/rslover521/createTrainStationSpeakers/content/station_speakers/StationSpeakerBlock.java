package net.rslover521.createTrainStationSpeakers.content.station_speakers;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.rslover521.createTrainStationSpeakers.content.modclasses.CTSBlockEntityTypes;
import net.rslover521.createTrainStationSpeakers.content.station_speakers.menu.StationSpeakerConfigMenu;

public class StationSpeakerBlock extends Block implements IBE<StationSpeakerBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public StationSpeakerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    // Tell Create's IBE helper which block entity class belongs to this block.
    @Override
    public Class<StationSpeakerBlockEntity> getBlockEntityClass() {
        return StationSpeakerBlockEntity.class;
    }

    // Tell Create's IBE helper which registered block entity type belongs to this block.
    @Override
    public BlockEntityType<? extends StationSpeakerBlockEntity> getBlockEntityType() {
        return CTSBlockEntityTypes.STATION_SPEAKER.get();
    }

    // Face the front of the speaker toward the player when placed.
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // Support structure rotation in-world and in generated structures.
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    // Support mirroring so blockstates behave correctly when mirrored.
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // Expose the block properties that are serialized into the placed blockstate.
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    // Copy a custom item name onto the placed block entity so named speaker items keep their label.
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (stack.hasCustomHoverName()) {
            withBlockEntityDo(level, pos, speaker -> speaker.setSpeakerName(stack.getHoverName().getString()));
        }
    }

    // Watch redstone power changes and fire a one-shot announcement on rising edge.
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }

        boolean hasSignal = level.hasNeighborSignal(pos);
        boolean isPowered = state.getValue(POWERED);
        if (hasSignal != isPowered) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_ALL);
            if (hasSignal) {
                withBlockEntityDo(level, pos, speaker -> speaker.triggerRedstoneAnnouncement());
            }
        }
    }

    // Manual interaction currently acts as a simple test trigger for the speaker.
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (isCreateWrench(player.getItemInHand(hand))) {
            return onBlockEntityUse(level, pos, speaker -> {
                if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                    openConfigurationMenu(serverPlayer, pos, speaker);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            });
        }

        return onBlockEntityUse(level, pos, speaker -> {
            speaker.triggerManualAnnouncement(player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    // Detect the Create wrench so normal clicks can still act as a debug trigger until more tools are added.
    private boolean isCreateWrench(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.equals(new ResourceLocation("create", "wrench"));
    }

    // Open the speaker editor and send the current speaker values to the client screen.
    private void openConfigurationMenu(ServerPlayer player, BlockPos pos, StationSpeakerBlockEntity speaker) {
        MenuProvider menuProvider = new SimpleMenuProvider(
                (containerId, inventory, menuPlayer) -> new StationSpeakerConfigMenu(
                        containerId,
                        inventory,
                        pos,
                        speaker.getSpeakerName(),
                        speaker.getLinkedStationName() == null ? "" : speaker.getLinkedStationName()
                ),
                Component.translatable("gui.create_train_station_speakers.station_speaker.title")
        );

        NetworkHooks.openScreen(player, menuProvider, buffer -> {
            buffer.writeBlockPos(pos);
            buffer.writeUtf(speaker.getSpeakerName(), 64);
            buffer.writeUtf(speaker.getLinkedStationName() == null ? "" : speaker.getLinkedStationName(), 128);
        });
    }
}
