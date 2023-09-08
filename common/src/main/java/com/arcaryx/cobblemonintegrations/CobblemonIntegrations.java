package com.arcaryx.cobblemonintegrations;

import com.arcaryx.cobblemonintegrations.config.IConfig;
import com.arcaryx.cobblemonintegrations.net.AbstractNetworkHandler;

public class CobblemonIntegrations
{
	public static final String MOD_ID = "cobblemonintegrations";
	public static IConfig CONFIG;
	public static AbstractNetworkHandler NETWORK;

	public static void init() {
		NETWORK.registerMessages();
	}
}