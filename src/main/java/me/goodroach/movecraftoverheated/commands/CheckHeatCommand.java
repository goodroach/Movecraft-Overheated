package me.goodroach.movecraftoverheated.commands;

import me.goodroach.movecraftoverheated.tracking.DispenserWeapon;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.util.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static me.goodroach.movecraftoverheated.MovecraftOverheated.dispenserHeatUUID;
import static me.goodroach.movecraftoverheated.MovecraftOverheated.heatKey;

public class CheckHeatCommand implements CommandExecutor {
    private final WeaponHeatManager heatManager;

    public CheckHeatCommand(WeaponHeatManager heatManager) {
        this.heatManager = heatManager;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;
        Component baseMessage = ChatUtils.commandPrefix();

        RayTraceResult result = player.rayTraceBlocks(5, FluidCollisionMode.NEVER);
        if (result == null || result.getHitBlock() == null) {
            baseMessage = baseMessage.append(Component.text("You are not looking at a solid block."));
            player.sendMessage(baseMessage);
            return true;
        }

        Block dispenser = result.getHitBlock();
        if (dispenser.getType() != Material.DISPENSER) {
            baseMessage = baseMessage.append(Component.text("This block is not a dispenser."));
            player.sendMessage(baseMessage);
            return true;
        }

        TileState state = (TileState) dispenser.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();
        if (container.get(heatKey, PersistentDataType.INTEGER) == null) {
            baseMessage = baseMessage.append(Component.text("Current dispenser heat at: 0"));
            player.sendMessage(baseMessage);
            return true;
        }

        int heat = container.get(heatKey, PersistentDataType.INTEGER);
        baseMessage = baseMessage.append(Component.text("Current dispenser heat at: "))
            .append(Component.text(heat));

        // Get UUID and attach it to the message
        String uuidString = container.get(dispenserHeatUUID, PersistentDataType.STRING);
        if (uuidString == null) {
            baseMessage = baseMessage.append(Component.text("\nUUID: Not found in dispenser data."));
            player.sendMessage(baseMessage);
            return true;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
            baseMessage = baseMessage.append(Component.text("\nUUID: ")).append(Component.text(uuid.toString()));
        } catch (IllegalArgumentException e) {
            baseMessage = baseMessage.append(Component.text("\nInvalid UUID format: " + uuidString));
            player.sendMessage(baseMessage);
            return true;
        }

        // Get location and attach it to the message
        Map<UUID, DispenserWeapon> trackedDispensers = heatManager.getTrackedDispensers();
        DispenserWeapon trackedDispenser = trackedDispensers.get(uuid);

        if (trackedDispenser == null) {
            baseMessage = baseMessage.append(Component.text("\nNo tracked dispenser found for UUID: " + uuid));
            player.sendMessage(baseMessage);
            return true;
        }

        Location location = trackedDispenser.getLocation();
        baseMessage = baseMessage.append(Component.text("\nDispenser Location: "))
            .append(Component.text(location.toString()));

        player.sendMessage(baseMessage);
        return true;
    }
}