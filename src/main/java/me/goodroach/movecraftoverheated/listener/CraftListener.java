package me.goodroach.movecraftoverheated.listener;

import me.goodroach.movecraftoverheated.tracking.DispenserWeapon;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.events.CraftDetectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
}
