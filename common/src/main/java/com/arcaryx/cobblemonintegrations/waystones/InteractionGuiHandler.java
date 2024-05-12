package com.arcaryx.cobblemonintegrations.waystones;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.net.messages.TeleportInteractMessage;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption;
import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation;
import kotlin.Unit;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class InteractionGuiHandler {

    public static void init() {
        CobblemonEvents.POKEMON_INTERACTION_GUI_CREATION.subscribe(Priority.NORMAL, (event) -> {
            if (CobblemonIntegrations.CONFIG.isModLoaded("waystones") && CobblemonIntegrations.CONFIG.allowWaystoneTeleport()) {
                var pokemonOpt = CobblemonClient.INSTANCE.getStorage().getMyParty().getSlots().stream()
                        .filter((x) -> x != null && x.getEntity() != null && x.getEntity().getUUID().equals(event.getPokemonID())).findFirst();
                if (pokemonOpt.isPresent()) {
                    var pokemon = pokemonOpt.get();
                    if (WaystonesHandler.CanUseTeleport(pokemon)) {
                        var teleport = new InteractWheelOption(
                                new ResourceLocation(CobblemonIntegrations.MOD_ID, "textures/gui/icon_teleport.png"),
                                "cobblemonintegrations.ui.interact.teleport",
                                () -> new Vector3f(1f, 1f, 1f),
                                () -> {
                                    CobblemonIntegrations.NETWORK.sendToServer(new TeleportInteractMessage(event.getPokemonID()));
                                    return Unit.INSTANCE;
                                }
                        );
                        event.getOptions().put(Orientation.BOTTOM_LEFT, teleport);
                    }
                }
            }
            return null; // TODO: Is this correct?
        });
    }
}
