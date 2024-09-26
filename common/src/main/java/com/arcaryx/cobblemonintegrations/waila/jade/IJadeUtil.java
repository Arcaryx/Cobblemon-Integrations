package com.arcaryx.cobblemonintegrations.waila.jade;

import java.util.UUID;

public interface IJadeUtil {
    String getPlayerUsernameClient(UUID playerUuid);
    String getPlayerUsernameServer(UUID playerUuid);
}
