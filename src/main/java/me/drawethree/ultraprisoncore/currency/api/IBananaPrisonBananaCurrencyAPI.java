package me.drawethree.ultraprisoncore.currency.api;

import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public interface IBananaPrisonBananaCurrencyAPI {

    /**
     * Check if the item is a valid banana
     *
     * @param item The item you want to check
     * @return Whether the item is valid or not
     * @author _LightDream
     */
    boolean isValid(ItemStack item);

}
