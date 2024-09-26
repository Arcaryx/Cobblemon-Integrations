package com.arcaryx.cobblemonintegrations.forge.waila.jade;

import com.arcaryx.cobblemonintegrations.waila.jade.IJadeUtil;
import net.minecraftforge.common.UsernameCache;

import java.util.UUID;

public class JadeForgeUtil  implements IJadeUtil {
    @Override
    public String getPlayerUsernameClient(UUID playerUuid) {
        return "";
    }

    @Override
    public String getPlayerUsernameServer(UUID playerUuid) {
        return UsernameCache.getLastKnownUsername(playerUuid);
    }
}
