package com.arcaryx.cobblemonintegrations.util;

import com.cobblemon.mod.common.client.keybind.CurrentKeyAccessorKt;
import com.cobblemon.mod.common.client.keybind.keybinds.PartySendBinding;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ClientUtils {

    public static MutableComponent CreateBattleHint() {
        var component = Component.literal("<press ");
        Component sendOutBinding = CurrentKeyAccessorKt.boundKey(PartySendBinding.INSTANCE).getDisplayName();
        component.append(sendOutBinding).append(" to battle>");
        return component;
    }
}
