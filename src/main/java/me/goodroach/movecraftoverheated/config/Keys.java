
package me.goodroach.movecraftoverheated.config;

import me.goodroach.movecraftoverheated.MovecraftOverheated;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.TypeData;
import net.countercraft.movecraft.craft.type.property.BooleanProperty;
import net.countercraft.movecraft.craft.type.property.DoubleProperty;
import net.countercraft.movecraft.craft.type.property.ObjectPropertyImpl;
import net.countercraft.movecraft.util.Tags;
import net.countercraft.movecraft.util.functions.QuadFunction;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.util.NumberConversions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Keys {
    public static final NamespacedKey BASE_HEAT_CAPACITY = build("base_heat_capacity");
    public static final NamespacedKey HEAT_CAPACITY_PER_BLOCK = build("heat_capacity_per_block");
    public static final NamespacedKey BASE_HEAT_DISSIPATION = build("base_heat_dissipation");
    public static final NamespacedKey HEAT_DISSIPATION_PER_BLOCK = build("heat_dissipation_per_block");
    public static final NamespacedKey WEAPON_HEAT_RATE_MULTIPLIER = build("weapon_heat_rate_multiplier");
    public static final NamespacedKey WEAPON_HEAT_DISSAPATION_MULTIPLIER = build("weapon_heat_dissipation_multiplier");
    public static final NamespacedKey USE_HEAT = build("use_heat");

    public static void register() {
        CraftType.registerProperty(new DoubleProperty("BaseHeatCapacity", BASE_HEAT_CAPACITY, craftType -> 300.0));
        CraftType.registerProperty(new DoubleProperty("CapacityPerBlock", HEAT_CAPACITY_PER_BLOCK, craftType -> 0.1));
        CraftType.registerProperty(new DoubleProperty("BaseHeatDissipation", BASE_HEAT_DISSIPATION, craftType -> 5.0));
        CraftType.registerProperty(new DoubleProperty("DissipationPerBlock", HEAT_DISSIPATION_PER_BLOCK, craftType -> 0.005));
        CraftType.registerProperty(new BooleanProperty("UseHeat", USE_HEAT, craftType -> false));
        CraftType.registerProperty(
                new ObjectPropertyImpl("WeaponHeatRateMultiplier", WEAPON_HEAT_RATE_MULTIPLIER,
                        materialDoubleMap,
                        type -> {
                            return Map.<Material, Double>of();
                        }
                )
        );
        CraftType.registerProperty(
                new ObjectPropertyImpl("WeaponHeatDissipationMultiplier", WEAPON_HEAT_DISSAPATION_MULTIPLIER,
                        materialDoubleMap,
                        type -> {
                            return Map.<Material, Double>of();
                        }
                )
        );
    }

    private static QuadFunction<TypeData, CraftType, String, NamespacedKey, Object> materialDoubleMap = (data, type, fileKey, namespacedKey) -> {
        final Map<Material, Double> multiplierMap = new HashMap<>();

        // Grab backing data
        Map<String, Object> map = data.getData(fileKey).getBackingData();
        // If empty, we can quit early
        if (map == null || map.isEmpty()) {
            return multiplierMap;
        }
        // Iterate through all entries and try to parse them
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Support block lists; should be moved to separate method eventually
            EnumSet<Material> materials = EnumSet.noneOf(Material.class);
            String[] splitted = entry.getKey().split(",");
            for (String s : splitted) {
                try {
                    materials.addAll(Tags.parseMaterials(s));
                } catch(IllegalArgumentException iae) {
                    MovecraftOverheated.getInstance().getLogger().warning(iae.getMessage());
                }
            }
            if (materials.isEmpty()) {
                continue;
            }

            Object value = entry.getValue();
            double actualValue;
            try {
                actualValue = NumberConversions.toDouble(value);
            } catch(NullPointerException | NumberFormatException exception) {
                MovecraftOverheated.getInstance().getLogger().warning("Value defined for entry " + entry.toString() + " is not valid!");
                continue;
            }

            for (Material material : materials) {
                multiplierMap.put(material, actualValue);
            }
        }

        return multiplierMap;
    };

    private static NamespacedKey build (String key) {return new NamespacedKey("movecraft-overheat", key);}
}
