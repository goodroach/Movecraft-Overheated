package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.weapons.Weapon;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CraftHeat {
    private final Craft craft;
    private Map<TrackedLocation, Integer> heatMap = new HashMap<>();
    private long lastUpdate;
    private long lastDisaster;

    public CraftHeat(Craft craft) {
        this.craft = craft;
    }

    public Map<TrackedLocation, Integer> getHeatMap() {
        return heatMap;
    }

    public void addHeat(TrackedLocation location, int amount) {
        heatMap.put(location, amount);
    }
}
