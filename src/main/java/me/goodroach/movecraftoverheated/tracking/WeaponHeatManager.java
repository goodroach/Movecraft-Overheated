package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.BFSUtils;
import me.goodroach.movecraftoverheated.MovecraftOverheated;
import me.goodroach.movecraftoverheated.weapons.Weapon;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class WeaponHeatManager extends BukkitRunnable implements Listener {
    private ArrayList<Weapon> weapons = new ArrayList<>();
    private Map<Craft, CraftHeat> trackedCrafts = new WeakHashMap<>();

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        for (Weapon weapon : weapons) {
            coolDispenserHeat(weapon);

            if (weapon.getNodes().isEmpty() || weapon.getNodes() == null) {
                continue;
            }

            List<List<Block>> dispenserForest = BFSUtils.getConnectedDispensers(
                weapon.getDirections(), weapon.getNodes()
            );

            setHeatFromForest(dispenserForest, weapon.getHeatRate());
            weapon.clearNodes();
        }
    }

    public void coolDispenserHeat(Weapon weapon) {
        if (trackedCrafts.isEmpty() || trackedCrafts == null) {
            return;
        }

        for (Craft craft : trackedCrafts.keySet()) {
            CraftHeat heat = trackedCrafts.get(craft);
            if (heat.getHeatMap() == null || heat.getHeatMap().isEmpty()) {
                continue;
            }

            heat.getHeatMap().entrySet().removeIf(entry -> {
                // I believe entry is what the heat map turns into?
                entry.setValue(entry.getValue() - weapon.getHeatDissipation());
                // This is the boolean value that makes it concurrently remove any heat values less than zero.
                return entry.getValue() <= 0;
            });
        }
    }

    public void setHeatFromForest(List<List<Block>> forest, int amount) {
        for (List<Block> dispenserTree : forest) {
            System.out.println("Dispenser tree size: " + dispenserTree.size());
            for (Block dispenser : dispenserTree) {
                Craft craft = MathUtils.fastNearestCraftToLoc(CraftManager.getInstance().getCrafts(), dispenser.getLocation());
                if (craft == null) {
                    continue;
                }

                MovecraftLocation location = MathUtils.bukkit2MovecraftLoc(dispenser.getLocation());
                TrackedLocation trackedLocation = new TrackedLocation(craft, location);

                if (trackedCrafts.get(craft) == null) {
                    trackedCrafts.put(craft, new CraftHeat(craft));
                }
                trackedCrafts.get(craft).addHeat(trackedLocation, amount * dispenserTree.size());
            }
        }
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(ArrayList<Weapon> weapons) {
        this.weapons = weapons;
    }
}
