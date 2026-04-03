package net.rslover521.createTrainStationSpeakers.content.speaker;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.rslover521.createTrainStationSpeakers.content.modclasses.CTSBlockEntityTypes;

public class SpeakerBlock extends Block implements IBE<SpeakerBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SpeakerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    // Tell Create's IBE helper which block entity class belongs to this block.
    @Override
    public Class<SpeakerBlockEntity> getBlockEntityClass() {
        return SpeakerBlockEntity.class;
    }

    // Tell Create's IBE helper which registered block entity type belongs to this block.
    @Override
    public BlockEntityType<? extends SpeakerBlockEntity> getBlockEntityType() {
        return CTSBlockEntityTypes.SPEAKER.get();
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
        return onBlockEntityUse(level, pos, speaker -> {
            speaker.triggerManualAnnouncement(player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }
}
