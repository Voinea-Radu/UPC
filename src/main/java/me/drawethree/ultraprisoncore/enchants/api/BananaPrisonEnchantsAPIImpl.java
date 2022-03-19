package me.drawethree.ultraprisoncore.enchants.api;

import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import me.drawethree.ultraprisoncore.enchants.managers.EnchantsManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BananaPrisonEnchantsAPIImpl implements BananaPrisonEnchantsAPI {

    private EnchantsManager enchantsManager;

    public BananaPrisonEnchantsAPIImpl(EnchantsManager enchantsManager) {

        this.enchantsManager = enchantsManager;
    }

    @Override
    public HashMap<BananaPrisonEnchantment, Integer> getPlayerEnchants(ItemStack pickAxe) {
        return this.enchantsManager.getPlayerEnchants(pickAxe);
    }

    @Override
    public boolean hasEnchant(Player p, int id) {
        return this.enchantsManager.hasEnchant(p, id);
    }

    @Override
    public synchronized int getEnchantLevel(ItemStack item, int id) {
        return this.enchantsManager.getEnchantLevel(item, id);
    }

    @Override
    public ItemStack addEnchant(ItemStack item, Player p, int id, int level) {
        return this.enchantsManager.addEnchant(p, item, id, level);
    }

    @Override
    public ItemStack addEnchant(ItemStack item, int id, int level) {
        return this.enchantsManager.addEnchant(item, id, level);
    }

    @Override
    public ItemStack removeEnchant(ItemStack item, Player p, int id) {
        return this.enchantsManager.removeEnchant(item, p, id, 0);
    }

    @Override
    public BananaPrisonEnchantment getById(int id) {
        return BananaPrisonEnchantment.getEnchantById(id);
    }

    @Override
    public BananaPrisonEnchantment getByName(String rawName) {
        return BananaPrisonEnchantment.getEnchantByName(rawName);
    }
}
