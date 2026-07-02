package tobymoszer.shulkerreader;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
	public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(
		BlockState state,
		ServerPlayer player,
		InteractionHand hand,
		ItemStack stack,
		ServerLevel world,
		BlockHitResult hitResult
	) {
		return !ShulkerReaderNetworking.supportsNativeClient(player);
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
