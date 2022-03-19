package me.drawethree.ultraprisoncore.gangs.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.gangs.BananaPrisonGangs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangAcceptCommand extends GangCommand {

    @Override
    public String getUsage() {
        return ChatColor.RED + "/gang accept";
    }

    public GangAcceptCommand(BananaPrisonGangs plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ImmutableList<String> args) {
        if (sender instanceof Player && args.size() == 0) {
            return this.plugin.getGangsManager().acceptInvite((Player) sender);
        }
        return false;
    }
}
