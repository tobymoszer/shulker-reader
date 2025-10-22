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

		this.addSlot(new Slot(inventory, 0, 80, 35) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return ShulkerReaderBlockEntity.isShulkerBox(stack);
			}
		});

		addPlayerInventory(playerInventory);
	}

	private static Inventory getClientInventory(PlayerInventory playerInventory, BlockPos pos) {
		World world = playerInventory.player.getWorld();
		if (world != null) {
			return world.getBlockEntity(pos) instanceof ShulkerReaderBlockEntity blockEntity ? blockEntity : new SimpleInventory(1);
		}
		return new SimpleInventory(1);
	}

	private void addPlayerInventory(PlayerInventory playerInventory) {
		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				int x = 8 + column * 18;
				int y = 84 + row * 18;
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, x, y));
			}
		}

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
		}
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
			if (index == 0) {
				if (!insertItem(original, 1, 37, true)) {
					return ItemStack.EMPTY;
				}
			} else if (ShulkerReaderBlockEntity.isShulkerBox(original)) {
				if (!insertItem(original, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 1 && index < 28) {
				if (!insertItem(original, 28, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 28 && index < 37) {
				if (!insertItem(original, 1, 28, false)) {
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
