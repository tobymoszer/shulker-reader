package tobymoszer.shulkerreader;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import tobymoszer.shulkerreader.network.ShulkerReaderNetworking;

public final class ShulkerReaderBlockItem extends PolymerBlockItem implements PolymerClientDecoded {
	public ShulkerReaderBlockItem(Block block, Properties properties) {
		super(block, properties, Items.TARGET, true);
	}

	@Override
	public Item getPolymerItem(ItemStack stack, PacketContext context) {
		if (ShulkerReaderNetworking.supportsNativeClient(context)) {
			return this;
		}
		return super.getPolymerItem(stack, context);
	}

	@Override
	public Identifier getPolymerItemModel(
		ItemStack stack,
		PacketContext context,
		HolderLookup.Provider lookup
	) {
		if (ShulkerReaderNetworking.supportsNativeClient(context)) {
			return null;
		}
		if (!PolymerResourcePackUtils.hasMainPack(context)) {
			return null;
		}
		return super.getPolymerItemModel(stack, context, lookup);
	}
}
