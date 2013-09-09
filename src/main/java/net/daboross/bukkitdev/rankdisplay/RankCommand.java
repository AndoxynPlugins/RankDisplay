/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.rankdisplay;

import java.util.logging.Level;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class RankCommand implements CommandExecutor {

    private static final String REG = ChatColor.WHITE.toString();
    private static final String DATA = ChatColor.RED.toString();
    private static final String NAME = ChatColor.GRAY.toString();
    private static final String ERR = ChatColor.DARK_RED.toString();
    private final Permission permissionHandler;

    public RankCommand(Plugin plugin) {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        permissionHandler = rsp.getProvider();
        if (permissionHandler == null) {
            plugin.getLogger().log(Level.SEVERE, "[Rank] Vault permission handler not found");
        }
    }

    public void setup(JavaPlugin plugin) {
        PluginCommand rank = plugin.getCommand("rank");
        if (rank != null) {
            rank.setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (permissionHandler == null) {
            sender.sendMessage(ERR + "Vault not found");
        }
        if (args.length == 0) {
            sender.sendMessage(REG + "You are currently " + DATA + getRankName(sender));
        } else if (args.length != 1) {
            sender.sendMessage(ERR + "To many Arguments");
            return false;
        } else {
            Player player = getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ERR + "Player " + NAME + args[0] + ERR + " not found");
            } else {
                sender.sendMessage(NAME + player.getName() + REG + " is currently " + getRankName(player));
            }
        }
        return true;
    }

    private Player getPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())
                    || ChatColor.stripColor(p.getDisplayName()).toLowerCase().contains(name.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    private String getRankName(CommandSender sender) {
        if (sender instanceof Player) {
            String[] ranks = permissionHandler.getPlayerGroups((String) null, sender.getName());
            if (ranks.length == 0) {
                return DATA + "None";
            }
            if (ranks.length == 1) {
                return DATA + ranks[0];
            }
            StringBuilder builder = new StringBuilder(DATA).append(ranks[0]);
            for (int i = 1; i < ranks.length; i++) {
                builder.append(REG).append(", ").append(DATA).append(ranks[i]);
            }
            return builder.toString();
        } else {
            return DATA + "NonPlayer";
        }
    }
}
