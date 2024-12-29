package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.MovecraftOverheated;
import me.goodroach.movecraftoverheated.weapons.Weapon;
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

public class WeaponHeatManager extends BukkitRunnable implements Listener {
    private final GraphManager graphManager;
    private Map<Material, DispenserGraph> weapons = new HashMap<>();
    private Set<DispenserWeapon> trackedDispensers = new HashSet<>();
    private static final NamespacedKey key = new NamespacedKey(MovecraftOverheated.getInstance(), "heat");

    public WeaponHeatManager(GraphManager graphManager) {
        this.graphManager = graphManager;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        for (DispenserGraph graph : weapons.values()) {
            coolDispensers(graph.getWeapon());

            List<List<DispenserWeapon>> dispenserForest = graphManager.getForest(graph);

            setHeatFromForest(dispenserForest, graph.getWeapon());
            graph.clear();
        }
    }

    /**
     * Sets the heat value for a given dispenser weapon.
     * <p>
     * This method updates the persistent data container of the dispenser's block to track the heat value.
     * It ensures that the dispenser's heat does not go below zero and removes the dispenser from tracking
     * if the heat is reset to zero. It also handles scenarios where the dispenser was not properly tracked
     * due to plugin crashes or bugs.
     * </p>
     *
     * @param dispenserWeapon The dispenser weapon whose heat is being set. This should not be {@code null}.
     * @param amount The heat amount to set for the dispenser. A value less than or equal to zero will reset
     *               the heat and remove the dispenser from tracking.
     * @throws IllegalArgumentException If the dispenser weapon's block is not of type {@link Material#DISPENSER}.
     */
    public void setDispenserHeat(DispenserWeapon dispenserWeapon, int amount) {
        Block dispenser = dispenserWeapon.block();
        if (dispenserWeapon.block().getType() != Material.DISPENSER) {
            return;
        }
        TileState state = (TileState) dispenser.getState();
        PersistentDataContainer dataContainer = state.getPersistentDataContainer();

        // This resets the dispenser's tile state if the plugin did not track it due to a crash or a bug.
        if (trackedDispensers.contains(dispenserWeapon)) {
            dataContainer.remove(key);
        }

        if (dataContainer.has(key)) {
            amount += dataContainer.get(key, PersistentDataType.INTEGER);
        }

        // Cleans the data container and the list of tracked dispensers.
        if (amount <= 0) {
            amount = 0;
            trackedDispensers.remove(dispenser);
            dataContainer.remove(key);
        }

        dataContainer.set(key, PersistentDataType.INTEGER, amount);
        state.update();
        trackedDispensers.add(dispenserWeapon);
    }

    private void coolDispensers(Weapon weapon) {
        if (trackedDispensers.isEmpty()) {
            return;
        }

        for (DispenserWeapon dispenser : trackedDispensers) {
            // Negative value as it is removing heat
            setDispenserHeat(dispenser, -1 * weapon.getHeatDissipation());
        }
    }

    private void setHeatFromForest(List<List<DispenserWeapon>> forest, Weapon weapon) {
        for (List<DispenserWeapon> dispenserTree : forest) {
            System.out.println("Dispenser tree size: " + dispenserTree.size());
            for (DispenserWeapon dispenser : dispenserTree) {
                setDispenserHeat(dispenser, dispenserTree.size() * weapon.getHeatRate());
            }
        }
    }

    public static NamespacedKey getKey() {
        return key;
    }

    public Map<Material, DispenserGraph> getWeapons() {
        return weapons;
    }

    public void addWeapon(Weapon weapon) {
        weapons.put(weapon.getMaterial(), new DispenserGraph(weapon));
    }
}
