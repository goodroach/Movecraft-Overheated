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
    private HashMap<Block, Vector> nodes;

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
        nodes = new LinkedHashMap<>();
    }

    public Material getMaterial() {
        return material;
    }

    public byte[][] getDirections() {
        return directions;
    }

    public HashMap<Block, Vector> getNodes() {
        return nodes;
    }

    public void addNode(Block dispenser, Vector nodeLoc) {
        nodes.put(dispenser, nodeLoc);
    }

    public void clearNodes() {
        nodes.clear();
    }

    public int getHeatRate() {
        return heatRate;
    }

    public int getHeatDissipation() {
        return heatDissipation;
    }
}
