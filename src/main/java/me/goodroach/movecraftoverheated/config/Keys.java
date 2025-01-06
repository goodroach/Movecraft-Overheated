
package me.goodroach.movecraftoverheated.config;

import me.goodroach.movecraftoverheated.MovecraftOverheated;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.TypeData;
import net.countercraft.movecraft.craft.type.property.BooleanProperty;
import net.countercraft.movecraft.craft.type.property.DoubleProperty;
import net.countercraft.movecraft.craft.type.property.ObjectPropertyImpl;
import net.countercraft.movecraft.util.Tags;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Keys {
    public static final NamespacedKey BASE_HEAT_CAPACITY = build("base_heat_capacity");
    public static final NamespacedKey HEAT_CAPACITY_PER_BLOCK = build("heat_capacity_per_block");
    public static final NamespacedKey BASE_HEAT_DISSIPATION = build("base_heat_dissipation");
    public static final NamespacedKey HEAT_DISSIPATION_PER_BLOCK = build("heat_dissipation_per_block");
    public static final NamespacedKey WEAPON_HEAT_RATE_MULTIPLIER = build("weapon_heat_rate_multiplier");
    public static final NamespacedKey WEAPON_HEAT_DISSIPATION_MULTIPLIER = build("weapon_heat_dissipation_multiplier");
    public static final NamespacedKey USE_HEAT = build("use_heat");

    public static void register() {
        CraftType.registerProperty(new DoubleProperty("BaseHeatCapacity", BASE_HEAT_CAPACITY, craftType -> 300.0));
        CraftType.registerProperty(new DoubleProperty("CapacityPerBlock", HEAT_CAPACITY_PER_BLOCK, craftType -> 0.1));
        CraftType.registerProperty(new DoubleProperty("BaseHeatDissipation", BASE_HEAT_DISSIPATION, craftType -> 5.0));
        CraftType.registerProperty(new DoubleProperty("DissipationPerBlock", HEAT_DISSIPATION_PER_BLOCK, craftType -> 0.005));
        CraftType.registerProperty(new BooleanProperty("UseHeat", USE_HEAT, craftType -> false));
    }

    private static NamespacedKey build (String key) {return new NamespacedKey("movecraft-overheat", key);}
}
