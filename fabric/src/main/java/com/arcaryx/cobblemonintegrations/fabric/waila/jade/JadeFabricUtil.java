package com.arcaryx.cobblemonintegrations.fabric.waila.jade;

import com.arcaryx.cobblemonintegrations.waila.jade.IJadeUtil;
import snownee.jade.util.CommonProxy;

import java.util.UUID;

public class JadeFabricUtil implements IJadeUtil {
    @Override
    public String getPlayerUsernameClient(UUID playerUuid) {
        return CommonProxy.getLastKnownUsername(playerUuid);
    }

    @Override
    public String getPlayerUsernameServer(UUID playerUuid) {
        return "";
    }
}
