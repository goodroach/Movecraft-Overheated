package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.weapons.Weapon;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.craftHeatKey;

public class DispenserWeapon {
    private final Vector vector;
    private Location absolute;
    private TrackedLocation tracked;
    private Craft craft;

    public DispenserWeapon(Vector vector, Location absolute) {
        this.vector = vector;
        this.absolute = absolute;
    }

    public Vector getVector() {
        return vector;
    }

    public void bindToCraft() {
        Craft craft = MathUtils.fastNearestCraftToLoc(CraftManager.getInstance().getCrafts(), absolute);
        if (!MathUtils.locationInHitBox(craft.getHitBox(), absolute) || craft == null) {
            return;
        }

        this.craft = craft;
        this.tracked = new TrackedLocation(craft, MathUtils.bukkit2MovecraftLoc(absolute));
        if (craft.getTrackedLocations().get(craftHeatKey) == null) {
            craft.getTrackedLocations().put(craftHeatKey, new HashSet<>());
        }
        craft.getTrackedLocations().get(craftHeatKey).add(tracked);
    }

    public Location getLocation() {
        if (tracked == null) {
            return absolute;
        }
        return tracked.getAbsoluteLocation().toBukkit(craft.getWorld());
    }
}
