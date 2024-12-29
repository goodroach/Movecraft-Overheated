package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.BFSUtils;
import me.goodroach.movecraftoverheated.MovecraftOverheated;
import me.goodroach.movecraftoverheated.weapons.Weapon;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.Material;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class WeaponHeatManager extends BukkitRunnable implements Listener {
    private ArrayList<Weapon> weapons = new ArrayList<>();
    private Map<Craft, CraftHeat> trackedCrafts = new WeakHashMap<>();
    private Map<Block, Weapon> trackedDispensers = new WeakHashMap<>();
    private static final NamespacedKey key = new NamespacedKey(MovecraftOverheated.getInstance(), "heat");

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        for (Weapon weapon : weapons) {
            //coolDispensers(weapon);

            if (weapon.getNodes().isEmpty() || weapon.getNodes() == null) {
                continue;
            }

            List<List<Block>> dispenserForest = BFSUtils.getConnectedDispensers(
                weapon.getDirections(), weapon.getNodes()
            );

            setHeatFromForest(dispenserForest, weapon);
            weapon.clearNodes();
        }
    }

    /**
     * Sets or updates the heat level of a dispenser block.
     * <p>
     * This method applies a heat value to a dispenser based on the provided weapon and its heat rate.
     * If the heat value becomes zero or less, the dispenser is removed from the tracking map.
     * Otherwise, the heat value is stored in the dispenser's persistent data container, and the dispenser is tracked.
     * </p>
     *
     * @param dispenser the {@link Block} representing the dispenser. Must be of type {@link Material#DISPENSER}.
     *                  If the block is not a dispenser, the method will return without performing any operations.
     * @param weapon    the {@link Weapon} associated with the dispenser, which determines the heat rate.
     * @param amount    the base heat amount to apply to the dispenser. This value is modified by the weapon's heat rate.
     *                  The resulting heat is added to any existing heat value if already set.
     */
    public void setDispenserHeat(Block dispenser, Weapon weapon, int amount) {
        if (dispenser.getType() != Material.DISPENSER) {
            return;
        }
        TileState state = (TileState) dispenser.getState();
        PersistentDataContainer dataContainer = state.getPersistentDataContainer();

        amount *= weapon.getHeatRate();
        if (dataContainer.has(key)) {
            amount += dataContainer.get(key, PersistentDataType.INTEGER);
        }
        if (amount <= 0) {
            amount = 0;
            trackedDispensers.remove(dispenser);
        }

        dataContainer.set(key, PersistentDataType.INTEGER, amount);
        state.update();
        trackedDispensers.put(dispenser, weapon);
    }

    private void coolDispensers(Weapon weapon) {
        for (Block block : trackedDispensers.keySet()) {
            // Negative value as it is removing heat
            setDispenserHeat(block, weapon, -1);
        }
    }

    private void setHeatFromForest(List<List<Block>> forest, Weapon weapon) {
        for (List<Block> dispenserTree : forest) {
            System.out.println("Dispenser tree size: " + dispenserTree.size());
            for (Block dispenser : dispenserTree) {
                setDispenserHeat(dispenser, weapon, dispenserTree.size());
            }
        }
    }

    @Deprecated
    private void coolDispenserHeat(Weapon weapon) {
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

    @Deprecated
    private void setHeatFromForest(List<List<Block>> forest, int amount) {
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

    public static NamespacedKey getKey() {
        return key;
    }
}
