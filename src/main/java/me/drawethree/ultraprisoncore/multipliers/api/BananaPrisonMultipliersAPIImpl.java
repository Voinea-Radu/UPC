package me.drawethree.ultraprisoncore.multipliers.api;

import me.drawethree.ultraprisoncore.multipliers.BananaPrisonMultipliers;
import org.bukkit.entity.Player;

public class BananaPrisonMultipliersAPIImpl implements BananaPrisonMultipliersAPI {

    private final BananaPrisonMultipliers module;

    public BananaPrisonMultipliersAPIImpl(BananaPrisonMultipliers plugin) {

        this.module = plugin;
    }

    @Override
    public double getGlobalMultiplier() {
        return module.getGlobalMultiplier();
    }

    @Override
    public double getVoteMultiplier(Player p) {
        return module.getPersonalMultiplier(p);
    }

    @Override
    public double getRankMultiplier(Player p) {
        return module.getRankMultiplier(p);
    }

    @Override
    public double getArmourMultiplier(Player p) {
        return module.getArmourMultiplier(p);
    }

    @Override
    public double getPlayerMultiplier(Player p) {
        return (this.getArmourMultiplier(p) + 100) * (this.getGlobalMultiplier() + this.getVoteMultiplier(p) + this.getRankMultiplier(p));
    }


}
