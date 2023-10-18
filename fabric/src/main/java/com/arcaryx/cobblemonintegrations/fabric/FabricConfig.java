package com.arcaryx.cobblemonintegrations.fabric;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.config.IConfig;
import com.arcaryx.cobblemonintegrations.config.ShowType;
import com.arcaryx.cobblemonintegrations.waila.TooltipType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class FabricConfig implements IConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(new TypeToken<Pair<TooltipType, ShowType>>(){}.getType(), new PairAdapter())
            .create();
    private static final Path CONFIG_PATH = Paths.get("config", CobblemonIntegrations.MOD_ID + ".json");

    // WAILA
    private boolean hidePokemonLabel = false;
    private List<Pair<TooltipType, ShowType>> pokemonTooltips = TooltipType.pokemonDefaults;

    // Waystones
    private boolean allowWaystoneTeleport = true, requireTeleportMove = true;
    private int waystoneMinTeleportLevel = 0;

    // Enhanced Celestials
    private boolean applyInPVP = false;


    public static FabricConfig load() {
        var config = new FabricConfig();
        if (!Files.exists(CONFIG_PATH)) {
            config.saveConfig();
        } else {
            config.loadConfig();
        }
        return config;
    }

    private void loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            FabricConfig config = GSON.fromJson(reader, FabricConfig.class);
            this.hidePokemonLabel = config.hidePokemonLabel;
            this.pokemonTooltips = config.pokemonTooltips;
            this.allowWaystoneTeleport = config.allowWaystoneTeleport;
            this.requireTeleportMove = config.requireTeleportMove;
            this.waystoneMinTeleportLevel = config.waystoneMinTeleportLevel;
            this.applyInPVP = config.applyInPVP;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean hidePokemonLabel() {
        return hidePokemonLabel;
    }

    @Override
    public List<Pair<TooltipType, ShowType>> getPokemonTooltips() {
        return Collections.unmodifiableList(pokemonTooltips);
    }

    @Override
    public boolean allowWaystoneTeleport() {
        return allowWaystoneTeleport;
    }

    @Override
    public int waystoneMinTeleportLevel() {
        return waystoneMinTeleportLevel;
    }

    @Override
    public boolean requireTeleportMove() {
        return requireTeleportMove;
    }

    @Override
    public boolean applyInPVP() {
        return applyInPVP;
    }
}