package me.goodroach.movecraftoverheated;

import me.goodroach.movecraftoverheated.commands.CheckHeatCommand;
import me.goodroach.movecraftoverheated.config.Settings;
import me.goodroach.movecraftoverheated.listener.WeaponListener;
import me.goodroach.movecraftoverheated.tracking.GraphManager;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import me.goodroach.movecraftoverheated.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MovecraftOverheated extends JavaPlugin {
    private static MovecraftOverheated instance;
    private WeaponHeatManager heatManager;
    private GraphManager graphManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        graphManager = new GraphManager();
        heatManager = new WeaponHeatManager(graphManager);
        heatManager.runTaskTimer(MovecraftOverheated.getInstance(), 0L, 20L); // Run every 20 ticks (1 second)

        //Listeners
        getServer().getPluginManager().registerEvents(new WeaponListener(heatManager), this);

        // Plugin startup logic
        initConfig();

        this.getCommand("checkheat").setExecutor(new CheckHeatCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initConfig() {
        Settings.HeatCheckInterval = getConfig().getInt("HeatCheckInterval", 1000);
        Settings.DisasterCheckInterval = getConfig().getInt("DisasterCheckInterval", 10000);
        if (getConfig().contains("HeatSinkBlocks")) {
            Map<String, Object> tempMap = getConfig().getConfigurationSection("HeatSinkBlocks").getValues(false);
            for(String str : tempMap.keySet()) {
                Material type;
                try {
                    type = Material.getMaterial(str);
                }
                catch(NumberFormatException e) {
                    type = Material.getMaterial(str);
                }
                Settings.HeatSinkBlocks.put(type,(Double)tempMap.get(str));
            }
        }
        if (getConfig().contains("RadiatorBlocks")) {
            Map<String, Object> tempMap = getConfig().getConfigurationSection("RadiatorBlocks").getValues(false);
            for(String str : tempMap.keySet()) {
                Material type;
                try {
                    type = Material.getMaterial(str);
                }
                catch(NumberFormatException e) {
                    type = Material.getMaterial(str);
                }
                Settings.RadiatorBlocks.put(type,(Double)tempMap.get(str));
            }
        }
        if (getConfig().contains("Weapons")) {
            Map<String, Object> tempMap = getConfig().getConfigurationSection("Weapons").getValues(false);
            for (String str : tempMap.keySet()) {
                Material type;
                try {
                    type = Material.getMaterial(str);
                }
                catch(NumberFormatException e) {
                    type = Material.getMaterial(str);
                }

                System.out.println("Material type is: " + type.toString());

                // Get the directions relative to the block the dispenser outputs to.
                ConfigurationSection weaponSection = getConfig().getConfigurationSection("Weapons." + str);
                List<String> directionStrings = weaponSection.getStringList("Directions");
                List<byte[]> tempDirections = new ArrayList<>();
                for (String dir : directionStrings) {
                    try {
                        String[] parts = dir.split(",\\s*"); // Split by comma and optional spaces
                        if (parts.length != 3) {
                            throw new IllegalArgumentException("Invalid vector format: " + str);
                        }
                        byte x = Byte.parseByte(parts[0]);
                        byte y = Byte.parseByte(parts[1]);
                        byte z = Byte.parseByte(parts[2]);
                        tempDirections.add(new byte[] {x, y, z});
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid number in vector: " + str, e);
                    }
                }
                byte[][] directions = new byte[tempDirections.size()][];
                for (int i = 0; i < tempDirections.size(); i++) {
                    directions[i] = tempDirections.get(i);
                }

                Map<String, Object> weaponData = ((ConfigurationSection) tempMap.get(str)).getValues(false);
                int heatRate = (int) weaponData.getOrDefault("HeatRate", 0d);
                int heatDissipation = (int) weaponData.getOrDefault("HeatDissipation", 0d);

                heatManager.addWeapon(new Weapon(type, directions, heatRate, heatDissipation));
            }
        }
    }

    public static MovecraftOverheated getInstance() {
        return instance;
    }
}
