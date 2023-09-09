package com.arcaryx.cobblemonintegrations.net.messages;

import com.arcaryx.cobblemonintegrations.data.ClientCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.arcaryx.cobblemonintegrations.data.PokemonDrop;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncDropsMessage extends AbstractMessage {
    public List<PokemonDrop> drops;

    public SyncDropsMessage(List<PokemonDrop> drops) {
        super();
        this.drops = drops;
    }

    // Decoder
    public SyncDropsMessage(FriendlyByteBuf buf) {
        super(buf);
        int size = buf.readInt();
        drops = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation species = buf.readResourceLocation();
            String form = buf.readUtf();
            ResourceLocation item = buf.readResourceLocation();
            float chance = buf.readFloat();
            int min = buf.readInt();
            int max = buf.readInt();
            drops.add(new PokemonDrop(species, form, item, chance, min, max));
        }
    }

    // Encoder
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(drops.size());
        for (PokemonDrop drop : drops) {
            buf.writeResourceLocation(drop.getSpecies());
            buf.writeUtf(drop.getForm());
            buf.writeResourceLocation(drop.getItem());
            buf.writeFloat(drop.getChance());
            buf.writeInt(drop.getRange().getFirst());
            buf.writeInt(drop.getRange().getLast());
        }
    }

    public void handle(Player player) {
        ClientCache.setPokemonDrops(drops);
    }
}
