package com.arcaryx.cobblemonintegrations;

import com.arcaryx.cobblemonintegrations.config.IConfig;
import com.arcaryx.cobblemonintegrations.net.AbstractNetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CobblemonIntegrations
{
	public static final String MOD_ID = "cobblemonintegrations";
	public static IConfig CONFIG;
	public static final Logger LOGGER = LogManager.getLogger();
	public static AbstractNetworkHandler NETWORK;

	public static void init() {
		NETWORK.registerMessages();
	}
}