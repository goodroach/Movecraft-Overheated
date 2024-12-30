package me.goodroach.movecraftoverheated.weapons;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedHashMap;

public record Weapon(
        Material material,
        byte[][] directions,
        int heatRate,
        int heatDissipation
) {

    public Weapon(
        Material material,
        byte[][] directions,
        int heatRate,
        int heatDissipation
    ) {
        this.material = material;
        this.directions = directions;
        this.heatRate = heatRate;
        this.heatDissipation = heatDissipation;
    }

    @Deprecated(forRemoval = true)
    public Material getMaterial() {
        return material;
    }

    @Deprecated(forRemoval = true)
    public byte[][] getDirections() {
        return directions;
    }

    @Deprecated(forRemoval = true)
    public int getHeatRate() {
        return heatRate;
    }

    @Deprecated(forRemoval = true)
    public int getHeatDissipation() {
        return heatDissipation;
    }
}
