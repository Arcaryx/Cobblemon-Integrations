package com.arcaryx.cobblemonintegrations.forge;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.net.AbstractNetworkHandler;
import com.arcaryx.cobblemonintegrations.net.messages.AbstractMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ForgeNetworkHandler extends AbstractNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CobblemonIntegrations.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int ID = 0;

    @Override
    public <T extends AbstractMessage> void registerMessageClient(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder) {
        INSTANCE.registerMessage(ID++, type, encoder, decoder, (msg, context) -> {
            context.get().enqueueWork(() -> msg.handle(context.get().getSender()));
            context.get().setPacketHandled(true);
        });
    }

    @Override
    public void sendToPlayer(ServerPlayer player, AbstractMessage message) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
