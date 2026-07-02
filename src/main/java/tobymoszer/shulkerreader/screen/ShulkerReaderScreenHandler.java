package tobymoszer.shulkerreader.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntity;

public class ShulkerReaderScreenHandler extends AbstractContainerMenu {
	private static final int VANILLA_HOPPER_SLOT_COUNT = 5;
	private static final int READER_SLOT = 2;
	private final Container inventory;

	public ShulkerReaderScreenHandler(int syncId, Inventory playerInventory, Container inventory, BlockPos pos) {
		this(syncId, playerInventory, inventory);
	}

	public ShulkerReaderScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
		super(MenuType.HOPPER, syncId);
		checkContainerSize(inventory, 1);
		this.inventory = inventory;
		inventory.startOpen(playerInventory.player);

		SimpleContainer fillerSlots = new SimpleContainer(VANILLA_HOPPER_SLOT_COUNT - 1);
		int fillerIndex = 0;
		for (int index = 0; index < VANILLA_HOPPER_SLOT_COUNT; index++) {
			int x = 44 + index * 18;
			if (index == READER_SLOT) {
				this.addSlot(new Slot(inventory, 0, x, 20) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return ShulkerReaderBlockEntity.isEmptyShulkerBox(stack);
					}
				});
			} else {
				this.addSlot(new Slot(fillerSlots, fillerIndex++, x, 20) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return false;
					}

					@Override
					public boolean mayPickup(Player player) {
						return false;
					}

					@Override
					public boolean isActive() {
						return false;
					}
				});
			}
		}

		addStandardInventorySlots(playerInventory, 8, 51);
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
			int blockSlotEnd = VANILLA_HOPPER_SLOT_COUNT;
			int playerInventoryStart = blockSlotEnd;
			int playerInventoryEnd = playerInventoryStart + 27;
			int hotbarStart = playerInventoryEnd;
			int hotbarEnd = hotbarStart + 9;

			if (index == READER_SLOT) {
				if (!moveItemStackTo(original, playerInventoryStart, hotbarEnd, true)) {
					return ItemStack.EMPTY;
				}
			} else if (ShulkerReaderBlockEntity.isEmptyShulkerBox(original)) {
				if (!moveItemStackTo(original, READER_SLOT, READER_SLOT + 1, false)) {
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
