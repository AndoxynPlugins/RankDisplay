
package net.daboross.bukkitdev.rankdisplay;

//import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
public class RankDisplay extends JavaPlugin /*implements Listener*/ {

    private static final String MAIN_COLOR = ChatColor.AQUA.toString();
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
                sender.sendMessage("You are currently " + RANK_COLOR + getRankName(sender));
            } else {
                Player player = getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ERROR_COLOR + "Player " + args[0] + " not found.");
                    sender.sendMessage(ERROR_COLOR + "For better name matching, use:");
                    sender.sendMessage(COMMAND_COLOR + "/pd i " + args[0]);
                } else {
                    sender.sendMessage(NAME_COLOR + player.getName() + MAIN_COLOR + " is currently " + RANK_COLOR + getRankName(player));
                }
            }
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
            return strings[1];
        }
        StringBuilder builder = new StringBuilder(strings[0]);
        for (String string : strings) {
            builder.append(", ").append(string);
        }
        return builder.toString();
    }

    private PermissionUser getPermUser(Player player) {
        return PermissionsEx.getUser(player);
    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerJoin(PlayerJoinEvent pje) {
//    }
}
