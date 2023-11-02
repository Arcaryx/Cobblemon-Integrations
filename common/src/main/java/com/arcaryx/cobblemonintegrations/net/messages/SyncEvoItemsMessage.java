package com.arcaryx.cobblemonintegrations.net.messages;

import com.arcaryx.cobblemonintegrations.data.ClientCache;
import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncEvoItemsMessage extends AbstractMessage {
    public List<PokemonItemEvo> itemEvos;

    public SyncEvoItemsMessage(List<PokemonItemEvo> itemEvos) {
        super();
        this.itemEvos = itemEvos;
    }

    // Decoder
    public SyncEvoItemsMessage(FriendlyByteBuf buf) {
        super(buf);
        int size = buf.readInt();
        itemEvos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation speciesBase = buf.readResourceLocation();
            String formBase = buf.readUtf();
            ResourceLocation speciesEvo = buf.readResourceLocation();
            String formEvo = buf.readUtf();
            int validCount = buf.readInt();
            List<ResourceLocation> validItems = new ArrayList<>();
            for (int j = 0; j < validCount; j++) {
                validItems.add(buf.readResourceLocation());
            }
            itemEvos.add(new PokemonItemEvo(speciesBase, formBase, speciesEvo, formEvo, validItems));
        }
    }

    // Encoder
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(itemEvos.size());
        for (var itemEvo : itemEvos) {
            buf.writeResourceLocation(itemEvo.speciesBase);
            buf.writeUtf(itemEvo.formBase);
            buf.writeResourceLocation(itemEvo.speciesEvo);
            buf.writeUtf(itemEvo.formEvo);
            var validItems = itemEvo.validItems;
            buf.writeInt(validItems.size());
            for (var validItem : validItems) {
                buf.writeResourceLocation(validItem);
            }
        }
    }

    public void handle(Player player) {
        ClientCache.setPokemonItemEvos(itemEvos);
    }
}
