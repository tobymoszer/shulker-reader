package tobymoszer.shulkerreader.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import tobymoszer.shulkerreader.screen.ShulkerReaderScreenHandler;
import tobymoszer.shulkerreader.screen.VanillaShulkerReaderScreenHandler;

public class ShulkerReaderBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
	private static final int[] AVAILABLE_SLOTS = new int[] { 0 };
	private static final List<DyeColor> COLOR_ORDER = List.of(
		DyeColor.RED,
		DyeColor.ORANGE,
		DyeColor.YELLOW,
		DyeColor.LIME,
		DyeColor.GREEN,
		DyeColor.CYAN,
		DyeColor.LIGHT_BLUE,
		DyeColor.BLUE,
		DyeColor.PURPLE,
		DyeColor.MAGENTA,
		DyeColor.PINK,
		DyeColor.BROWN,
		DyeColor.BLACK,
		DyeColor.GRAY,
		DyeColor.LIGHT_GRAY,
		DyeColor.WHITE
	);

	private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

	public ShulkerReaderBlockEntity(BlockPos pos, BlockState state) {
		super(ShulkerReaderBlockEntities.SHULKER_READER, pos, state);
	}

	@Override
	protected Component getDefaultName() {
		return Component.literal("Shulker Reader");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> stacks) {
		for (int i = 0; i < inventory.size(); i++) {
			inventory.set(i, ItemStack.EMPTY);
		}
		for (int i = 0; i < stacks.size() && i < inventory.size(); i++) {
			inventory.set(i, stacks.get(i));
		}
		setChanged();
	}

	@Override
	public int getContainerSize() {
		return inventory.size();
	}

	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new VanillaShulkerReaderScreenHandler(syncId, playerInventory, this, this.worldPosition);
	}

	public ExtendedMenuProvider<BlockPos> getNativeMenuProvider() {
		return new ExtendedMenuProvider<>() {
			@Override
			public BlockPos getScreenOpeningData(ServerPlayer player) {
				return ShulkerReaderBlockEntity.this.worldPosition;
			}

			@Override
			public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
				return new ShulkerReaderScreenHandler(
					syncId,
					playerInventory,
					ShulkerReaderBlockEntity.this,
					ShulkerReaderBlockEntity.this.worldPosition
				);
			}

			@Override
			public Component getDisplayName() {
				return ShulkerReaderBlockEntity.this.getDisplayName();
			}
		};
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, inventory);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		ContainerHelper.loadAllItems(input, inventory);
		if (!inventory.isEmpty() && !inventory.get(0).isEmpty() && !isEmptyShulkerBox(inventory.get(0))) {
			inventory.set(0, ItemStack.EMPTY);
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return AVAILABLE_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == 0 && isEmptyShulkerBox(stack);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot == 0) {
			if (!stack.isEmpty() && !isEmptyShulkerBox(stack)) {
				return;
			}
			ItemStack copy = stack.copy();
			if (copy.getCount() > getMaxStackSize()) {
				copy.setCount(getMaxStackSize());
			}
			inventory.set(slot, copy);
			setChanged();
		}
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack result = super.removeItem(slot, amount);
		if (!result.isEmpty()) {
			setChanged();
		}
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack result = super.removeItemNoUpdate(slot);
		if (!result.isEmpty()) {
			setChanged();
		}
		return result;
	}

	@Override
	public void clearContent() {
		inventory.clear();
		setChanged();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.level != null) {
			this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
		}
	}

	public static boolean isShulkerBoxItem(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		if (stack.getItem() instanceof BlockItem blockItem) {
			return blockItem.getBlock() instanceof ShulkerBoxBlock;
		}
		return false;
	}

	public static boolean isEmptyShulkerBox(ItemStack stack) {
		if (!isShulkerBoxItem(stack)) {
			return false;
		}

		if (stack.has(DataComponents.CONTAINER_LOOT)) {
			return false;
		}

		ItemContainerContents container = stack.get(DataComponents.CONTAINER);
		if (container == null) {
			return true;
		}

		return container.nonEmptyItemCopyStream().findAny().isEmpty();
	}

	public int calculateComparatorOutput(boolean powered) {
		ItemStack stack = inventory.get(0);
		if (!isShulkerBoxItem(stack)) {
			return 0;
		}

		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return 0;
		}

		if (!(blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock)) {
			return 0;
		}

		DyeColor color = shulkerBoxBlock.getColor();
		boolean hasCustomName = stack.has(DataComponents.CUSTOM_NAME);

		if (powered && hasCustomName) {
			return 15;
		}

		if (!powered && hasCustomName) {
			return 0;
		}

		if (!powered) {
			if (color == null) {
				return 1;
			}
			int index = COLOR_ORDER.indexOf(color);
			if (index == -1) {
				return 0;
			}
			if (index <= 13) {
				return index + 2;
			}
			return 0;
		}

		if (color == DyeColor.LIGHT_GRAY) {
			return 1;
		}
		if (color == DyeColor.WHITE) {
			return 2;
		}

		return 0;
	}
}
