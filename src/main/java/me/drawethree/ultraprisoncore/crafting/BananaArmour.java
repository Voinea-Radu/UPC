package me.drawethree.ultraprisoncore.crafting;

import lombok.Getter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.BananaPrisonModule;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.currency.BananaPrisonBananaCurrency;
import me.drawethree.ultraprisoncore.multipliers.BananaPrisonMultipliers;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation", "unused", "MismatchedQueryAndUpdateOfCollection"})
public class BananaArmour implements BananaPrisonModule {

    public static final String MODULE_NAME = "BananaCurrency";
    public static final String GIVE_BANANA_ARMOUR_PERMISSION = "bananaprison.admin.give.banana-armour";
    public static final String GIVE_TOP_RANK_ARMOUR_PERMISSION = "bananaprison.admin.give.top-rank-armour";

    @Getter
    private static BananaArmour instance;
    @Getter
    private final BananaPrisonCore core;
    @Getter
    private final BananaPrisonBananaCurrency bananaCurrency;
    private final BananaPrisonMultipliers multipliers;
    private final List<Recipe> recipes = new ArrayList<>();
    @Getter
    private FileManager.Config config;
    private boolean enabled;

    public BananaArmour(BananaPrisonCore UltraPrisonCore) {
        instance = this;
        this.core = UltraPrisonCore;
        this.bananaCurrency = core.getBananaCurrency();
        this.multipliers = core.getMultipliers();
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void reload() {
        this.config = this.core.getFileManager().getConfig("banana-currency.yml").copyDefaults(true).save();
    }

    @Override
    public void enable() {
        this.enabled = true;

        this.config = this.core.getFileManager().getConfig("banana-currency.yml").copyDefaults(true).save();
        registerRecipe();
        registerEvents();
        registerSchedulers();
        registerCommands();
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }


    /**
     * Registers the commands /gba /gtra
     *
     * @author _LightDream
     */
    private void registerCommands() {
        Commands.create()
                .assertPermission(GIVE_BANANA_ARMOUR_PERMISSION)
                .handler(c -> {
                    if (c.args().size() == 0) {

                        if (c.sender() instanceof Player) {
                            Player player = (Player) c.sender();
                            player.getInventory().addItem(enchantBananaArmour(new ItemStack(Material.LEATHER_HELMET, 1)));
                            player.getInventory().addItem(enchantBananaArmour(new ItemStack(Material.LEATHER_CHESTPLATE, 1)));
                            player.getInventory().addItem(enchantBananaArmour(new ItemStack(Material.LEATHER_LEGGINGS, 1)));
                            player.getInventory().addItem(enchantBananaArmour(new ItemStack(Material.LEATHER_BOOTS, 1)));
                            player.sendMessage("You have received the banana armour");
                        } else {
                            c.sender().sendMessage("You must be in game!");
                        }
                    }
                }).registerAndBind(core, "givebananaarmour", "gba");
        Commands.create()
                .assertPermission(GIVE_TOP_RANK_ARMOUR_PERMISSION)
                .handler(c -> {
                    if (c.args().size() == 0) {
                        if (c.sender() instanceof Player) {
                            Player player = (Player) c.sender();
                            player.getInventory().addItem(enchantTopRankArmour(new ItemStack(Material.DIAMOND_HELMET, 1)));
                            player.getInventory().addItem(enchantTopRankArmour(new ItemStack(Material.DIAMOND_CHESTPLATE, 1)));
                            player.getInventory().addItem(enchantTopRankArmour(new ItemStack(Material.DIAMOND_LEGGINGS, 1)));
                            player.getInventory().addItem(enchantTopRankArmour(new ItemStack(Material.DIAMOND_BOOTS, 1)));
                            player.sendMessage("You have received the top rank armour");
                        } else {
                            c.sender().sendMessage("You must be in game!");
                        }

                    }
                }).registerAndBind(core, "givetoprankarmour", "gtra");
    }

    /**
     * Registers the recipes for the custom armour craftings
     *
     * @author _LightDream
     */
    private void registerRecipe() {
        List<ItemStack> bananaArmour = Arrays.asList(
                enchantBananaArmour(new ItemStack(Material.LEATHER_HELMET, 1)),
                enchantBananaArmour(new ItemStack(Material.LEATHER_CHESTPLATE, 1)),
                enchantBananaArmour(new ItemStack(Material.LEATHER_LEGGINGS, 1)),
                enchantBananaArmour(new ItemStack(Material.LEATHER_BOOTS, 1))
        );
        List<ItemStack> topRankArmour = Arrays.asList(
                enchantTopRankArmour(new ItemStack(Material.DIAMOND_HELMET, 1)),
                enchantTopRankArmour(new ItemStack(Material.DIAMOND_CHESTPLATE, 1)),
                enchantTopRankArmour(new ItemStack(Material.DIAMOND_LEGGINGS, 1)),
                enchantTopRankArmour(new ItemStack(Material.DIAMOND_BOOTS, 1))
        );

        for (ItemStack item : bananaArmour) {
            ShapedRecipe recipe = new ShapedRecipe(item);

            recipe.shape("BBB", "BAB", "BBB");

            recipe.setIngredient('B', bananaCurrency.getBananaItem(1).getData());
            recipe.setIngredient('A', topRankArmour.get(bananaArmour.indexOf(item)).getData());

            recipes.add(recipe);

            core.getServer().addRecipe(recipe);
        }
    }

    /**
     * Registers the events
     *
     * @author _LightDream
     */
    private void registerEvents() {
        Events.subscribe(PrepareItemCraftEvent.class)
                .handler(e -> {
                    if (e.getInventory().getMatrix().length < 5) {
                        return;
                    }
                    if (e.getInventory().getMatrix()[4] == null) {
                        return;
                    }

                    ItemStack checkItem = e.getInventory().getMatrix()[4].clone();
                    checkItem = enchantTopRankArmour(checkItem);

                    if (!e.getInventory().getMatrix()[4].equals(checkItem)) {
                        System.out.println("Cancel the craft");
                        e.getInventory().setResult(null);
                    }
                }).bindWith(core);
    }

    /**
     * Registers the schedulers
     *
     * @author _LightDream
     */
    private void registerSchedulers() {
        Schedulers.bukkit().runTaskTimer(core, () -> {
            for (Player player : Players.all()) {
                ItemStack[] armour = player.getInventory().getArmorContents();
                if (!(armour[0] == null || armour[1] == null || armour[2] == null || armour[3] == null)) {
                    if (armour[0].equals(enchantBananaArmour(new ItemStack(Material.LEATHER_BOOTS, 1))) &&
                            armour[1].equals(enchantBananaArmour(new ItemStack(Material.LEATHER_LEGGINGS, 1))) &&
                            armour[2].equals(enchantBananaArmour(new ItemStack(Material.LEATHER_CHESTPLATE, 1))) &&
                            armour[3].equals(enchantBananaArmour(new ItemStack(Material.LEATHER_HELMET, 1)))) {
                        multipliers.addArmourMultiplier(player.getUniqueId());

                        for (String effect : (List<String>) config.get("banana-armour-effects")) {
                            int power = Integer.parseInt(effect.split(" ")[1]);
                            String name = effect.split(" ")[0];
                            player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), 5 * 20, power));
                        }
                        System.out.println("Player " + player.getName() + " has the armour");
                    } else {
                        multipliers.removeArmourMultiplier(player.getUniqueId());
                    }
                } else {
                    multipliers.removeArmourMultiplier(player.getUniqueId());
                }
            }
        }, 20, 20);
    }

    /**
     * Enchants an item according to the banana armour config
     *
     * @param item The item you want to enchant
     * @return The enchanted item
     * @author _LightDream
     */
    private ItemStack enchantBananaArmour(ItemStack item) {

        List<String> allowedMaterials = Arrays.asList("LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS");

        if (item == null) {
            return null;
        }
        if (!allowedMaterials.contains(item.getType().toString())) {
            return null;
        }

        String configType = item.getType().toString().replace("LEATHER_", "").toLowerCase();

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB((int) config.get("banana-armour-color.red"), (int) config.get("banana-armour-color.green"), (int) config.get("banana-armour-color.blue")));
        meta.setDisplayName(Text.colorize((String) config.get("banana-" + configType + "-name")));
        List<String> lore = new ArrayList<>();
        for (String loreLine : (List<String>) config.get("banana-" + configType + "-lore")) {
            lore.add(Text.colorize(loreLine.replace("%multi%", String.valueOf(config.get("banana-armour-multi")))));
        }
        meta.setLore(lore);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);

        for (String s : (List<String>) config.get("banana-armour-enchants"))
            item.addUnsafeEnchantment(Enchantment.getByName(s.split(" ")[0]), Integer.parseInt(s.split(" ")[1]));

        return item;
    }

    /**
     * Enchants an item according to the top tank armour config
     *
     * @param item The item you want to enchant
     * @return The enchanted item
     * @author _LightDream
     */
    private ItemStack enchantTopRankArmour(ItemStack item) {

        List<String> allowedMaterials = Arrays.asList("DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS");

        if (item == null) {
            return null;
        }
        if (!allowedMaterials.contains(item.getType().toString())) {
            return null;
        }

        String configType = item.getType().toString().replace("DIAMOND_", "").toLowerCase();


        ItemMeta meta = item.getItemMeta();
        List<String> enchants;

        meta.setDisplayName(Text.colorize((String) config.get("top-rank-" + configType + "-name")));

        item.setItemMeta(meta);
        for (String s : (List<String>) config.get("top-rank-" + configType + "-enchants"))
            item.addEnchantment(Enchantment.getByName(s.split(" ")[0]), Integer.parseInt(s.split(" ")[1]));
        return item;
    }


}
