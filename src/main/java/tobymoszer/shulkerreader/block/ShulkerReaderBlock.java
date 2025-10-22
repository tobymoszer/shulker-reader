package tobymoszer.shulkerreader.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.util.ItemScatterer;

import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntity;

public class ShulkerReaderBlock extends BlockWithEntity {
	public static final MapCodec<ShulkerReaderBlock> CODEC = createCodec(ShulkerReaderBlock::new);

	public ShulkerReaderBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ShulkerReaderBlockEntity(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}

		NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
		if (factory != null && player.openHandledScreen(factory) != null) {
			world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, pos);
		}

		return ActionResult.CONSUME;
	}
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return net.minecraft.screen.ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		if (moved) {
			return;
		}

		if (!world.getBlockState(pos).isOf(state.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerReaderBlockEntity shulkerReader) {
				ItemScatterer.spawn(world, pos, shulkerReader);
				world.updateComparators(pos, this);
			}
			world.removeBlockEntity(pos);
		}

		super.onStateReplaced(state, world, pos, moved);
	}
}
