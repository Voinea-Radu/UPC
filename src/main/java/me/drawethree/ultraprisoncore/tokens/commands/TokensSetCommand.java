package me.drawethree.ultraprisoncore.tokens.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.tokens.BananaPrisonTokens;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class TokensSetCommand extends TokensCommand {

    public TokensSetCommand(BananaPrisonTokens plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ImmutableList<String> args) {

        if(!sender.isOp()) {
            return false;
        }

        if(args.size() == 2) {
            try {
                long amount = Long.parseLong(args.get(1));
                OfflinePlayer target = Players.getOfflineNullable(args.get(0));
                plugin.getTokensManager().setTokens(target, amount, sender);
                return true;
            } catch (Exception e) {
                sender.sendMessage(plugin.getMessage("not_a_number").replace("%input%", String.valueOf(args.get(0))));
            }
        }
        return false;
    }
}
