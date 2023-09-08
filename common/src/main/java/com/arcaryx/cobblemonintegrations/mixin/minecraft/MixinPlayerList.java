package com.arcaryx.cobblemonintegrations.mixin.minecraft;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.data.PokemonDrop;
import com.arcaryx.cobblemonintegrations.net.messages.SyncDropsMessage;
import com.cobblemon.mod.common.api.drop.ItemDropEntry;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import kotlin.ranges.IntRange;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = PlayerList.class)
public abstract class MixinPlayerList {
    @Shadow @Final private List<ServerPlayer> players;

    @Inject(method = "reloadResources",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"), require = 1)
    private void mixinReloadResources(CallbackInfo ci) {
        // TODO: Verify this is even used, since JEI might not refresh categories on reload
        var lootDrops = computeLootDrops();
        for (var player : this.players) {
            CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncDropsMessage(lootDrops));
        }
    }

    @Inject(method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 6), require = 1)
    private void mixinPlaceNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        // TODO: This only needs to be computed once
        var lootDrops = computeLootDrops();
        CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncDropsMessage(lootDrops));
    }

    private static void addDrops(List<PokemonDrop> lootDrops, Species species, FormData form) {
        var formDrops = form.getDrops().getEntries();
        for (var drop : formDrops) {
            if (!(drop instanceof ItemDropEntry itemDrop))
                continue;
            var range = itemDrop.getQuantityRange();
            if (range == null)
                range = new IntRange(itemDrop.getQuantity(), itemDrop.getQuantity());
            var chance = itemDrop.getPercentage() / 100;
            lootDrops.add(new PokemonDrop(species.getResourceIdentifier(), form.getName(), itemDrop.getItem(), chance, range.getFirst(), range.getLast()));
        }
    }

    private static List<PokemonDrop> computeLootDrops() {
        List<PokemonDrop> lootDrops = new ArrayList<>();
        var pokemonSpecies = PokemonSpecies.INSTANCE.getSpecies();
        for (var species : pokemonSpecies) {
            var forms = species.getForms();
            if (forms.isEmpty()) {
                var form = species.getStandardForm();
                addDrops(lootDrops, species, form);
                continue;
            }
            for (var form : species.getForms()) {
                addDrops(lootDrops, species, form);
            }
        }
        return lootDrops;
    }
}
