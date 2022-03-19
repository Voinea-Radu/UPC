package me.drawethree.ultraprisoncore.enchants.enchants.implementations;

import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GraceEnchant extends BananaPrisonEnchantment {
    private double chance;
    private List<String> commandsToExecute;


    public GraceEnchant(BananaPrisonEnchants instance) {
        super(instance, 9);
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
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
            for (String cmd : this.commandsToExecute) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", e.getPlayer().getName()));
            }
        }
    }

    @Override
    public void reload() {
        this.chance = plugin.getConfig().get().getDouble("enchants." + id + ".Chance");
        this.commandsToExecute = plugin.getConfig().get().getStringList("enchants." + id + ".Commands");
    }
}
