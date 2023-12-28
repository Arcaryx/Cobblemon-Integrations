package com.arcaryx.cobblemonintegrations.mixin.minecraft;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.net.messages.SyncDropsMessage;
import com.arcaryx.cobblemonintegrations.net.messages.SyncEvoItemsMessage;
import com.arcaryx.cobblemonintegrations.util.PokemonUtils;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = PlayerList.class)
public abstract class MixinPlayerList {
    @Shadow @Final private List<ServerPlayer> players;

    @Inject(method = "reloadResources",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"), require = 1)
    private void mixinReloadResources(CallbackInfo ci) {
        // TODO: Verify this is even used, since JEI might not refresh categories on reload
        var lootDrops = PokemonUtils.computeLootDrops();
        var itemEvos = PokemonUtils.computeItemEvos();
        for (var player : this.players) {
            CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncDropsMessage(lootDrops));
            CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncEvoItemsMessage(itemEvos));
        }

    }

    @Inject(method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 6), require = 1)
    private void mixinPlaceNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        // TODO: This only needs to be computed once
        var lootDrops = PokemonUtils.computeLootDrops();
        var itemEvos = PokemonUtils.computeItemEvos();
        CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncDropsMessage(lootDrops));
        CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncEvoItemsMessage(itemEvos));
    }
}
