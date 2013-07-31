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

import java.io.IOException;
import java.util.logging.Level;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

/**
 *
 * @author daboross
 */
public class RankDisplayPlugin extends JavaPlugin implements Listener {

    private static final String MAIN_COLOR = ChatColor.WHITE.toString();
    private static final String RANK_COLOR = ChatColor.RED.toString();
    private static final String NAME_COLOR = ChatColor.BLUE.toString();
    private static final String ERROR_COLOR = ChatColor.DARK_RED.toString();
    private static final String COMMAND_COLOR = ChatColor.GREEN.toString();
    private Permission permissionHandler;

    @Override
    public void onEnable() {
        setupVault();
        Bukkit.getPluginManager().registerEvents(this, this);
        try {
            new MetricsLite(this).start();
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "Unable to create metrics");
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rank")) {
            if (args.length == 0) {
                sender.sendMessage(MAIN_COLOR + "You are currently " + RANK_COLOR + getRankName(sender));
            } else if (args.length != 1) {
                sender.sendMessage(ERROR_COLOR + "To Many Arguments");
                return false;
            } else {
                Player player = getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ERROR_COLOR + "Player " + NAME_COLOR + args[0] + ERROR_COLOR + " not found or not online");
                    sender.sendMessage(ERROR_COLOR + "For better name matching, use:");
                    sender.sendMessage(COMMAND_COLOR + "/pd i " + NAME_COLOR + args[0]);
                } else {
                    sender.sendMessage(NAME_COLOR + player.getName() + MAIN_COLOR + " is currently " + RANK_COLOR + getRankName(player));
                }
            }
            return true;
        }
        sender.sendMessage(ERROR_COLOR + "Command Error!");
        return false;
    }

    private Player getPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(name.toLowerCase()) || ChatColor.stripColor(p.getDisplayName()).toLowerCase().contains(name.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    private String getRankName(CommandSender sender) {
        if (sender instanceof Player) {
            return getRankName((Player) sender);
        } else {
            return "NonPlayer";
        }
    }

    private String getRankName(Player player) {
        return getFormattedRank(permissionHandler.getPlayerGroups((String) null, player.getName()));
    }

    private String getFormattedRank(String[] strings) {
        if (strings.length == 0) {
            return "None";
        }
        if (strings.length == 1) {
            return strings[0];
        }
        StringBuilder builder = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            builder.append(", ").append(strings[i]);
        }
        return builder.toString();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent pje) {
        Player p = pje.getPlayer();
        if (!p.hasPlayedBefore()) {
            p.chat("/spawn");
        }
    }

    private void setupVault() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permissionHandler = rsp.getProvider();
        if (permissionHandler == null) {
            getLogger().log(Level.SEVERE, "Vault found, but Permission handler not found!");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            getLogger().log(Level.INFO, "Vault and Permission handler found.");
        }
    }
}
