package me.goodroach.movecraftoverheated.disaster;

import me.goodroach.movecraftoverheated.util.SerializationUtil;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ExplosionDisaster extends BaseDisaster {
    double randomChance;
    float explosionPower;

    public ExplosionDisaster(Map<String, Object> rawData) {
        super(rawData);
        this.randomChance = NumberConversions.toDouble(rawData.getOrDefault("RandomChance", 0.0));
        this.explosionPower = NumberConversions.toFloat(rawData.getOrDefault("ExplosionPower", 0.0f));
    }

    @NotNull
    @Override
    public Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized) {
        serialized.put("RandomChance", this.randomChance);
        serialized.put("ExplosionPower", this.explosionPower);
        return serialized;
    }

    @Override
    public boolean executeDisaster(Block dispenser, @NotNull Craft craft) {
        System.out.println("This man, explode his balls!");
        return true;
    }
}
