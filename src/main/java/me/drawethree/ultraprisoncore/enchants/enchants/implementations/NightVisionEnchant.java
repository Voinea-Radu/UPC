package me.drawethree.ultraprisoncore.enchants.enchants.implementations;

import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionEnchant extends BananaPrisonEnchantment {
    public NightVisionEnchant(BananaPrisonEnchants instance) {
        super(instance, 7);
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {
        if (level == 0) {
            this.onUnequip(p,pickAxe,level);
            return;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, level-1,true,true),true);
    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }
}
