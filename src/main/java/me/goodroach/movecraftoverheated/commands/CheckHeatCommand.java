package me.goodroach.movecraftoverheated.commands;

import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.commands.MovecraftCommand;
import net.countercraft.movecraft.util.ChatUtils;
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

        RayTraceResult result = player.rayTraceBlocks(5, FluidCollisionMode.NEVER);
        if (result == null || result.getHitBlock() == null) {
            player.sendMessage(ChatUtils.errorPrefix() + "You are not looking at a solid block.");
            return true;
        }

        Block dispenser = result.getHitBlock();
        if (dispenser.getType() != Material.DISPENSER) {
            player.sendMessage(ChatUtils.errorPrefix() + "This block is not a dispenser.");
            return true;
        }

        TileState state = (TileState) dispenser.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();
        if (container.get(WeaponHeatManager.getKey(), PersistentDataType.INTEGER) == null) {
            player.sendMessage(ChatUtils.commandPrefix() + "Current dispenser heat at: 0");
            return true;
        }

        int heat = container.get(WeaponHeatManager.getKey(), PersistentDataType.INTEGER);
        player.sendMessage(ChatUtils.commandPrefix() + "Current dispenser heat at: " + heat);

        return true;
    }
}
