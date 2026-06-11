package tobymoszer.shulkerreader.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import tobymoszer.shulkerreader.ShulkerReader;
import tobymoszer.shulkerreader.ShulkerReaderBlocks;

public final class ShulkerReaderBlockEntities {
	public static final BlockEntityType<ShulkerReaderBlockEntity> SHULKER_READER = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(ShulkerReader.MOD_ID, "shulker_reader"),
		FabricBlockEntityTypeBuilder.create(ShulkerReaderBlockEntity::new, ShulkerReaderBlocks.SHULKER_READER).build()
	);

	private ShulkerReaderBlockEntities() {
	}

	public static void initialize() {
		// Ensure the class is loaded.
	}
}
