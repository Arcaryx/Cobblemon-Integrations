package com.arcaryx.cobblemonintegrations.data;

import kotlin.ranges.IntRange;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class PokemonDrop implements Comparable<PokemonDrop> {
    private final ResourceLocation species, item;
    private final String form;
    private final float chance;
    private final int min, max;

    public PokemonDrop(ResourceLocation species, String form, ResourceLocation item, float chance, int min, int max) {
        this.species = species;
        this.form = form;
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public ResourceLocation getSpecies() {
        return species;
    }

    public String getForm() {
        return form;
    }

    public ResourceLocation getItem() {
        return item;
    }

    public float getChance() {
        return chance;
    }

    public IntRange getRange() {
        return new IntRange(min, max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonDrop that = (PokemonDrop) o;
        return Float.compare(that.chance, chance) == 0 &&
                min == that.min &&
                max == that.max &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, chance, min, max);
    }

    @Override
    public int compareTo(PokemonDrop other) {
        int itemComparison = item.compareTo(other.item);
        if (itemComparison != 0) {
            return itemComparison;
        }

        int chanceComparison = Float.compare(chance, other.chance);
        if (chanceComparison != 0) {
            return chanceComparison;
        }

        int minComparison = Integer.compare(min, other.min);
        if (minComparison != 0) {
            return minComparison;
        }

        return Integer.compare(max, other.max);
    }
}
