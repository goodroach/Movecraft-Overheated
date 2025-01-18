package me.goodroach.movecraftoverheated.tracking;

import me.goodroach.movecraftoverheated.config.Keys;
import net.countercraft.movecraft.MovecraftLocation;
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
import java.util.Set;
import java.util.UUID;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.craftHeatKey;
import static me.goodroach.movecraftoverheated.config.Keys.BASE_HEAT_CAPACITY;

public class DispenserWeapon {
    private final Vector vector;
    private final Location absolute;
    private final UUID uuid;
    private WeakReference<TrackedLocation> tracked = new WeakReference<>(null);
    private WeakReference<Craft> craft = new WeakReference<>(null);
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

        Craft alreadyKnownCraft = getCraft();

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

        Set<TrackedLocation> trackedLocationSet = craft.getTrackedLocations().computeIfAbsent(craftHeatKey, key -> new HashSet<>());
        MovecraftLocation absoluteMovecraft = MathUtils.bukkit2MovecraftLoc(absolute);
        TrackedLocation trackedLocation = null;
        // TODO: Test performance! Possibly move this to a async task...
        for (TrackedLocation known : trackedLocationSet) {
            if (known.getAbsoluteLocation().equals(absoluteMovecraft)) {
                trackedLocation = known;
                break;
            }
        }
        if (trackedLocation == null) {
            trackedLocation = new TrackedLocation(craft, absoluteMovecraft);
            if (trackedLocationSet.add(trackedLocation)) {
                this.tracked = new WeakReference<>(trackedLocation);
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public Location getLocation() {
        if (getTrackedLocation() == null || getCraft() == null) {
            return absolute;
        }
        return getTrackedLocation().getAbsoluteLocation().toBukkit(getCraft().getWorld());
    }

    @Nullable
    protected TrackedLocation getTrackedLocation() {
        return this.tracked.get();
    }

    @Nullable
    public Craft getCraft() {
        return this.craft.get();
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
