package com.arcaryx.cobblemonintegrations.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractMessage {
    public AbstractMessage(){}

    // Decoder
    public AbstractMessage(FriendlyByteBuf buf) {}

    // Encoder
    public abstract void encode(FriendlyByteBuf buf);

    public abstract void handle(Player player);
}
