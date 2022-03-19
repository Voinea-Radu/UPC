package me.drawethree.ultraprisoncore.enchants.enchants.implementations;

import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class RepairEnchant extends BananaPrisonEnchantment {

    private double chance;

    public RepairEnchant(BananaPrisonEnchants instance) {
        super(instance, 8);
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
            e.getPlayer().getItemInHand().setDurability((short) (e.getPlayer().getItemInHand().getDurability() + 4.0));
        }
    }

    @Override
    public void reload() {
        this.chance = plugin.getConfig().get().getDouble("enchants." + id + ".Chance");
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }
}
