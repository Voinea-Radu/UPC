package me.drawethree.ultraprisoncore.enchants.enchants;

import lombok.Getter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.implementations.*;
import me.drawethree.ultraprisoncore.utils.compat.CompMaterial;
import me.lucko.helper.text3.Text;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

@Getter
public abstract class BananaPrisonEnchantment implements Refundable {

    private static HashMap<Integer, BananaPrisonEnchantment> allEnchantmentsById = new HashMap<>();
    private static HashMap<String, BananaPrisonEnchantment> allEnchantmentsByName = new HashMap<>();

    static {
        loadDefaultEnchantments();
    }

    protected final BananaPrisonEnchants plugin;

    protected final int id;
    private String rawName;
    private String name;
    private Material material;
    private String description;
    private boolean enabled;
    private int guiSlot;
    private int maxLevel;
    private long cost;
    private long increaseCost;
    private int requiredPickaxeLevel;
    private boolean messagesEnabled;

    public BananaPrisonEnchantment(BananaPrisonEnchants plugin, int id) {
        this.plugin = plugin;
        this.id = id;
        this.reloadDefaultAttributes();
        this.reload();
    }

    private void reloadDefaultAttributes() {
        this.rawName = this.plugin.getConfig().get().getString("enchants." + id + ".RawName");
        this.name = Text.colorize(this.plugin.getConfig().get().getString("enchants." + id + ".Name"));
        this.material = CompMaterial.fromString(this.plugin.getConfig().get().getString("enchants." + id + ".Material")).toMaterial();
        this.description = Text.colorize(this.plugin.getConfig().get().getString("enchants." + id + ".Description"));
        this.enabled = this.plugin.getConfig().get().getBoolean("enchants." + id + ".Enabled");
        this.guiSlot = this.plugin.getConfig().get().getInt("enchants." + id + ".InGuiSlot");
        this.maxLevel = this.plugin.getConfig().get().getInt("enchants." + id + ".Max");
        this.cost = this.plugin.getConfig().get().getLong("enchants." + id + ".Cost");
        this.increaseCost = this.plugin.getConfig().get().getLong("enchants." + id + ".Increase-Cost-by");
        this.requiredPickaxeLevel = this.plugin.getConfig().get().getInt("enchants." + id + ".Pickaxe-Level-Required");
        this.messagesEnabled = this.plugin.getConfig().get().getBoolean("enchants." + id + ".Messages-Enabled", true);
    }

    public abstract String getAuthor();

    public abstract void onEquip(Player p, ItemStack pickAxe, int level);

    public abstract void onUnequip(Player p, ItemStack pickAxe, int level);

    public abstract void onBlockBreak(BlockBreakEvent e, int enchantLevel);

    public abstract void reload();

    public static Collection<BananaPrisonEnchantment> all() {
        return allEnchantmentsById.values();
    }

    public long getCostOfLevel(int level) {
        return (this.cost + (this.increaseCost * (level - 1)));
    }


    @Override
    public boolean isRefundEnabled() {
        return this.plugin.getConfig().get().getBoolean("enchants." + this.id + ".Refund.Enabled");
    }

    @Override
    public int refundGuiSlot() {
        return this.plugin.getConfig().get().getInt("enchants." + this.id + ".Refund.InGuiSlot");
    }

    public static BananaPrisonEnchantment getEnchantById(int id) {
        return allEnchantmentsById.get(id);
    }

    public static BananaPrisonEnchantment getEnchantByName(String name) {
        return allEnchantmentsByName.get(name.toLowerCase());
    }

    public void register() {

        if (allEnchantmentsById.containsKey(this.getId()) || allEnchantmentsByName.containsKey(this.getRawName())) {
            BananaPrisonCore.getInstance().getLogger().warning(Text.colorize("&cUnable to register enchant " + this.getName() + "&c created by " + this.getAuthor() + ". That enchant is already registered."));
            return;
        }

        Validate.notNull(this.getRawName());

        allEnchantmentsById.put(this.getId(), this);
        allEnchantmentsByName.put(this.getRawName().toLowerCase(), this);

        BananaPrisonCore.getInstance().getLogger().info(Text.colorize("&aSuccessfully registered enchant " + this.getName() + "&a created by " + this.getAuthor()));
    }

    public void unregister() {

        if (!allEnchantmentsById.containsKey(this.getId()) && !allEnchantmentsByName.containsKey(this.getRawName())) {
            BananaPrisonCore.getInstance().getLogger().warning(Text.colorize("&cUnable to unregister enchant " + this.getName() + "&c created by " + this.getAuthor() + ". That enchant is not registered."));
            return;
        }

        allEnchantmentsById.remove(this.getId());
        allEnchantmentsByName.remove(this.getRawName());

        BananaPrisonCore.getInstance().getLogger().info(Text.colorize("&aSuccessfully unregistered enchant " + this.getName() + "&a created by " + this.getAuthor()));
    }


    private static void loadDefaultEnchantments() {
        new EfficiencyEnchant(BananaPrisonEnchants.getInstance()).register();
        new UnbreakingEnchant(BananaPrisonEnchants.getInstance()).register();
        new FortuneEnchant(BananaPrisonEnchants.getInstance()).register();
        new HasteEnchant(BananaPrisonEnchants.getInstance()).register();
        new SpeedEnchant(BananaPrisonEnchants.getInstance()).register();
        new JumpBoostEnchant(BananaPrisonEnchants.getInstance()).register();
        new NightVisionEnchant(BananaPrisonEnchants.getInstance()).register();
        new RepairEnchant(BananaPrisonEnchants.getInstance()).register();
        new GraceEnchant(BananaPrisonEnchants.getInstance()).register();
        new LayerEnchant(BananaPrisonEnchants.getInstance()).register();
        new MeteorsEnchant(BananaPrisonEnchants.getInstance()).register();
        new BananeerEnchant(BananaPrisonEnchants.getInstance()).register();
        new NukeEnchant(BananaPrisonEnchants.getInstance()).register();
        new TokenatorEnchant(BananaPrisonEnchants.getInstance()).register();
        new KeyFinderEnchant(BananaPrisonEnchants.getInstance()).register();
    }

    public static void reloadAll() {

        allEnchantmentsById.values().forEach(enchant -> {
            enchant.reloadDefaultAttributes();
            enchant.reload();
        });

        BananaPrisonCore.getInstance().getLogger().info(Text.colorize("&aSuccessfully reloaded all enchants."));
    }


    public int getMaxLevel() {
        return this.maxLevel == -1 ? Integer.MAX_VALUE : this.maxLevel;
    }

    public boolean canBeBought(ItemStack pickAxe) {
        if (!this.plugin.getCore().isModuleEnabled("Pickaxe Levels")) {
            return true;
        }
        return this.plugin.getCore().getPickaxeLevels().getPickaxeLevel(pickAxe).getLevel() >= this.requiredPickaxeLevel;
    }
}
