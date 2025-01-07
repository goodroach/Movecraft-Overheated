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
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.craftHeatKey;

public class DispenserWeapon {
    private final Vector vector;
    private final Location absolute;
    private final UUID uuid;
    private TrackedLocation tracked;
    private Craft craft;

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
