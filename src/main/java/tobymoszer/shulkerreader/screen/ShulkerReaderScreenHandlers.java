package tobymoszer.shulkerreader.screen;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

import tobymoszer.shulkerreader.ShulkerReader;

public final class ShulkerReaderScreenHandlers {
	public static final MenuType<ShulkerReaderScreenHandler> SHULKER_READER = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(ShulkerReader.MOD_ID, "shulker_reader"),
		new ExtendedMenuType<>(ShulkerReaderScreenHandler::new, BlockPos.STREAM_CODEC)
	);

	private ShulkerReaderScreenHandlers() {
	}

	public static void initialize() {
		// Trigger class loading.
	}
}
