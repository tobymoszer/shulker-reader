package tobymoszer.shulkerreader.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

import tobymoszer.shulkerreader.ShulkerReader;
import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntity;

public class ShulkerReaderBlock extends BaseEntityBlock implements PolymerTexturedBlock {
	public static final MapCodec<ShulkerReaderBlock> CODEC = simpleCodec(ShulkerReaderBlock::new);
	private static final Identifier UNPOWERED_MODEL = Identifier.fromNamespaceAndPath(
		ShulkerReader.MOD_ID,
		"block/shulker_reader"
	);
	private static final Identifier POWERED_MODEL = Identifier.fromNamespaceAndPath(
		ShulkerReader.MOD_ID,
		"block/shulker_reader_powered"
	);
	private static final BlockState NORTH_UNPOWERED = requestModel(UNPOWERED_MODEL, 180);
	private static final BlockState NORTH_POWERED = requestModel(POWERED_MODEL, 180);
	private static final BlockState SOUTH_UNPOWERED = requestModel(UNPOWERED_MODEL, 0);
	private static final BlockState SOUTH_POWERED = requestModel(POWERED_MODEL, 0);
	private static final BlockState WEST_UNPOWERED = requestModel(UNPOWERED_MODEL, 90);
	private static final BlockState WEST_POWERED = requestModel(POWERED_MODEL, 90);
	private static final BlockState EAST_UNPOWERED = requestModel(UNPOWERED_MODEL, 270);
	private static final BlockState EAST_POWERED = requestModel(POWERED_MODEL, 270);

	public ShulkerReaderBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
			.setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
		return this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
			.setValue(BlockStateProperties.POWERED, powered);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
		if (context == null || !PolymerResourcePackUtils.hasMainPack(context)) {
			return Blocks.TARGET.defaultBlockState();
		}

		boolean powered = state.getValue(BlockStateProperties.POWERED);
		return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
			case NORTH -> powered ? NORTH_POWERED : NORTH_UNPOWERED;
			case SOUTH -> powered ? SOUTH_POWERED : SOUTH_UNPOWERED;
			case WEST -> powered ? WEST_POWERED : WEST_UNPOWERED;
			case EAST -> powered ? EAST_POWERED : EAST_UNPOWERED;
			default -> Blocks.TARGET.defaultBlockState();
		};
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ShulkerReaderBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		MenuProvider factory = state.getMenuProvider(level, pos);
		if (factory != null && player.openMenu(factory).isPresent()) {
			level.gameEvent(player, GameEvent.CONTAINER_OPEN, pos);
		}

		return InteractionResult.CONSUME;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ShulkerReaderBlockEntity shulkerReader) {
			return shulkerReader.calculateComparatorOutput(state.getValue(BlockStateProperties.POWERED));
		}
		return 0;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
		if (moved) {
			return;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ShulkerReaderBlockEntity shulkerReader) {
			Containers.dropContents(level, pos, shulkerReader);
			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.affectNeighborsAfterRemoval(state, level, pos, moved);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
		if (level.isClientSide()) {
			return;
		}

		boolean powered = level.hasNeighborSignal(pos);
		if (powered != state.getValue(BlockStateProperties.POWERED)) {
			level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, powered), Block.UPDATE_ALL);
			level.updateNeighbourForOutputSignal(pos, this);
		}
	}

	private static BlockState requestModel(Identifier model, int yRotation) {
		BlockState state = PolymerBlockResourceUtils.requestBlock(
			BlockModelType.FULL_BLOCK,
			PolymerBlockModel.of(model, 0, yRotation)
		);
		if (state == null) {
			ShulkerReader.LOGGER.warn("No Polymer full-block state was available for {}", model);
			return Blocks.TARGET.defaultBlockState();
		}
		return state;
	}
}
