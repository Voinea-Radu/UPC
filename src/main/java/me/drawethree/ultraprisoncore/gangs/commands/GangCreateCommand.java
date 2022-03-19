package me.drawethree.ultraprisoncore.gangs.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.gangs.BananaPrisonGangs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangCreateCommand extends GangCommand {

    @Override
    public String getUsage() {
        return ChatColor.RED + "/gang create [gang]";
    }

    public GangCreateCommand(BananaPrisonGangs plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ImmutableList<String> args) {
        if (sender instanceof Player && args.size() == 1) {
            return this.plugin.getGangsManager().createGang(args.get(0),(Player) sender);
        }
        return false;
    }
}
