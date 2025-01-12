package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.config.Keys;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.SubCraft;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.craftHeatKey;
import static me.goodroach.movecraftoverheated.config.Keys.BASE_HEAT_CAPACITY;

public class DispenserWeapon {
    private final Vector vector;
    private final Location absolute;
    private final UUID uuid;
    private TrackedLocation tracked;
    private WeakReference<Craft> craft;
    private int heatValue;
    private int heatCapacity;

    public DispenserWeapon(Vector vector, Location absolute) {
        this.vector = vector;
        this.absolute = absolute;
        uuid = UUID.randomUUID();
    }

    public Vector getVector() {
        return vector;
    }

    public boolean bindToCraft(@Nullable Craft craft) {
        if (craft == null) {
            craft = MathUtils.getCraftByPersistentBlockData(absolute);
        }
        if (craft == null) {
            return false;
        }

        // TODO: Create a getter for this!
        Craft alreadyKnownCraft = this.craft == null ? null : this.craft.get();

        // we are already bound to this craft!
        if (alreadyKnownCraft != null && alreadyKnownCraft.getUUID().equals(craft.getUUID()) && this.tracked != null) {
            return true;
        }

        // Movecraft itself shoudl do the transition stuff! We should not care about it at all!
        if (craft instanceof SubCraft subCraft && alreadyKnownCraft == subCraft.getParent() && subCraft.getParent() != null) {
            // We only need to update our craft reference, movecraft itself cares about updating the tracked location!
            this.craft = new WeakReference<>(craft);
            return true;
        }

        this.craft = new WeakReference<>(craft);
        this.tracked = new TrackedLocation(craft, MathUtils.bukkit2MovecraftLoc(absolute));
        craft.getTrackedLocations().computeIfAbsent(craftHeatKey, key -> new HashSet<>()).add(tracked);
        return true;
    }

    public Location getLocation() {
        if (tracked == null || this.craft.get() == null) {
            return absolute;
        }
        return tracked.getAbsoluteLocation().toBukkit(craft.get().getWorld());
    }

    public UUID getUuid() {
        return uuid;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DispenserWeapon that = (DispenserWeapon) obj;
        return this.getLocation().equals(that.getLocation()) && this.getVector().equals(that.getVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getVector());
    }
}
