package me.goodroach.movecraftoverheated.commands;

import me.goodroach.movecraftoverheated.tracking.DispenserWeapon;
import me.goodroach.movecraftoverheated.tracking.WeaponHeatManager;
import net.countercraft.movecraft.util.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

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

        Block facingBlock = dispenser.getRelative(((Dispenser) dispenser.getBlockData()).getFacing());
        Vector nodeLoc = facingBlock.getLocation().toVector();
        DispenserWeapon read = new DispenserWeapon(nodeLoc, dispenser.getLocation());
        System.out.println(heatManager.getTrackedDispensers().size());
        int heat = heatManager.getTrackedDispensers().get(read.hashCode()).getHeatValue();
        baseMessage = baseMessage.append(Component.text("Current dispenser heat at: "))
            .append(Component.text(heat));
        player.sendMessage(baseMessage);

        return true;
    }
}
