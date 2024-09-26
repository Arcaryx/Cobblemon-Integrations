package com.arcaryx.cobblemonintegrations;

import com.arcaryx.cobblemonintegrations.config.IConfig;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.LunarEventCondition;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.LunarEventRequirement;
import com.arcaryx.cobblemonintegrations.net.AbstractNetworkHandler;
import com.arcaryx.cobblemonintegrations.waila.jade.IJadeUtil;
import com.arcaryx.cobblemonintegrations.waystones.InteractionGuiHandler;
import com.cobblemon.mod.common.pokemon.evolution.adapters.CobblemonRequirementAdapter;
import kotlin.jvm.internal.Reflection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CobblemonIntegrations
{
	public static final String MOD_ID = "cobblemonintegrations";
	public static IConfig CONFIG;
	public static final Logger LOGGER = LogManager.getLogger();
	public static AbstractNetworkHandler NETWORK;
	public static IJadeUtil JADE_UTIL;

	public static void init() {
		NETWORK.registerMessagesServer();

		LunarEventCondition.register();
		CobblemonRequirementAdapter.INSTANCE.registerType(LunarEventRequirement.ADAPTER_VARIANT, Reflection.getOrCreateKotlinClass(LunarEventRequirement.class));
	}

	public static void initClient() {
		InteractionGuiHandler.init();
	}
}