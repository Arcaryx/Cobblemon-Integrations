package com.arcaryx.cobblemonintegrations.net;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.net.messages.AbstractMessage;
import com.arcaryx.cobblemonintegrations.net.messages.SyncDropsMessage;
import com.arcaryx.cobblemonintegrations.net.messages.SyncEvoItemsMessage;
import com.arcaryx.cobblemonintegrations.net.messages.TeleportInteractMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractNetworkHandler {
    public void registerMessagesClient() {
        registerMessageClient(SyncDropsMessage.class, SyncDropsMessage::encode, SyncDropsMessage::new);
        registerMessageClient(SyncEvoItemsMessage.class, SyncEvoItemsMessage::encode, SyncEvoItemsMessage::new);
    }
    public void registerMessagesServer() {
        if (CobblemonIntegrations.CONFIG.isModLoaded("waystones")) {
            registerMessageServer(TeleportInteractMessage.class, TeleportInteractMessage::encode, TeleportInteractMessage::new);
        }
    }

    public abstract <T extends AbstractMessage> void registerMessageClient(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder);
    public abstract <T extends AbstractMessage> void registerMessageServer(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder);
    public abstract void sendToPlayer(ServerPlayer player, AbstractMessage message);
    public abstract void sendToServer(AbstractMessage message);
}
