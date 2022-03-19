package me.drawethree.ultraprisoncore.tokens.commands;

import com.google.common.collect.ImmutableList;
import me.drawethree.ultraprisoncore.tokens.BananaPrisonTokens;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensPayCommand extends TokensCommand {

    public TokensPayCommand(BananaPrisonTokens plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ImmutableList<String> args) {
        if (args.size() == 2 && sender instanceof Player) {
            Player p = (Player) sender;
            try {
                long amount = Long.parseLong(args.get(1).replace(",", ""));

                if (0 >= amount) {
                    return false;
                }

                OfflinePlayer target = Players.getOfflineNullable(args.get(0));

                if (!target.isOnline()) {
                    sender.sendMessage(plugin.getMessage("player_not_online").replace("%player%", target.getName()));
                    return true;
                }

                plugin.getTokensManager().payTokens(p, amount, target);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("not_a_number").replace("%input%", String.valueOf(args.get(0))));
            }
        }
        return false;
    }
}
