package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.net.messages.TeleportInteractMessage;
import com.arcaryx.cobblemonintegrations.waystones.WaystonesHandler;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelGUI;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelGuiFactoryKt;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption;
import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation;
import kotlin.Unit;
import kotlin.Pair;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.UUID;

@Mixin(value = InteractWheelGuiFactoryKt.class)
abstract class MixinInteractWheelGuiFactory {
    @Inject(method = "createPokemonInteractGui", require = 1,
            at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/network/chat/Component;)Lcom/cobblemon/mod/common/client/gui/interact/wheel/InteractWheelGUI;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void mixinCreatePokemonInteractGui(UUID pokemonID, boolean canMountShoulder, CallbackInfoReturnable<InteractWheelGUI> cir, InteractWheelOption mountShoulder, InteractWheelOption giveItem, Map<Orientation, InteractWheelOption> options, Pair[] arrayOfPair) {
        if (CobblemonIntegrations.CONFIG.isModLoaded("waystones") && CobblemonIntegrations.CONFIG.allowWaystoneTeleport()) {
            var pokemonOpt = CobblemonClient.INSTANCE.getStorage().getMyParty().getSlots().stream()
                    .filter((x) -> x != null && x.getEntity() != null && x.getEntity().getUUID().equals(pokemonID)).findFirst();
            if (pokemonOpt.isPresent()) {
                var pokemon = pokemonOpt.get();
                if (WaystonesHandler.CanUseTeleport(pokemon)) {
                    var teleport = new InteractWheelOption(
                            new ResourceLocation(CobblemonIntegrations.MOD_ID, "textures/gui/icon_teleport.png"),
                            () -> new Vector3f(1f, 1f, 1f),
                            () -> {
                                CobblemonIntegrations.NETWORK.sendToServer(new TeleportInteractMessage(pokemonID));
                                return Unit.INSTANCE;
                            }
                    );
                    options.put(Orientation.BOTTOM_LEFT, teleport);
                }
            }
        }
    }
}
