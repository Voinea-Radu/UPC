package me.drawethree.ultraprisoncore.gangs.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.gangs.BananaPrisonGangs;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public abstract class GangCommand {

    public static final HashMap<String, GangCommand> commands;

    static {
        commands = new HashMap<>();
        commands.put("help", new GangHelpCommand(BananaPrisonGangs.getInstance()));
        commands.put("info", new GangInfoCommand(BananaPrisonGangs.getInstance()));
        commands.put("create", new GangCreateCommand(BananaPrisonGangs.getInstance()));
        commands.put("invite", new GangInviteCommand(BananaPrisonGangs.getInstance()));
        commands.put("accept", new GangAcceptCommand(BananaPrisonGangs.getInstance()));
        commands.put("leave", new GangLeaveCommand(BananaPrisonGangs.getInstance()));
        commands.put("disband", new GangDisbandCommand(BananaPrisonGangs.getInstance()));
    }

    protected BananaPrisonGangs plugin;

    public abstract String getUsage();

    public GangCommand(BananaPrisonGangs plugin) {
        this.plugin = plugin;
    }

    public abstract boolean execute(CommandSender sender, ImmutableList<String> args);

    public static GangCommand getCommand(String arg) {
        return commands.get(arg.toLowerCase());
    }
}
