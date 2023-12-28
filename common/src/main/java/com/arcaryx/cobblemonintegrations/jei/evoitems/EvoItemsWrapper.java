package com.arcaryx.cobblemonintegrations.jei.evoitems;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.EnhancedCelestialsHandler;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.LunarEventRequirement;
import com.arcaryx.cobblemonintegrations.util.TextUtils;
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition;
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.spawning.TimeRange;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.evolution.requirements.*;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EvoItemsWrapper implements IRecipeCategoryExtension, IRecipeSlotTooltipCallback {

    private final PokemonItemEvo itemEvo;

    private final List<Item> validItems;

    private PokemonFloatingState state;
    private long last;

    public EvoItemsWrapper(PokemonItemEvo itemEvo) {
        this.itemEvo = itemEvo;
        this.validItems = BuiltInRegistries.ITEM.stream().filter(x -> itemEvo.getItemEvo().getRequiredContext().getItem().fits(x, BuiltInRegistries.ITEM)).toList();;
    }

    public List<Item> getValidItems() {
        return validItems;
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics graphics, double mouseX, double mouseY) {
        if (state == null) {
            state = new PokemonFloatingState();
        }
        // Why am I being forced to compute partialTicks?
        var elapsed = System.currentTimeMillis() - last;
        last = System.currentTimeMillis();
        var partialTicks = Mth.clamp(elapsed / 100F, 0F, 1F);

        var pose = graphics.pose();

        pose.pushPose();
        var speciesBase = PokemonSpecies.INSTANCE.getByIdentifier(itemEvo.getSpecies());
        var formBase = speciesBase.getForms().stream().filter(x -> x.getName().equals(itemEvo.getForm())).findFirst().orElse(speciesBase.getStandardForm());
        var component = speciesBase.getTranslatedName();
        if (speciesBase.getStandardForm() != formBase && !formBase.getName().equalsIgnoreCase("base")) {
            component.append(Component.literal(String.format(" (%s)", formBase.getName())));
        }
        graphics.drawString(Minecraft.getInstance().font, component, 2, 1, Objects.requireNonNull(ChatFormatting.WHITE.getColor()));
        pose.popPose();


        var pokemonBase = new RenderablePokemon(speciesBase, new HashSet<>(formBase.getAspects()));
        var evoAspects = new HashSet<>(formBase.getAspects());
        var evoSpecies = PokemonSpecies.INSTANCE.getByName(itemEvo.getItemEvo().getResult().getSpecies());
        for (var prop : itemEvo.getItemEvo().getResult().getCustomProperties()) {
            if (prop instanceof FlagSpeciesFeature sf) {
                if (sf.getEnabled()) {
                    evoAspects.add(sf.getName());
                } else {
                    evoAspects.remove(sf.getName());
                }
            }
        }

        var pokemonEvo = new RenderablePokemon(evoSpecies, new HashSet<>(itemEvo.getResult()));

        var m1 = pose.last().pose();
        var l1 = m1.m30();
        var t1 = m1.m31();
        var rotationY = -30F;

        // Pokemon Base
        pose.pushPose();
        graphics.enableScissor((int)l1 + 2, (int)t1 + 13, (int)l1 + 61, (int)t1 + 92);
        pose.translate(31, 13, 0);
        pose.scale(1F, 1F, 1F);
        pose.pushPose();
        PokemonGuiUtilsKt.drawProfilePokemon(
                pokemonBase,
                pose,
                QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, rotationY, 0F)),
                state,
                partialTicks,
                40F
        );
        pose.popPose();
        graphics.disableScissor();
        pose.popPose();

        // Pokemon Evo
        pose.pushPose();
        graphics.enableScissor((int)l1 + 91, (int)t1 + 13, (int)l1 + 150, (int)t1 + 92);
        pose.translate(120, 13, 0);
        pose.scale(1F, 1F, 1F);
        pose.pushPose();
        PokemonGuiUtilsKt.drawProfilePokemon(
                pokemonEvo,
                pose,
                QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, rotationY, 0F)),
                state,
                partialTicks,
                40F
        );
        pose.popPose();
        graphics.disableScissor();
        pose.popPose();
    }

    @Override
    public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
        if (itemEvo.getItemEvo().getRequirements().isEmpty()) {
            tooltip.add(Component.literal("No additional requirements."));
            return;
        }
        tooltip.add(Component.literal(TextUtils.basicPluralize("Requirement", itemEvo.getItemEvo().getRequirements().size()) + ":"));
        for (var requirement : itemEvo.getItemEvo().getRequirements()) {
            var handler = reqHandlers.get(requirement.getClass());
            if (handler != null) {
                handler.accept(requirement, tooltip);
            } else {
                tooltip.add(Component.literal("Unknown: %s".formatted(requirement.getClass().getSimpleName())));
            }
        }
    }

    // TODO: I could move these to a different file at least :P
    private Map<Class<? extends EvolutionRequirement>, BiConsumer<EvolutionRequirement, List<Component>>> reqHandlers = new HashMap<>() {{
        // Cobblemon
        put(AnyRequirement.class, EvoItemsWrapper::anyRequirement);
        put(AreaRequirement.class, EvoItemsWrapper::areaRequirement);
        put(AttackDefenceRatioRequirement.class, EvoItemsWrapper::attackDefenceRatioRequirement);
        put(BattleCriticalHitsRequirement.class, EvoItemsWrapper::battleCriticalHitsRequirement);
        put(BiomeRequirement.class, EvoItemsWrapper::biomeRequirement);
        put(BlocksTraveledRequirement.class, EvoItemsWrapper::blocksTraveledRequirement);
        put(DamageTakenRequirement.class, EvoItemsWrapper::damageTakenRequirement);
        put(DefeatRequirement.class, EvoItemsWrapper::defeatRequirement);
        put(FriendshipRequirement.class, EvoItemsWrapper::friendshipRequirement);
        put(HeldItemRequirement.class, EvoItemsWrapper::heldItemRequirement);
        put(LevelRequirement.class, EvoItemsWrapper::levelRequirement);
        put(MoonPhaseRequirement.class, EvoItemsWrapper::moonPhaseRequirement);
        put(MoveSetRequirement.class, EvoItemsWrapper::moveSetRequirement);
        put(MoveTypeRequirement.class, EvoItemsWrapper::moveTypeRequirement);
        put(PartyMemberRequirement.class, EvoItemsWrapper::partyMemberRequirement);
        put(PokemonPropertiesRequirement.class, EvoItemsWrapper::pokemonPropertiesRequirement);
        put(RecoilRequirement.class, EvoItemsWrapper::recoilRequirement);
        put(StructureRequirement.class, EvoItemsWrapper::structureRequirement);
        put(TimeRangeRequirement.class, EvoItemsWrapper::timeRangeRequirement);
        put(UseMoveRequirement.class, EvoItemsWrapper::useMoveRequirement);
        put(WeatherRequirement.class, EvoItemsWrapper::weatherRequirement);
        put(WorldRequirement.class, EvoItemsWrapper::worldRequirement);

        // Integrations
        put(LunarEventRequirement.class, EvoItemsWrapper::lunarEventRequirement);

    }};

    private static void anyRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (AnyRequirement)evoRequirement;
        tooltip.add(Component.literal("(Any)"));
    }

    private static void areaRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (AreaRequirement)evoRequirement;
        tooltip.add(Component.literal("Within %s".formatted(requirement.getBox().toString())));
    }

    private static void attackDefenceRatioRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (AttackDefenceRatioRequirement)evoRequirement;
        switch (requirement.getRatio()) {
            case ATTACK_HIGHER -> tooltip.add(Component.literal("Attack > Defence"));
            case DEFENCE_HIGHER -> tooltip.add(Component.literal("Attack < Defence"));
            case EQUAL -> tooltip.add(Component.literal("Attack = Defence"));
        }
    }

    private static void battleCriticalHitsRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (BattleCriticalHitsRequirement)evoRequirement;
        tooltip.add(Component.literal("%d Critical Hits".formatted(requirement.getAmount())));
    }

    private static void biomeRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (BiomeRequirement)evoRequirement;
        if (requirement.getBiomeCondition() != null) {
            if (requirement.getBiomeCondition() instanceof RegistryLikeIdentifierCondition<Biome> biomeId) {
                tooltip.add(Component.literal("In biome %s".formatted(biomeId.getIdentifier())));
            } else if (requirement.getBiomeCondition() instanceof RegistryLikeTagCondition<Biome> biomeTag) {
                tooltip.add(Component.literal("In biome #%s".formatted(biomeTag.getTag().location())));
            }
        }
        else if (requirement.getBiomeAnticondition() != null) {
            if (requirement.getBiomeAnticondition() instanceof RegistryLikeIdentifierCondition<Biome> biomeId) {
                tooltip.add(Component.literal("Not In biome %s".formatted(biomeId.getIdentifier())));
            } else if (requirement.getBiomeAnticondition() instanceof RegistryLikeTagCondition<Biome> biomeTag) {
                tooltip.add(Component.literal("Not in biome #%s".formatted(biomeTag.getTag().location())));
            }
        }
    }

    private static void blocksTraveledRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (BlocksTraveledRequirement)evoRequirement;
        tooltip.add(Component.literal("%d Blocks Traveled".formatted(requirement.getAmount())));
    }

    private static void damageTakenRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (DamageTakenRequirement)evoRequirement;
        tooltip.add(Component.literal("%d Damage Taken".formatted(requirement.getAmount())));
    }

    private static void defeatRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (DefeatRequirement)evoRequirement;
        tooltip.add(Component.literal("%d %s".formatted(requirement.getAmount(), TextUtils.basicPluralize("Defeat", requirement.getAmount()))));
    }

    private static void friendshipRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (FriendshipRequirement)evoRequirement;
        tooltip.add(Component.literal("%d Friendship".formatted(requirement.getAmount())));
    }

    private static void heldItemRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (HeldItemRequirement)evoRequirement;
        var item = requirement.getItemCondition().getItem();
        if (item instanceof RegistryLikeIdentifierCondition<Item> itemId) {
            tooltip.add(Component.literal("Holding %s".formatted(itemId.getIdentifier())));
        } else if (item instanceof RegistryLikeTagCondition<Item> itemTag) {
            tooltip.add(Component.literal("Holding #%s".formatted(itemTag.getTag().location())));
        }
    }

    private static void levelRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (LevelRequirement)evoRequirement;
        tooltip.add(Component.literal("%sLevel%s".formatted(requirement.getMinLevel() == 1 ? "" : "%d ≤ ".formatted(requirement.getMaxLevel()),
                requirement.getMaxLevel() == Integer.MAX_VALUE ? "" : " ≤ %d".formatted(requirement.getMaxLevel()))));
    }

    private static void moonPhaseRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (MoonPhaseRequirement)evoRequirement;
        switch (requirement.getMoonPhase()) {
            // TODO: I can't wait to do a translation/cleanup pass and clean this up :deceased:
            case FULL_MOON -> tooltip.add(Component.literal("Moon Phase: Full Moon"));
            case WANING_GIBBOUS -> tooltip.add(Component.literal("Moon Phase: Waning Gibbous"));
            case THIRD_QUARTER -> tooltip.add(Component.literal("Moon Phase: Third Quarter"));
            case WANING_CRESCENT -> tooltip.add(Component.literal("Moon Phase: Waning Crescent"));
            case NEW_MOON -> tooltip.add(Component.literal("Moon Phase: New Moon"));
            case WAXING_CRESCENT -> tooltip.add(Component.literal("Moon Phase: Waxing Crescent"));
            case FIRST_QUARTER -> tooltip.add(Component.literal("Moon Phase: First Quarter"));
            case WAXING_GIBBOUS -> tooltip.add(Component.literal("Moon Phase: Waxing Gibbous"));
        }
    }

    private static void moveSetRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (MoveSetRequirement)evoRequirement;
        tooltip.add(Component.literal("Move: ").append(requirement.getMove().getDisplayName()));
    }

    private static void moveTypeRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (MoveTypeRequirement)evoRequirement;
        tooltip.add(Component.literal("Move Type: ").append(requirement.getType().getDisplayName()));
    }

    private static void partyMemberRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (PartyMemberRequirement)evoRequirement;
        tooltip.add(Component.literal("Party Member: ").append(requirement.getTarget().asString(", ")));
    }

    private static void pokemonPropertiesRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (PokemonPropertiesRequirement)evoRequirement;
        tooltip.add(Component.literal("Properties: ").append(requirement.getTarget().asString(", ")));
    }

    private static void recoilRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (RecoilRequirement)evoRequirement;
        tooltip.add(Component.literal("%d Recoil Damage".formatted(requirement.getAmount())));
    }

    private static void structureRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (StructureRequirement)evoRequirement;
        var structure = requirement.getStructureCondition();
        if (structure instanceof RegistryLikeIdentifierCondition<Structure> structureId) {
            tooltip.add(Component.literal("Near Structure %s".formatted(structureId.getIdentifier())));
        } else if (structure instanceof RegistryLikeTagCondition<Structure> structureTag) {
            tooltip.add(Component.literal("Near Structure #%s".formatted(structureTag.getTag().location())));
        }
    }

    private static void timeRangeRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (TimeRangeRequirement)evoRequirement;
        tooltip.add(Component.literal("Specific Time Range")); // TODO: Some way of formatting these time ranges
        //if (TimeRange.Companion.getTimeRanges().containsValue(requirement.getRange())) {
        //    for (var timeRange : TimeRange.Companion.getTimeRanges().entrySet()) {
        //        if (new HashSet<>(requirement.getRange().getRanges()).containsAll(timeRange.getValue().getRanges())) {
        //            tooltip.add(Component.literal("Time: %s".formatted(timeRange.getKey())));
        //            break;
        //        }
        //    }
        //} else {
        //    tooltip.add(Component.literal("Unknown: Time Range"));
        //}
    }

    private static void useMoveRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (UseMoveRequirement)evoRequirement;
        tooltip.add(Component.literal("Use ").append(requirement.getMove().getDisplayName())
                .append(" %d %s".formatted(requirement.getAmount(), TextUtils.basicPluralize("time", requirement.getAmount()))));
    }

    private static void weatherRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (WeatherRequirement)evoRequirement;
        // TODO: Probably a way of displaying "Sunny" as opposed to "Not Raining, Not Thundering"
        if (Boolean.TRUE.equals(requirement.isRaining())) {
            tooltip.add(Component.literal("Raining"));
        } else if (Boolean.FALSE.equals(requirement.isRaining())) {
            tooltip.add(Component.literal("Not Raining"));
        }
        if (Boolean.TRUE.equals(requirement.isThundering())) {
            tooltip.add(Component.literal("Thundering"));
        } else if (Boolean.FALSE.equals(requirement.isThundering())) {
            tooltip.add(Component.literal("Not Thundering"));
        }
    }

    private static void worldRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (WorldRequirement)evoRequirement;
        tooltip.add(Component.literal("Dimension: %s".formatted(requirement.getIdentifier())));
    }

    private static void lunarEventRequirement(EvolutionRequirement evoRequirement, List<Component> tooltip) {
        var requirement = (LunarEventRequirement)evoRequirement;
        tooltip.add(Component.literal("Lunar Event: ").append(EnhancedCelestialsHandler.GetLunarEventName(requirement.lunarEvent, Minecraft.getInstance().level)));
    }
}
