package me.uselessmnemonic.griefdefenderfix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class SanitizeExecutor implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {

        Player player;

        // user targets themselves
        if (commandSender instanceof Player && args.length == 0) {
            player = (Player)commandSender;
        }

        // user is OP or issuing from console
        else if (commandSender.isOp() && args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + "Player does not exists or is offline.");
                return true;
            }
        }

        // user is not OP
        else if (!commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You may only sanitize your own inventory.");
            return true;
        }

        else return false;

        Fix.sanitize(player.getInventory());
        player.updateInventory();

        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {

        // ordinary players may target only themselves
        if (!commandSender.isOp()) {
            return Collections.emptyList();
        }

        // ops can get username suggestions
        else if (args.length == 1) {
            return null;
        }

        // do not autocomplete if > 1 arguments
        else return null;
    }
}
