package me.drawethree.ultraprisoncore.gems.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.gems.BananaPrisonGems;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public abstract class GemsCommand {

    public static final HashMap<String, GemsCommand> commands;

    static {
        commands = new HashMap<>();
        commands.put("give", new GemsGiveCommand(BananaPrisonGems.getInstance()));
        commands.put("remove", new GemsRemoveCommand(BananaPrisonGems.getInstance()));
        commands.put("set", new GemsSetCommand(BananaPrisonGems.getInstance()));
        commands.put("help", new GemsHelpCommand(BananaPrisonGems.getInstance()));
        commands.put("pay", new GemsPayCommand(BananaPrisonGems.getInstance()));
    }

    protected BananaPrisonGems plugin;

    public GemsCommand(BananaPrisonGems plugin) {

        this.plugin = plugin;
    }

    public abstract boolean execute(CommandSender sender, ImmutableList<String> args);

    public static GemsCommand getCommand(String arg) {
        return commands.get(arg.toLowerCase());
    }
}
