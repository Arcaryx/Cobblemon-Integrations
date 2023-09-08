package com.arcaryx.cobblemonintegrations.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractMessage {
    public AbstractMessage(){}

    // Decoder
    public AbstractMessage(FriendlyByteBuf buf) {}

    // Encoder
    public abstract void encode(FriendlyByteBuf buf);

    public abstract void handle(ServerPlayer player);
}
