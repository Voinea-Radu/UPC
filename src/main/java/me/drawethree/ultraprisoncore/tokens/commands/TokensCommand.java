package me.drawethree.ultraprisoncore.tokens.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.tokens.BananaPrisonTokens;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public abstract class TokensCommand {

    public static final HashMap<String, TokensCommand> commands;

    static {
        commands = new HashMap<>();
        commands.put("give", new TokensGiveCommand(BananaPrisonTokens.getInstance()));
        commands.put("pay", new TokensPayCommand(BananaPrisonTokens.getInstance()));
        commands.put("remove", new TokensRemoveCommand(BananaPrisonTokens.getInstance()));
        commands.put("set", new TokensSetCommand(BananaPrisonTokens.getInstance()));
        commands.put("withdraw", new TokensWithdrawCommand(BananaPrisonTokens.getInstance()));
        commands.put("help", new TokensHelpCommand(BananaPrisonTokens.getInstance()));
    }

    protected BananaPrisonTokens plugin;

    public TokensCommand(BananaPrisonTokens plugin) {

        this.plugin = plugin;
    }

    public abstract boolean execute(CommandSender sender, ImmutableList<String> args);

    public static TokensCommand getCommand(String arg) {
        return commands.get(arg.toLowerCase());
    }
}
