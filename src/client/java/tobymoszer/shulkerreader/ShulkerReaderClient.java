package tobymoszer.shulkerreader;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;

import tobymoszer.shulkerreader.network.ShulkerReaderNetworking;
import tobymoszer.shulkerreader.screen.ShulkerReaderScreen;
import tobymoszer.shulkerreader.screen.ShulkerReaderScreenHandlers;

public class ShulkerReaderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ShulkerReaderScreenHandlers.SHULKER_READER, ShulkerReaderScreen::new);
		ClientPlayNetworking.registerGlobalReceiver(
			ShulkerReaderNetworking.NativeClientPayload.TYPE,
			(payload, context) -> {
			}
		);
	}
}
