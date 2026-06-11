package tobymoszer.shulkerreader.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntity;

public class ShulkerReaderScreenHandler extends AbstractContainerMenu {
	private final Container inventory;

	public ShulkerReaderScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
		this(syncId, playerInventory, getClientInventory(playerInventory, pos));
	}

	public ShulkerReaderScreenHandler(int syncId, Inventory playerInventory, Container inventory, BlockPos pos) {
		this(syncId, playerInventory, inventory);
	}

	public ShulkerReaderScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
		super(ShulkerReaderScreenHandlers.SHULKER_READER, syncId);
		checkContainerSize(inventory, 1);
		this.inventory = inventory;
		inventory.startOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 80, 20) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return ShulkerReaderBlockEntity.isEmptyShulkerBox(stack);
			}
		});

		addStandardInventorySlots(playerInventory, 8, 51);
	}

	private static Container getClientInventory(Inventory playerInventory, BlockPos pos) {
		Level level = playerInventory.player.level();
		if (level != null) {
			return level.getBlockEntity(pos) instanceof ShulkerReaderBlockEntity blockEntity ? blockEntity : new SimpleContainer(1);
		}
		return new SimpleContainer(1);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.inventory.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack original = slot.getItem();
			newStack = original.copy();
			int blockSlotEnd = 1;
			int playerInventoryStart = blockSlotEnd;
			int playerInventoryEnd = playerInventoryStart + 27;
			int hotbarStart = playerInventoryEnd;
			int hotbarEnd = hotbarStart + 9;

			if (index == 0) {
				if (!moveItemStackTo(original, playerInventoryStart, hotbarEnd, true)) {
					return ItemStack.EMPTY;
				}
			} else if (ShulkerReaderBlockEntity.isEmptyShulkerBox(original)) {
				if (!moveItemStackTo(original, 0, blockSlotEnd, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= playerInventoryStart && index < playerInventoryEnd) {
				if (!moveItemStackTo(original, hotbarStart, hotbarEnd, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= hotbarStart && index < hotbarEnd) {
				if (!moveItemStackTo(original, playerInventoryStart, playerInventoryEnd, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (original.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return newStack;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.inventory.stopOpen(player);
	}
}
