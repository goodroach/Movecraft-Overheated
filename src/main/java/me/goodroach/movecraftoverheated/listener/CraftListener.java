package me.goodroach.movecraftoverheated.listener;

import me.goodroach.movecraftoverheated.tracking.DispenserWeapon;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.events.CraftPilotEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftListener implements Listener {
    private final WeaponHeatManager heatManager;

    public CraftListener(WeaponHeatManager heatManager) {
        this.heatManager = heatManager;
    }

    @EventHandler
    public void onCraftPilot(CraftDetectEvent event){
        for (DispenserWeapon dispenser : heatManager.getTrackedDispensers().values()) {
            dispenser.bindToCraft(event.getCraft());
        }
    }

    // Copy from sign change code in movecraft code
    @EventHandler
    public void onCraftPilot(@NotNull CraftPilotEvent event) {
        // Walk through all signs and set a UUID in there
        final Craft craft = event.getCraft();

        // Now, find all signs on the craft...
        for (MovecraftLocation mLoc : craft.getHitBox()) {
            Block block = mLoc.toBukkit(craft.getWorld()).getBlock();
            // Only interested in signs, if no sign => continue
            // TODO: Just limit to signs?
            // Edit: That's useful for dispensers too to flag TNT and the like, but for that one could use a separate listener
            if (!(block.getState() instanceof Dispenser))
                continue;
            // Sign located!
            Dispenser tile = (Dispenser) block.getState();

            craft.markTileStateWithUUID(tile);
            tile.update();
        }
    }
}
