package me.goodroach.movecraftoverheated.listener;

import me.goodroach.movecraftoverheated.tracking.DispenserGraph;
import me.goodroach.movecraftoverheated.tracking.DispenserWeapon;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.dispenserHeatUUID;
public class WeaponListener implements Listener {
    private WeaponHeatManager heatManager;

    public WeaponListener(WeaponHeatManager heatManager) {
        this.heatManager = heatManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();

        DispenserGraph graph = heatManager.getWeapons().get(item.getType());
        if (graph == null) {
            return;
        }

        Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) {
            return;
        }

        Block facingBlock = block.getRelative(((Dispenser) block.getBlockData()).getFacing());
        Vector nodeLoc = facingBlock.getLocation().toVector();

        TileState state = (TileState) block.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();

        DispenserWeapon dispenserWeapon;

        UUID uuid = null;

        if (container.has(dispenserHeatUUID)) {
            uuid = UUID.fromString(container.get(dispenserHeatUUID, PersistentDataType.STRING));
        }

        if (uuid != null && heatManager.getTrackedDispensers().containsKey(uuid)) {
            dispenserWeapon = heatManager.getTrackedDispensers().get(uuid);
        } else {
            dispenserWeapon = new DispenserWeapon(nodeLoc, block.getLocation());
            container.set(dispenserHeatUUID, PersistentDataType.STRING, dispenserWeapon.getUuid().toString());
            state.update();
        }

        // Bind to craft and add to graph
        dispenserWeapon.bindToCraft(null);
        graph.addDispenser(dispenserWeapon);
    }
}
