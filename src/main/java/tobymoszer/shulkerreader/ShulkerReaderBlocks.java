package tobymoszer.shulkerreader;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import tobymoszer.shulkerreader.block.ShulkerReaderBlock;

public final class ShulkerReaderBlocks {
	public static final Block SHULKER_READER = register(
		"shulker_reader",
		ShulkerReaderBlock::new,
		BlockBehaviour.Properties.of()
			.sound(SoundType.STONE)
			.strength(2.0f)
			.requiresCorrectToolForDrops(),
		true
	);

	private ShulkerReaderBlocks() {
	}

	public static void initialize() {
		// Force class loading for static registration.
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		ResourceKey<Block> blockKey = keyOfBlock(name);
		Block block = blockFactory.apply(settings.setId(blockKey));

		if (shouldRegisterItem) {
			ResourceKey<Item> itemKey = keyOfItem(name);
			BlockItem blockItem = new ShulkerReaderBlockItem(
				block,
				new Item.Properties()
					.setId(itemKey)
					.useBlockDescriptionPrefix()
					.component(DataComponents.ITEM_NAME, Component.literal("Shulker Reader"))
			);
			Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
		}

		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	private static ResourceKey<Block> keyOfBlock(String name) {
		return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ShulkerReader.MOD_ID, name));
	}

	private static ResourceKey<Item> keyOfItem(String name) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ShulkerReader.MOD_ID, name));
	}
}
