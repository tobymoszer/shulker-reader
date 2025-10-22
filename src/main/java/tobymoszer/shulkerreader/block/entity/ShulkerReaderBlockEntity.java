package tobymoszer.shulkerreader.block.entity;

import java.util.List;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DyeColor;
import net.minecraft.component.DataComponentTypes;

import tobymoszer.shulkerreader.screen.ShulkerReaderScreenHandler;

public class ShulkerReaderBlockEntity extends LockableContainerBlockEntity implements SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
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

	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

	public ShulkerReaderBlockEntity(BlockPos pos, BlockState state) {
		super(ShulkerReaderBlockEntities.SHULKER_READER, pos, state);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.shulkerreader.shulker_reader");
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> stacks) {
		for (int i = 0; i < inventory.size(); i++) {
			inventory.set(i, ItemStack.EMPTY);
		}
		for (int i = 0; i < stacks.size() && i < inventory.size(); i++) {
			inventory.set(i, stacks.get(i));
		}
		markDirty();
	}

	@Override
	public int size() {
		return inventory.size();
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new ShulkerReaderScreenHandler(syncId, playerInventory, this, this.pos);
	}

	@Override
	protected void writeData(net.minecraft.storage.WriteView view) {
		super.writeData(view);
		Inventories.writeData(view, inventory);
	}

	@Override
	protected void readData(net.minecraft.storage.ReadView view) {
		super.readData(view);
		Inventories.readData(view, inventory);
		if (!inventory.isEmpty() && !inventory.get(0).isEmpty() && !isShulkerBox(inventory.get(0))) {
			inventory.set(0, ItemStack.EMPTY);
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return AVAILABLE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return slot == 0 && isShulkerBox(stack);
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot == 0) {
			if (!stack.isEmpty() && !isShulkerBox(stack)) {
				return;
			}
			ItemStack copy = stack.copy();
			if (copy.getCount() > getMaxCountPerStack()) {
				copy.setCount(getMaxCountPerStack());
			}
			inventory.set(slot, copy);
			markDirty();
		}
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = super.removeStack(slot, amount);
		if (!result.isEmpty()) {
			markDirty();
		}
		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack result = super.removeStack(slot);
		if (!result.isEmpty()) {
			markDirty();
		}
		return result;
	}

	@Override
	public void clear() {
		inventory.clear();
		markDirty();
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (this.world != null) {
			this.world.updateComparators(this.pos, this.getCachedState().getBlock());
		}
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
		return this.pos;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return createScreenHandler(syncId, playerInventory);
	}

	public static boolean isShulkerBox(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		if (stack.getItem() instanceof BlockItem blockItem) {
			return blockItem.getBlock() instanceof net.minecraft.block.ShulkerBoxBlock;
		}
		return false;
	}

	public int calculateComparatorOutput(boolean powered) {
		ItemStack stack = inventory.get(0);
		if (!isShulkerBox(stack)) {
			return 0;
		}

		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return 0;
		}

		if (!(blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock)) {
			return 0;
		}

		DyeColor color = shulkerBoxBlock.getColor();
		boolean hasCustomName = stack.contains(DataComponentTypes.CUSTOM_NAME);

		if (powered && hasCustomName) {
			return 15;
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
