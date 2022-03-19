package me.drawethree.ultraprisoncore.currency;

import dev.dbassett.skullcreator.SkullCreator;
import lombok.Getter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.BananaPrisonModule;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.currency.api.BananaPrisonBananaCurrencyAPIImpl;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "ConstantConditions", "deprecation"})
public class BananaPrisonBananaCurrency implements BananaPrisonModule {

    public static final String MODULE_NAME = "BananaCurrency";
    private static final String GIVE_CURRENCY_PERMISSION = "bananaprison.currency.banana.give";

    @Getter
    private final BananaPrisonBananaCurrency instance;
    @Getter
    private final BananaPrisonCore core;
    @Getter
    private FileManager.Config config;
    private boolean enabled;
    @Getter
    private BananaPrisonBananaCurrencyAPIImpl api;

    public BananaPrisonBananaCurrency(BananaPrisonCore UltraPrisonCore) {
        instance = this;
        this.core = UltraPrisonCore;
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

        this.registerCommands();
        this.registerEvents();
        this.config = this.core.getFileManager().getConfig("banana-currency.yml").copyDefaults(true).save();
        api = new BananaPrisonBananaCurrencyAPIImpl(this);
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
     * Registers the commands /gb
     *
     * @author _LightDream
     */
    private void registerCommands() {
        Commands.create()
                .assertPermission(GIVE_CURRENCY_PERMISSION)
                .handler(c -> {
                    if (c.args().size() == 2) {
                        Player onlinePlayer = Players.getNullable(c.rawArg(0));
                        int count = Integer.parseInt(c.rawArg(1));
                        giveBanana(c.sender(), onlinePlayer, count);
                    }
                }).registerAndBind(core, "givecurrencybanana", "gcb", "givebanana", "gb");
    }

    /**
     * Registers the events
     *
     * @author _LightDream
     */
    private void registerEvents() {
        Events.subscribe(PlayerPickupItemEvent.class)
                .handler(e -> {
                    System.out.println(e.getItem().getItemStack().getType());
                    if (e.getItem().getItemStack().getType().equals(Material.SKULL_ITEM)) {
                        if (((SkullMeta) e.getItem().getItemStack().getItemMeta()).getOwner().equalsIgnoreCase("aaaaa")) {
                            e.getPlayer().getInventory().addItem(getBananaItem(e.getItem().getItemStack().getAmount()));
                            e.getItem().remove();
                            e.setCancelled(true);
                        }
                    }
                }).bindWith(core);
    }

    /**
     * Command executor for /gcb
     *
     * @param sender       The person who sends the command
     * @param onlinePlayer The player who will receive the bananas
     * @param count        The number of bananas the player will receive
     * @author _LightDream
     */
    public void giveBanana(CommandSender sender, Player onlinePlayer, int count) {
        onlinePlayer.getInventory().addItem(getBananaItem(count));

        String senderMessage = (String) config.get("item-given");
        String playerMessage = (String) config.get("item-received");

        senderMessage = senderMessage.replace("%count%", String.valueOf(count));
        playerMessage = playerMessage.replace("%count%", String.valueOf(count));
        senderMessage = senderMessage.replace("%target%", onlinePlayer.getName());
        playerMessage = playerMessage.replace("%sender%", sender.getName());

        sender.sendMessage(Text.colorize(senderMessage));
        onlinePlayer.sendMessage(Text.colorize(playerMessage));
    }

    /**
     * Gives bananas
     *
     * @param count The number of banana
     * @return ItemStack of count bananas
     */
    public ItemStack getBananaItem(int count) {
        String url = (String) config.get("item-texture");

        ItemStack banana = SkullCreator.itemFromUrl(url);
        banana.setAmount(count);
        ItemMeta meta = banana.getItemMeta();
        meta.setDisplayName(Text.colorize((String) config.get("item-name")));
        List<String> lore = new ArrayList<>();
        for (String loreLine : (List<String>) config.get("item-lore")) {
            lore.add(Text.colorize(loreLine));
        }
        meta.setLore(lore);
        banana.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        banana.setItemMeta(meta);

        return banana;
    }

    /**
     * Checks if an ItemStack is a valid banana
     *
     * @param item The item you want to check
     * @return True if the item is a valid banana or false
     * @author _LightDream
     */
    public boolean checkValidBanana(ItemStack item) {
        return item.equals(getBananaItem(item.getAmount()));
    }


}
