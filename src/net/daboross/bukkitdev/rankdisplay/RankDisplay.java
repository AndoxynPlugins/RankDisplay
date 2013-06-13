package net.daboross.bukkitdev.rankdisplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
public class RankDisplay extends JavaPlugin implements Listener {

    private static final String MAIN_COLOR = ChatColor.WHITE.toString();
    private static final String RANK_COLOR = ChatColor.RED.toString();
    private static final String NAME_COLOR = ChatColor.BLUE.toString();
    private static final String ERROR_COLOR = ChatColor.DARK_RED.toString();
    private static final String COMMAND_COLOR = ChatColor.GREEN.toString();

    @Override
    public void onEnable() {
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
                    sender.sendMessage(ERROR_COLOR + "Player " + NAME_COLOR + args[0] + ERROR_COLOR + " not found.");
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
        return Bukkit.getServer().getPlayer(name);
    }

    private String getRankName(CommandSender sender) {
        if (sender instanceof Player) {
            return getFormattedRank(getPermUser((Player) sender).getGroupsNames());
        } else {
            return "NonPlayer";
        }
    }

    private String getRankName(Player player) {
        return getFormattedRank(getPermUser(player).getGroupsNames());
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

    private PermissionUser getPermUser(Player player) {
        return PermissionsEx.getUser(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent pje) {
        Player p = pje.getPlayer();
        if (!p.hasPlayedBefore()) {
            p.chat("/spawn");
        }
    }
}
