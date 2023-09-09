package com.arcaryx.cobblemonintegrations.net.messages;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Objects;
import java.util.UUID;

public class TeleportInteractMessage extends AbstractMessage {
    public UUID pokemonID;

    public TeleportInteractMessage(UUID pokemonID) {
        super();
        this.pokemonID = pokemonID;
    }

    // Decoder
    public TeleportInteractMessage(FriendlyByteBuf buf) {
        super(buf);
        this.pokemonID = buf.readUUID();
    }


    // Encoder
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.pokemonID);
    }

    @Override
    public void handle(Player player) {
        if (!CobblemonIntegrations.CONFIG.allowWaystoneTeleport()) {
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var entity = serverPlayer.serverLevel().getEntity(pokemonID);
        if (!(entity instanceof PokemonEntity pokemonEntity)) {
            return;
        }
        var pokemon = pokemonEntity.getPokemon();
        var hasTeleportAccessible = pokemon.getAllAccessibleMoves().stream().anyMatch((x) -> x.getName().equals("teleport"));
        var isPsychicType = Objects.equals(pokemon.getForm().getPrimaryType(), ElementalTypes.INSTANCE.getPSYCHIC()) ||
                Objects.equals(pokemon.getForm().getSecondaryType(), ElementalTypes.INSTANCE.getPSYCHIC());

        if (pokemon.getLevel() < CobblemonIntegrations.CONFIG.waystoneMinTeleportLevel()) {
            return;
        }
        if (!hasTeleportAccessible && (CobblemonIntegrations.CONFIG.requireTeleportMove() || !isPsychicType)) {
            return;
        }

        Balm.getNetworking().openGui(player, containerProvider);
    }

    private static final BalmMenuProvider containerProvider = new BalmMenuProvider() {
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.waystones.waystone_selection");
        }

        @Override
        public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return WaystoneSelectionMenu.createWaystoneSelection(i, playerEntity, WarpMode.CUSTOM, null);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            buf.writeByte(WarpMode.CUSTOM.ordinal());
        }
    };
}
