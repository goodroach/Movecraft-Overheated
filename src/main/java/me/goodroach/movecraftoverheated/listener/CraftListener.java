package me.goodroach.movecraftoverheated.listener;

import me.goodroach.movecraftoverheated.tracking.DispenserWeapon;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.SubCraft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftListener implements Listener {
    private final WeaponHeatManager heatManager;

    public CraftListener(WeaponHeatManager heatManager) {
        this.heatManager = heatManager;
    }

    @EventHandler
    public void onCraftPilot(CraftDetectEvent event) {
        for (DispenserWeapon dispenser : heatManager.getTrackedDispensers().values()) {
            // Only care about those within the move box, otherwise this might be overkill
            if (event.getCraft().getHitBox().inBounds(dispenser.getLocation().getX(), dispenser.getLocation().getY(), dispenser.getLocation().getZ())) {
                dispenser.bindToCraft(event.getCraft());
            }
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraftRelease(CraftReleaseEvent event) {
        if (event.getReason() != CraftReleaseEvent.Reason.SUB_CRAFT) {
            return;
        }
        Craft released = event.getCraft();
        if (!(released instanceof SubCraft)) {
            return;
        }
        SubCraft releasedSubCraft = (SubCraft) released;
        if (releasedSubCraft.getParent() == null) {
            return;
        }

        // Now, transfer back the tracking UUID to the parentcraft...
        // Now, find all signs on the craft...
        // TODO @DerToaster98: Include this change in base movecraft!
        for (MovecraftLocation mLoc : releasedSubCraft.getHitBox()) {
            Block block = mLoc.toBukkit(releasedSubCraft.getWorld()).getBlock();
            // Only interested in signs, if no sign => continue
            // Edit: That's useful for dispensers too to flag TNT and the like, but for that one could use a separate listener
            if (!(block.getState() instanceof Dispenser))
                continue;
            // Sign located!
            Dispenser tile = (Dispenser) block.getState();

            releasedSubCraft.getParent().markTileStateWithUUID(tile);
            tile.update();
        }

    }
}
