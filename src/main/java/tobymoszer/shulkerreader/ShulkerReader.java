package tobymoszer.shulkerreader;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import tobymoszer.shulkerreader.block.entity.ShulkerReaderBlockEntities;

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
		enableAutoHostByDefault();
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		ShulkerReaderBlocks.initialize();
		ShulkerReaderBlockEntities.initialize();
		CreativeModeTabEvents.modifyOutputEvent(REDSTONE_BLOCKS_TAB).register(entries -> entries.accept(ShulkerReaderBlocks.SHULKER_READER));
		LOGGER.info("Shulker Reader initialized with vanilla-client compatibility");
	}

	private static void enableAutoHostByDefault() {
		Path configPath = FabricLoader.getInstance()
			.getConfigDir()
			.resolve("polymer")
			.resolve("auto-host.json");
		if (Files.exists(configPath)) {
			return;
		}

		try {
			Files.createDirectories(configPath.getParent());
			Files.writeString(
				configPath,
				"""
				{
				  "enabled": true,
				  "required": false,
				  "type": "polymer:automatic",
				  "settings": {}
				}
				""",
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE_NEW
			);
		} catch (IOException exception) {
			LOGGER.warn("Could not create the default Polymer AutoHost configuration", exception);
		}
	}
}
