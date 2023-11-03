package com.arcaryx.cobblemonintegrations.mixin.minecraft;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.data.PokemonDrop;
import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import com.arcaryx.cobblemonintegrations.net.messages.SyncDropsMessage;
import com.arcaryx.cobblemonintegrations.net.messages.SyncEvoItemsMessage;
import com.cobblemon.mod.common.api.drop.ItemDropEntry;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import kotlin.ranges.IntRange;
import net.minecraft.core.registries.BuiltInRegistries;
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
        var itemEvos = computeItemEvos();
        for (var player : this.players) {
            CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncDropsMessage(lootDrops));
            CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncEvoItemsMessage(itemEvos));
        }

    }

    @Inject(method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 6), require = 1)
    private void mixinPlaceNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        // TODO: This only needs to be computed once
        var lootDrops = computeLootDrops();
        var itemEvos = computeItemEvos();
        CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncDropsMessage(lootDrops));
        CobblemonIntegrations.NETWORK.sendToPlayer(player, new SyncEvoItemsMessage(itemEvos));
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

    private static List<PokemonItemEvo> computeItemEvos() {
        List<PokemonItemEvo> itemEvos = new ArrayList<>();
        for (var species : PokemonSpecies.INSTANCE.getSpecies()) {
            var forms = species.getForms().isEmpty() ? List.of(species.getStandardForm()) : species.getForms();
            for (var form : forms) {
                for (var evolution : form.getEvolutions()) {
                    if (evolution instanceof ItemInteractionEvolution itemEvolution) {
                        var evoResult = evolution.getResult().getSpecies();
                        if (evoResult == null) {
                            CobblemonIntegrations.LOGGER.warn("Null evolution result from species: " + species.getName());
                            continue;
                        }
                        var speciesEvo = PokemonSpecies.INSTANCE.getByName(evoResult);
                        FormData formEvo;
                        if (itemEvolution.getResult().getForm() != null) {
                            var speciesForms = speciesEvo.getForms().isEmpty() ? List.of(speciesEvo.getStandardForm()) : species.getForms();
                            formEvo = speciesForms.stream().filter(x -> x.getName().equalsIgnoreCase(evolution.getResult().getForm())).findFirst().orElse(speciesEvo.getStandardForm());
                        } else {
                            formEvo = speciesEvo.getForm(evolution.getResult().getAspects());
                        }
                        var itemReqs = itemEvolution.getRequiredContext().getItem();
                        var validItems = BuiltInRegistries.ITEM.stream().filter(x -> itemReqs.fits(x, BuiltInRegistries.ITEM)).toList();
                        if (!validItems.isEmpty()) {
                            itemEvos.add(new PokemonItemEvo(species.getResourceIdentifier(), form.getName(), speciesEvo.getResourceIdentifier(), formEvo.getName(), validItems.stream().map(BuiltInRegistries.ITEM::getKey).toList()));
                        }
                    }
                }
            }
        }
        return itemEvos;
    }

}
