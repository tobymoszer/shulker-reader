package tobymoszer.shulkerreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntities;
import tobymoszer.shulkerreader.screen.ShulkerReaderScreenHandlers;

public class ShulkerReader implements ModInitializer {
	public static final String MOD_ID = "shulkerreader";
	private static final ResourceKey<CreativeModeTab> REDSTONE_BLOCKS_TAB = ResourceKey.create(
		Registries.CREATIVE_MODE_TAB,
		Identifier.withDefaultNamespace("redstone_blocks")
	);

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ShulkerReaderBlocks.initialize();
		ShulkerReaderBlockEntities.initialize();
		ShulkerReaderScreenHandlers.initialize();
		CreativeModeTabEvents.modifyOutputEvent(REDSTONE_BLOCKS_TAB).register(entries -> entries.accept(ShulkerReaderBlocks.SHULKER_READER));
		LOGGER.info("Hello Fabric world!");
	}
}
