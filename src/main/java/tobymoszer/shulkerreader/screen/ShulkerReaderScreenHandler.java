package tobymoszer.shulkerreader.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntity;

public class ShulkerReaderScreenHandler extends ScreenHandler {
	private final Inventory inventory;

	public ShulkerReaderScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
		this(syncId, playerInventory, getClientInventory(playerInventory, pos));
	}

	public ShulkerReaderScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BlockPos pos) {
		this(syncId, playerInventory, inventory);
	}

	public ShulkerReaderScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		super(ShulkerReaderScreenHandlers.SHULKER_READER, syncId);
		checkSize(inventory, 1);
		this.inventory = inventory;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 80, 20) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return ShulkerReaderBlockEntity.isShulkerBox(stack);
			}
		});

		addPlayerSlots(playerInventory, 8, 51);
	}

	private static Inventory getClientInventory(PlayerInventory playerInventory, BlockPos pos) {
		World world = playerInventory.player.getWorld();
		if (world != null) {
			return world.getBlockEntity(pos) instanceof ShulkerReaderBlockEntity blockEntity ? blockEntity : new SimpleInventory(1);
		}
		return new SimpleInventory(1);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack original = slot.getStack();
			newStack = original.copy();
			int blockSlotEnd = 1;
			int playerInventoryStart = blockSlotEnd;
			int playerInventoryEnd = playerInventoryStart + 27;
			int hotbarStart = playerInventoryEnd;
			int hotbarEnd = hotbarStart + 9;

			if (index == 0) {
				if (!insertItem(original, playerInventoryStart, hotbarEnd, true)) {
					return ItemStack.EMPTY;
				}
			} else if (ShulkerReaderBlockEntity.isShulkerBox(original)) {
				if (!insertItem(original, 0, blockSlotEnd, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= playerInventoryStart && index < playerInventoryEnd) {
				if (!insertItem(original, hotbarStart, hotbarEnd, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= hotbarStart && index < hotbarEnd) {
				if (!insertItem(original, playerInventoryStart, playerInventoryEnd, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (original.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return newStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.inventory.onClose(player);
	}
}
