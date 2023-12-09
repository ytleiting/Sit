package com.ytleiting.sit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static com.ytleiting.sit.Sit.sitPlugin;

public class SitCommand implements CommandExecutor {
    String disabledMessage = sitPlugin.getConfig().getString("sit-command.disabled-message");
    String nonPlayerMessage = sitPlugin.getConfig().getString("sit-command.sender-is-not-player-message");
    String sitOnAirMessage = sitPlugin.getConfig().getString("sit-command.not-on-ground-message");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sitPlugin.getConfig().getBoolean("sit-command.enable-command")) {
            sender.sendMessage(disabledMessage);
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(nonPlayerMessage);
            return true;
        }
        if (!((Entity) player).isOnGround()) {
            player.sendMessage(sitOnAirMessage);
            return true;
        }
        Location location = player.getLocation().subtract(0, 0.2, 0);
        Sit.sitDown(location, player);

        return true;
    }
}
