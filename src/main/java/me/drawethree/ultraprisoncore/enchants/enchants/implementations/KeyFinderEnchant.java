package me.drawethree.ultraprisoncore.enchants.enchants.implementations;

import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KeyFinderEnchant extends BananaPrisonEnchantment {

    private double chance;
    private List<String> commandsToExecute;

    public KeyFinderEnchant(BananaPrisonEnchants instance) {
        super(instance, 15);
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (this.chance * enchantLevel >= ThreadLocalRandom.current().nextDouble(100)) {
            String randomCmd = this.commandsToExecute.get(ThreadLocalRandom.current().nextInt(commandsToExecute.size()));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), randomCmd.replace("%player%", e.getPlayer().getName()));
        }
    }

    @Override
    public void reload() {
        this.chance = plugin.getConfig().get().getDouble("enchants." + id + ".Chance");
        this.commandsToExecute = plugin.getConfig().get().getStringList("enchants." + id + ".Commands");
    }


    private int getDelayBetweenKeys(int enchantLevel) {
        if (enchantLevel <= 49) {
            return 30;
        } else if (enchantLevel <= 99) {
            return 25;
        } else if (enchantLevel <= 149) {
            return 20;
        } else if (enchantLevel <= 199) {
            return 15;
        } else {
            return 10;
        }
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }
}
