package me.goodroach.movecraftoverheated.weapons;

import me.goodroach.movecraftoverheated.util.SerializationUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record Weapon(
        Material material,
        byte[][] directions,
        int heatRate,
        int heatDissipation
) implements ConfigurationSerializable {

    public Weapon(
        Material material,
        byte[][] directions,
        int heatRate,
        int heatDissipation
    ) {
        this.material = material;
        this.directions = directions;
        this.heatRate = heatRate;
        this.heatDissipation = heatDissipation;
    }

    @Deprecated(forRemoval = true)
    public Material getMaterial() {
        return material;
    }

    @Deprecated(forRemoval = true)
    public byte[][] getDirections() {
        return directions;
    }

    @Deprecated(forRemoval = true)
    public int getHeatRate() {
        return heatRate;
    }

    @Deprecated(forRemoval = true)
    public int getHeatDissipation() {
        return heatDissipation;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "Material", this.material(),
                "Directions", SerializationUtil.serialize2dByteArray(this.directions()),
                "HeatRate", this.heatRate(),
                "HeatDissipation", this.heatDissipation()
        );
    }

    public static @NotNull Weapon deserialize(@NotNull Map<String, Object> args) {
        return new Weapon(
                Material.valueOf(String.valueOf(args.getOrDefault("Material", "BEDROCK"))),
                SerializationUtil.deserialize2dByteArray(args.getOrDefault("Directions", null)),
                NumberConversions.toInt(args.get("HeatRate")),
                NumberConversions.toInt(args.get("HeatDissipation"))
        );
    }
}
