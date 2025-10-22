package tobymoszer.shulkerreader.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import tobymoszer.shulkerreader.ShulkerReader;

public final class ShulkerReaderScreenHandlers {
	public static final ScreenHandlerType<ShulkerReaderScreenHandler> SHULKER_READER = Registry.register(
		Registries.SCREEN_HANDLER,
		Identifier.of(ShulkerReader.MOD_ID, "shulker_reader"),
		new ExtendedScreenHandlerType<>(ShulkerReaderScreenHandler::new, BlockPos.PACKET_CODEC)
	);

	private ShulkerReaderScreenHandlers() {
	}

	public static void initialize() {
		// Trigger class loading.
	}
}
