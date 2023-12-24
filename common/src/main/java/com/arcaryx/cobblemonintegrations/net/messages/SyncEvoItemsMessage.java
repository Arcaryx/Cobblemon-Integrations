package com.arcaryx.cobblemonintegrations.net.messages;

import com.arcaryx.cobblemonintegrations.data.ClientCache;
import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import com.cobblemon.mod.common.api.conditional.RegistryLikeAdapter;
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition;
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition;
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import com.cobblemon.mod.common.registry.BiomeIdentifierCondition;
import com.cobblemon.mod.common.registry.BiomeTagCondition;
import com.cobblemon.mod.common.registry.ItemIdentifierCondition;
import com.cobblemon.mod.common.registry.ItemTagCondition;
import com.cobblemon.mod.common.util.adapters.ItemLikeConditionAdapter;
import com.cobblemon.mod.common.util.adapters.TagKeyAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import kotlin.jvm.functions.Function1;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SyncEvoItemsMessage extends AbstractMessage {
    public List<PokemonItemEvo> itemEvos;

    public SyncEvoItemsMessage(List<PokemonItemEvo> itemEvos) {
        super();
        this.itemEvos = itemEvos;
    }

    private FriendlyByteBuf buf;

    public class ItemIdentifierConditionAdapter implements JsonSerializer<ItemIdentifierCondition> {
        @Override
        public JsonElement serialize(ItemIdentifierCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getIdentifier().toString());
        }
    }

    public class ItemTagConditionAdapter implements JsonSerializer<ItemTagCondition> {
        @Override
        public JsonElement serialize(ItemTagCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTag().location().toString());
        }
    }


    // Decoder
    public SyncEvoItemsMessage(FriendlyByteBuf buf) {
        super(buf);
        this.buf = buf;
    }

    // Encoder
    public void encode(FriendlyByteBuf buf) {
        var gson = PokemonSpecies.INSTANCE.getGson().newBuilder()
                .registerTypeAdapter(ItemIdentifierCondition.class, new ItemIdentifierConditionAdapter())
                .registerTypeAdapter(ItemTagCondition.class, new ItemTagConditionAdapter())
                .create();
        buf.writeInt(itemEvos.size());
        for (var itemEvo : itemEvos) {
            buf.writeResourceLocation(itemEvo.getSpecies());
            buf.writeUtf(itemEvo.getForm());
            buf.writeUtf(gson.toJson(itemEvo.getItemEvo(), Evolution.class));
        }
    }

    public void handle(Player player) {
        ClientCache.setEvoItemsMessage(this);
    }

    public void lateDecode() {
        // Late decode to handle race condition (Caused by loading the ContextEvolution types later in data sync?)
        var gson = PokemonSpecies.INSTANCE.getGson();
        itemEvos = new ArrayList<>();
        int size = buf.readInt();
        itemEvos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            var species = buf.readResourceLocation();
            var form = buf.readUtf();
            var itemEvo = gson.fromJson(buf.readUtf(), ItemInteractionEvolution.class);
            itemEvos.add(new PokemonItemEvo(species, form, itemEvo));
        }
    }
}
