package me.goodroach.movecraftoverheated.commands;

import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.util.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
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

import static me.goodroach.movecraftoverheated.MovecraftOverheated.heatKey;

public class CheckHeatCommand implements CommandExecutor {

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
        player.sendMessage(baseMessage);

        return true;
    }
}
