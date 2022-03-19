package me.drawethree.ultraprisoncore.ranks.api;

import me.drawethree.ultraprisoncore.ranks.BananaPrisonRankup;
import me.drawethree.ultraprisoncore.ranks.rank.Prestige;
import me.drawethree.ultraprisoncore.ranks.rank.Rank;
import org.bukkit.entity.Player;

public class BananaPrisonRankupAPIImpl implements BananaPrisonRankupAPI {

    private BananaPrisonRankup plugin;

    public BananaPrisonRankupAPIImpl(BananaPrisonRankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public Rank getPlayerRank(Player p) {
        return plugin.getRankManager().getPlayerRank(p);
    }

    @Override
    public Prestige getPlayerPrestige(Player p) {
        return plugin.getRankManager().getPlayerPrestige(p);
    }

    @Override
    public Rank getNextPlayerRank(Player player) {
        return plugin.getRankManager().getNextRank(this.getPlayerRank(player).getId());
    }

    @Override
    public int getRankupProgress(Player player) {
        return plugin.getRankManager().getRankupProgress(player);
    }
}
