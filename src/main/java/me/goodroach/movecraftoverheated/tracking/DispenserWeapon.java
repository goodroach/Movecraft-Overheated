package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.config.Keys;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.craftHeatKey;
import static me.goodroach.movecraftoverheated.config.Keys.BASE_HEAT_CAPACITY;

public class DispenserWeapon {
    private final Vector vector;
    private final Location absolute;
    private TrackedLocation tracked;
    private Craft craft;
    private int heatValue;
    private int heatCapacity;

    public DispenserWeapon(Vector vector, Location absolute) {
        this.vector = vector;
        this.absolute = absolute;
        this.heatValue = 0;
        this.heatCapacity = 0;
    }

    public Vector getVector() {
        return vector;
    }

    public boolean bindToCraft(@Nullable Craft craft) {
        // First check looks for a craft in case the input is null.
        if (craft == null) {
            craft = MathUtils.fastNearestCraftToLoc(CraftManager.getInstance().getCrafts(), absolute);
        }
        // Second check ensures that there is a craft within the vicinity.
        if (craft == null) {
            return false;
        }
        if (!MathUtils.locationInHitBox(craft.getHitBox(), absolute)) {
            return false;
        }

        this.craft = craft;
        this.tracked = new TrackedLocation(craft, MathUtils.bukkit2MovecraftLoc(absolute));
        if (craft.getTrackedLocations().get(craftHeatKey) == null) {
            craft.getTrackedLocations().put(craftHeatKey, new HashSet<>());
        }
        craft.getTrackedLocations().get(craftHeatKey).add(tracked);
        return true;
    }

    public Location getLocation() {
        if (tracked == null) {
            return absolute;
        }
        return tracked.getAbsoluteLocation().toBukkit(craft.getWorld());
    }

    public int getHeatValue() {
        return heatValue;
    }

    public void addHeatValue(int amount) {
        if (heatValue + amount <= 0) {
            heatValue = 0;
        }
        heatValue += amount;
    }

    public void setHeatValue(int amount) {
        heatValue = amount;
    }

    public int getHeatCapacity() {
        return heatCapacity;
    }

    public void setHeatCapacity(int newCapacity) {
        this.heatCapacity = newCapacity;
    }

    // These two methods are absolutely necessary to ensure dispensers can heat up multiple times
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DispenserWeapon that = (DispenserWeapon) obj;
        Location thisLocation = this.getLocation();
        Location thatLocation = that.getLocation();
        boolean locationsEqual = (thisLocation != null && thisLocation.equals(thatLocation));
        boolean vectorsEqual = (this.vector != null && this.vector.equals(that.vector));
        return locationsEqual && vectorsEqual;
    }

    @Override
    public int hashCode() {
        // Use both the Location and the Vector to compute the hash code
        Location currentLocation = this.getLocation();
        int locationHash = (currentLocation != null) ? currentLocation.hashCode() : 0;
        int vectorHash = (this.vector != null) ? this.vector.hashCode() : 0;
        return 31 * locationHash + vectorHash;
    }
}
