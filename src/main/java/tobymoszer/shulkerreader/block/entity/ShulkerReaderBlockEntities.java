package tobymoszer.shulkerreader.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import tobymoszer.shulkerreader.ShulkerReader;
import tobymoszer.shulkerreader.ShulkerReaderBlocks;

public final class ShulkerReaderBlockEntities {
	public static final BlockEntityType<ShulkerReaderBlockEntity> SHULKER_READER = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		Identifier.of(ShulkerReader.MOD_ID, "shulker_reader"),
		FabricBlockEntityTypeBuilder.create(ShulkerReaderBlockEntity::new, ShulkerReaderBlocks.SHULKER_READER).build()
	);

	private ShulkerReaderBlockEntities() {
	}

	public static void initialize() {
		// Ensure the class is loaded.
	}
}
