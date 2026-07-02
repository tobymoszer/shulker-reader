package tobymoszer.shulkerreader.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import tobymoszer.shulkerreader.ShulkerReader;

public final class ShulkerReaderNetworking {
	private ShulkerReaderNetworking() {
	}

	public static boolean supportsNativeClient(ServerPlayer player) {
		return ServerPlayNetworking.canSend(player, NativeClientPayload.TYPE);
	}

	public record NativeClientPayload() implements CustomPacketPayload {
		public static final NativeClientPayload INSTANCE = new NativeClientPayload();
		public static final Type<NativeClientPayload> TYPE = new Type<>(
			Identifier.fromNamespaceAndPath(ShulkerReader.MOD_ID, "native_client")
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, NativeClientPayload> CODEC =
			StreamCodec.unit(INSTANCE);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}
