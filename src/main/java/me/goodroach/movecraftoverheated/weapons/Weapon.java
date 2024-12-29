package me.goodroach.movecraftoverheated.weapons;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Weapon {
    private final Material material;
    private final byte[][] directions;
    private final int heatRate;
    private final int heatDissipation;

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

    public Material getMaterial() {
        return material;
    }

    public byte[][] getDirections() {
        return directions;
    }

    public int getHeatRate() {
        return heatRate;
    }

    public int getHeatDissipation() {
        return heatDissipation;
    }
}
