package tobymoszer.shulkerreader;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

import tobymoszer.shulkerreader.screen.ShulkerReaderScreen;
import tobymoszer.shulkerreader.screen.ShulkerReaderScreenHandlers;

public class ShulkerReaderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ShulkerReaderScreenHandlers.SHULKER_READER, ShulkerReaderScreen::new);
	}
}
