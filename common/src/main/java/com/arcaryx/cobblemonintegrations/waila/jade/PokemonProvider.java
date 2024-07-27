package com.arcaryx.cobblemonintegrations.waila.jade;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.config.ShowType;
import com.arcaryx.cobblemonintegrations.util.ClientUtils;
import com.arcaryx.cobblemonintegrations.util.PokemonUtils;
import com.arcaryx.cobblemonintegrations.util.TextUtils;
import com.arcaryx.cobblemonintegrations.waila.TooltipType;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.client.settings.ServerSettings;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.HealthElement;
import snownee.jade.util.CommonProxy;

import java.util.List;
import java.util.stream.StreamSupport;

public enum PokemonProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    public static final String TAG_GENDER = "ci_gender";
    public static final String TAG_TRAINER_NAME = "ci_trainer_name";
    public static final String TAG_NATURE_NAME = "ci_nature_name";
    public static final String TAG_MINTED_NATURE_NAME = "ci_minted_nature_name";
    public static final String TAG_FRIENDSHIP = "ci_friendship";
    public static final String TAG_ABILITY_NAME = "ci_ability_name";
    public static final String TAG_ABILITY_HIDDEN = "ci_ability_hidden";
    public static final String TAG_EV_YIELD = "ci_yield";
    public static final String TAG_IVS = "ci_ivs";
    public static final String TAG_EVS = "ci_evs";
    public static final String TAG_EGG_GROUPS = "ci_egg_groups";
    public static final String TAG_EGG_GROUP_COUNT = "ci_egg_group_count";

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor entityAccessor) {
        if (!(entityAccessor.getEntity() instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        var pokemon = pokemonEntity.getPokemon();
        var tooltips = CobblemonIntegrations.CONFIG.getPokemonTooltips();

        if (configContains(tooltips, TooltipType.TITLE_GENDER) || configContains(tooltips, TooltipType.GENDER)) {
            data.putString(TAG_GENDER, pokemon.getGender().getShowdownName());
        }

        if (configContains(tooltips, TooltipType.TRAINER) && pokemon.getOwnerUUID() != null) {
            data.putUUID(TAG_TRAINER_NAME, pokemon.getOwnerUUID());
        }

        if (configContains(tooltips, TooltipType.FRIENDSHIP) && !pokemon.isWild()) {
            data.putInt(TAG_FRIENDSHIP, pokemon.getFriendship());
        }

        if (configContains(tooltips, TooltipType.REWARD_EVS)) {
            data.put(TAG_EV_YIELD, PokemonUtils.saveStatMapToCompoundTag(pokemon.getForm().getEvYield()));
        }

        if (configContains(tooltips, TooltipType.NATURE)) {
            data.putString(TAG_NATURE_NAME, pokemon.getNature().getDisplayName());
            if (pokemon.getMintedNature() != null) {
                data.putString(TAG_MINTED_NATURE_NAME, pokemon.getMintedNature().getDisplayName());
            }
        }

        if (configContains(tooltips, TooltipType.ABILITY)) {
            data.putString(TAG_ABILITY_NAME, pokemon.getAbility().getDisplayName());
            data.putBoolean(TAG_ABILITY_HIDDEN, PokemonUtils.hasHiddenAbility(pokemon));
        }

        if (configContains(tooltips, TooltipType.IVS)) {
            data.put(TAG_IVS, pokemon.getIvs().saveToNBT(new CompoundTag()));
        }

        if (configContains(tooltips, TooltipType.EVS)) {
            data.put(TAG_EVS, pokemon.getEvs().saveToNBT(new CompoundTag()));
        }

        if (configContains(tooltips, TooltipType.EGG_GROUPS)) {
            var eggGroups = pokemon.getForm().getEggGroups();
            data.putInt(TAG_EGG_GROUP_COUNT, eggGroups.size());
            data.putString(TAG_EGG_GROUPS, String.join(", ", eggGroups.stream().map(EggGroup::getShowdownID$common).toList()));
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        var pokemon = pokemonEntity.getPokemon();
        pokemon.setAspects(pokemonEntity.getAspects());
        pokemon.updateForm();
        tooltip.clear();
        var tooltips = CobblemonIntegrations.CONFIG.getPokemonTooltips();

        for (var type : tooltips) {
            if (type.getRight() == ShowType.SHOW) {
                addTooltip(type.getLeft(), tooltip, accessor, pokemonEntity, pokemon, tooltips);
            } else if (type.getRight() == ShowType.SNEAK && accessor.getPlayer().isCrouching()) {
                addTooltip(type.getLeft(), tooltip, accessor, pokemonEntity, pokemon, tooltips);
            } else if (type.getRight() == ShowType.NO_SNEAK && !accessor.getPlayer().isCrouching()) {
                addTooltip(type.getLeft(), tooltip, accessor, pokemonEntity, pokemon, tooltips);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return CobblemonJadePlugin.POKEMON_ENTITY;
    }

    private boolean configContains(List<Pair<TooltipType, ShowType>> tooltips, TooltipType type) {
        return tooltips.stream().anyMatch(x -> x.getLeft() == type && x.getRight() != ShowType.HIDE);
    }

    private void addTooltip(TooltipType tooltipType, ITooltip tooltip, EntityAccessor accessor, PokemonEntity pokemonEntity, Pokemon pokemon, List<Pair<TooltipType, ShowType>> tooltips) {
        var data = accessor.getServerData();
        switch (tooltipType) {
            case TITLE -> {
                var component = pokemonEntity.getName().copy();
                if (ServerSettings.INSTANCE.getDisplayEntityLevelLabel() && pokemonEntity.labelLevel() > 0) {
                    var levelLabel = LocalizationUtilsKt.lang("label.lv", pokemonEntity.labelLevel());
                    component.append(" ").append(levelLabel);
                }
                tooltip.add(component.withStyle(ChatFormatting.WHITE));
            }
            case TITLE_GENDER -> {
                var gender = data.contains(TAG_GENDER) ? PokemonUtils.getGenderFromShowdownName(data.getString(TAG_GENDER)) : Gender.GENDERLESS;
                var component = pokemonEntity.getName().copy();
                if (gender != Gender.GENDERLESS) {
                    var prefix = gender == Gender.MALE ? ChatFormatting.BLUE + "\u2642 " : ChatFormatting.LIGHT_PURPLE + "\u2640 ";
                    component = Component.literal(prefix).append(component);
                }
                if (ServerSettings.INSTANCE.getDisplayEntityLevelLabel() && pokemonEntity.labelLevel() > 0) {
                    var levelLabel = LocalizationUtilsKt.lang("label.lv", pokemonEntity.labelLevel());
                    component.append(" ").append(levelLabel);
                }
                tooltip.add(component.withStyle(ChatFormatting.WHITE));
            }
            case SPECIES -> {
                var component = Component.literal("Species: ").append(pokemon.getDisplayName());
                if (pokemon.getSpecies().getStandardForm() != pokemon.getForm()) {
                    component.append(Component.literal(String.format(" (%s)", pokemon.getForm().getName())));
                }
                tooltip.add(component);
            }
            case LEVEL -> {
                var component = Component.literal("Level: " + pokemon.getLevel());
                tooltip.add(component);
            }
            case GENDER -> {
                var gender = data.contains(TAG_GENDER) ? PokemonUtils.getGenderFromShowdownName(data.getString(TAG_GENDER)) : Gender.GENDERLESS;
                if (gender != Gender.GENDERLESS) {
                    var suffix = gender == Gender.MALE ? ChatFormatting.BLUE + "\u2642 " : ChatFormatting.LIGHT_PURPLE + "\u2640 ";
                    tooltip.add(Component.literal("Gender: ").append(suffix));
                }
            }
            case HEALTH -> {
                tooltip.add(new HealthElement(pokemonEntity.getMaxHealth(), pokemonEntity.getHealth()));
            }
            case TRAINER -> {
                if (data.contains(TAG_TRAINER_NAME)) {
                    var username = CommonProxy.getLastKnownUsername(data.getUUID(TAG_TRAINER_NAME));
                    tooltip.add(Component.literal("Trainer: ").append(username == null ? "???" : username));
                }
            }
            case NICKNAME -> {
                if (pokemon.getNickname() != null && !pokemon.getNickname().getString().isEmpty()) {
                    tooltip.add(Component.literal("Nickname: ").append(pokemon.getNickname()));
                }
            }
            case FRIENDSHIP -> {
                if (data.contains(TAG_FRIENDSHIP)) {
                    double percentage = (data.getInt(TAG_FRIENDSHIP) / 255.0F) * 100;
                    int flooredPercentage = (int)Math.floor(percentage);
                    tooltip.add(Component.literal(String.format("Friendship: %d (%d%%)", data.getInt(TAG_FRIENDSHIP), flooredPercentage)));
                }
            }
            case TYPES -> {
                var types = StreamSupport.stream(pokemon.getForm().getTypes().spliterator(), false).toList();
                var typesComponent = Component.literal(TextUtils.basicPluralize("Type", types.size()) + ": ");
                for (var type : types) {
                    if (typesComponent.getSiblings().size() > 0) {
                        typesComponent.append(Component.literal(", "));
                    }
                    typesComponent.append(type.getDisplayName().withStyle(Style.EMPTY.withColor(type.getHue())));
                }
                tooltip.add(typesComponent);
            }
            case REWARD_EVS -> {
                if (data.contains(TAG_EV_YIELD)) {
                    var yield = TextUtils.formatEvYield(PokemonUtils.loadStatMapFromCompoundTag(data.getCompound(TAG_EV_YIELD)));
                    tooltip.add(Component.literal("EV Yield: ").append(yield));
                }
            }
            case NATURE -> {
                if (data.contains(TAG_NATURE_NAME)) {
                    if (!data.contains(TAG_MINTED_NATURE_NAME)) {
                        tooltip.add(Component.literal("Nature: ").append(Component.translatable(data.getString(TAG_NATURE_NAME))));
                    } else {
                        tooltip.add(Component.literal("Nature: ")
                                .append(Component.translatable(data.getString(TAG_MINTED_NATURE_NAME)))
                                .append(Component.literal(" (Minted)")));
                    }
                }
            }
            case ABILITY -> {
                if (data.contains(TAG_ABILITY_HIDDEN)) {
                    var abilityComponent = Component.literal("Ability: ").append(Component.translatable(data.getString(TAG_ABILITY_NAME)));
                    if (data.contains(TAG_ABILITY_HIDDEN) && data.getBoolean(TAG_ABILITY_HIDDEN)) {
                        abilityComponent.append(Component.literal(" (Hidden)"));
                    }
                    tooltip.add(abilityComponent);
                }
            }
            case IVS -> {
                if (data.contains(TAG_IVS)) {
                    var ivs = pokemon.getIvs().loadFromNBT(data.getCompound(TAG_IVS));
                    tooltip.add(Component.literal("IVs: " + TextUtils.formatStats(ivs, 31 * 6)));
                }
            }
            case EVS -> {
                if (data.contains(TAG_EVS)) {
                    var evs = pokemon.getEvs().loadFromNBT(data.getCompound(TAG_EVS));
                    tooltip.add(Component.literal("EVs: " + TextUtils.formatStats(evs, 510)));
                }
            }
            case DEX_ENTRY -> {
                if (pokemon.getForm().getPokedex().size() > 0) {
                    var dex = pokemon.getForm().getPokedex().stream().findFirst().orElse("");
                    var dexLines = TextUtils.wrapString("Dex Entry: " + I18n.get(dex), 32);
                    for (var line : dexLines) {
                        tooltip.add(Component.literal(line));
                    }
                }
            }
            case SNEAK_HINT -> {
                tooltip.add(Component.literal("<sneak for additional info>").withStyle(ChatFormatting.DARK_GRAY));
            }
            case BATTLE_HINT -> {
                if (!pokemonEntity.isOwnedBy(accessor.getPlayer())) {
                    var component = ClientUtils.CreateBattleHint();
                    tooltip.add(component.withStyle(ChatFormatting.DARK_GRAY));
                }
            }
            case EGG_GROUPS -> {
                var eggGroupCount = data.getInt(TAG_EGG_GROUP_COUNT);
                var eggGroups = data.getString(TAG_EGG_GROUPS);
                var eggGroupText = TextUtils.basicPluralize("Egg Group", eggGroupCount) + ": " + eggGroups;
                var eggGroupLines = TextUtils.wrapString(eggGroupText, 32);
                for (var line : eggGroupLines) {
                    tooltip.add(Component.literal(line));
                }
            }
        }
    }
}
