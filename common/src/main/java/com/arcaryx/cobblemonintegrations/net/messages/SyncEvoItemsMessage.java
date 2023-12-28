package com.arcaryx.cobblemonintegrations.net.messages;

import com.arcaryx.cobblemonintegrations.data.ClientCache;
import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition;
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition;
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SyncEvoItemsMessage extends AbstractMessage {
    public List<PokemonItemEvo> itemEvos;

    public SyncEvoItemsMessage(List<PokemonItemEvo> itemEvos) {
        super();
        this.itemEvos = itemEvos;
    }

    public static class SerializerFix<B> implements JsonSerializer<RegistryLikeCondition<B>> {
        @Override
        public JsonElement serialize(RegistryLikeCondition<B> src, Type typeOfSrc, JsonSerializationContext context) {
            if (src instanceof RegistryLikeIdentifierCondition<B> src1) {
                return new JsonPrimitive(src1.getIdentifier().toString());
            }
            return new JsonPrimitive(RegistryLikeTagCondition.PREFIX + ((RegistryLikeTagCondition<B>)src).getTag().location());
        }
    }


    // Decoder
    public SyncEvoItemsMessage(FriendlyByteBuf buf) {
        super(buf);
        int size = buf.readInt();
        itemEvos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            var species = buf.readResourceLocation();
            var form = buf.readUtf();
            var itemEvo = buf.readUtf();
            int aspectsCount = buf.readInt();
            var aspects = new ArrayList<String>();
            for (int j = 0; j < aspectsCount; j++) {
                aspects.add(buf.readUtf());
            }
            itemEvos.add(new PokemonItemEvo(species, form, itemEvo, aspects));
        }
    }
    
    // Encoder
    public void encode(FriendlyByteBuf buf) {
        var gson = PokemonSpecies.INSTANCE.getGson().newBuilder()
                .registerTypeHierarchyAdapter(RegistryLikeCondition.class, new SerializerFix<>())
                .create();
        buf.writeInt(itemEvos.size());
        for (var itemEvo : itemEvos) {
            buf.writeResourceLocation(itemEvo.getSpecies());
            buf.writeUtf(itemEvo.getForm());
            buf.writeUtf(gson.toJson(itemEvo.getItemEvo(), ItemInteractionEvolution.class));
            buf.writeInt(itemEvo.getResult().size());
            for (var aspect : itemEvo.getResult()) {
                buf.writeUtf(aspect);
            }
        }
    }

    public void handle(Player player) {
        ClientCache.setPokemonItemEvos(itemEvos);
    }
}
