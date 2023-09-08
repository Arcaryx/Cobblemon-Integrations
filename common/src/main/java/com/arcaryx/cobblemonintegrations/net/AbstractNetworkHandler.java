package com.arcaryx.cobblemonintegrations.net;

import com.arcaryx.cobblemonintegrations.net.messages.AbstractMessage;
import com.arcaryx.cobblemonintegrations.net.messages.SyncDropsMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractNetworkHandler {
    public void registerMessages() {
        registerMessageClient(SyncDropsMessage.class, SyncDropsMessage::encode, SyncDropsMessage::new);
    }
    public abstract <T extends AbstractMessage> void registerMessageClient(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder);
    public abstract void sendToPlayer(ServerPlayer player, AbstractMessage message);
}
