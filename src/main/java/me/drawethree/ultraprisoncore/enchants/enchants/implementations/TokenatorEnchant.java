package me.drawethree.ultraprisoncore.enchants.enchants.implementations;

import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class TokenatorEnchant extends BananaPrisonEnchantment {
    private long maxAmount;
    private long minAmount;
    private double chance;


    public TokenatorEnchant(BananaPrisonEnchants instance) {
        super(instance, 14);
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (chance * enchantLevel >= ThreadLocalRandom.current().nextDouble(100)) {
            long randAmount;
            randAmount = ThreadLocalRandom.current().nextLong(minAmount, maxAmount);
            plugin.getCore().getTokens().getApi().addTokens(e.getPlayer(), randAmount);
        }
    }

    @Override
    public void reload() {
        this.minAmount = plugin.getConfig().get().getLong("enchants." + id + ".Min-Tokens");
        this.maxAmount = plugin.getConfig().get().getLong("enchants." + id + ".Max-Tokens");
        this.chance = plugin.getConfig().get().getDouble("enchants." + id + ".Chance");
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }
}
