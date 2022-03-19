package me.drawethree.ultraprisoncore.enchants.api;

import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface BananaPrisonEnchantsAPI {


    /**
     * Method to get all custom enchants applied on specific ItemStack
     *
     * @param itemStack ItemStack
     * @return
     */
    HashMap<BananaPrisonEnchantment, Integer> getPlayerEnchants(ItemStack itemStack);

    /**
     * Method to check if player has specific enchant on his current item in hand
     * @param p Player
     * @param id Enchant ID
     * @return true if player has specified enchant
     */
    boolean hasEnchant(Player p, int id);

    /**
     * Method to get enchant level of specific ItemStack
     * @param item ItemStack
     * @param id Enchant ID
     * @return 0 if enchant was not found, otherwise level of enchant
     */
    int getEnchantLevel(ItemStack item, int id);

    /**
     * Method to add custom enchant to ItemStack
     * @param item ItemStack
     * @param p Player
     * @param id Enchant ID
     * @param level Enchant Level
     * @return modified ItemStack
     */
    ItemStack addEnchant(ItemStack item, Player p, int id, int level);

    /**
     * Method to add custom enchant to ItemStack
     * @param item ItemStack
     * @param id Enchant ID
     * @param level Enchant Level
     * @return modified ItemStack
     */
    ItemStack addEnchant(ItemStack item, int id, int level);

    /**
     * Method to remove custom enchant from ItemStack
     * @param item ItemStack
     * @param p Player
     * @param id Enchant ID
     * @return modified ItemStack
     */
    ItemStack removeEnchant(ItemStack item, Player p, int id);

    /**
     * Method to get Enchant by ID
     * @param id enchant id
     * @return UltraPrisonEnchantment
     */
    BananaPrisonEnchantment getById(int id);

    /**
     * Method to get Enchant by ID
     * @param rawName enchant rawname
     * @return UltraPrisonEnchantment
     */
    BananaPrisonEnchantment getByName(String rawName);

}
