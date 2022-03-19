package me.drawethree.ultraprisoncore;

import lombok.Getter;
import me.drawethree.ultraprisoncore.autominer.BananaPrisonAutoMiner;
import me.drawethree.ultraprisoncore.autosell.BananaPrisonAutoSell;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.crafting.BananaArmour;
import me.drawethree.ultraprisoncore.currency.BananaPrisonBananaCurrency;
import me.drawethree.ultraprisoncore.database.Database;
import me.drawethree.ultraprisoncore.database.DatabaseCredentials;
import me.drawethree.ultraprisoncore.database.SQLDatabase;
import me.drawethree.ultraprisoncore.database.implementations.MySQLDatabase;
import me.drawethree.ultraprisoncore.database.implementations.SQLiteDatabase;
import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.gangs.BananaPrisonGangs;
import me.drawethree.ultraprisoncore.gems.BananaPrisonGems;
import me.drawethree.ultraprisoncore.gui.BananaPrisonGuiManager;
import me.drawethree.ultraprisoncore.inventory_pets.BananaPrisonInventoryPets;
import me.drawethree.ultraprisoncore.multipliers.BananaPrisonMultipliers;
import me.drawethree.ultraprisoncore.pickaxelevels.BananaPrisonPickaxeLevels;
import me.drawethree.ultraprisoncore.placeholders.BananaPrisonMVdWPlaceholder;
import me.drawethree.ultraprisoncore.placeholders.BananaPrisonPlaceholder;
import me.drawethree.ultraprisoncore.ranks.BananaPrisonRankup;
import me.drawethree.ultraprisoncore.tokens.BananaPrisonTokens;
import me.drawethree.ultraprisoncore.utils.gui.ClearDBGui;
import me.jet315.prisonmines.JetsPrisonMines;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.text.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@SuppressWarnings({"deprecation", "ConstantConditions"})
@Getter
public final class BananaPrisonCore extends ExtendedJavaPlugin {


    private Map<String, BananaPrisonModule> loadedModules;

    @Getter
    private static BananaPrisonCore instance;
    private Database pluginDatabase;
    private Economy economy;
    private FileManager fileManager;

    private BananaPrisonTokens tokens;
    private BananaPrisonGems gems;
    private BananaPrisonRankup ranks;
    private BananaPrisonMultipliers multipliers;
    private BananaPrisonEnchants enchants;
    private BananaPrisonAutoSell autoSell;
    private BananaPrisonAutoMiner autoMiner;
    private BananaPrisonPickaxeLevels pickaxeLevels;
    private BananaPrisonGangs gangs;
    private BananaPrisonBananaCurrency bananaCurrency;
    private BananaArmour bananaArmour;
    private BananaPrisonInventoryPets pets;
    private BananaPrisonGuiManager guiManager;

    private JetsPrisonMinesAPI jetsPrisonMinesAPI;

    @Override
    protected void enable() {

        instance = this;
        this.loadedModules = new HashMap<>();
        this.fileManager = new FileManager(this);
        this.fileManager.getConfig("config.yml").copyDefaults(true).save();

        try {
            String databaseType = this.getConfig().getString("database_type");

            if (databaseType.equalsIgnoreCase("sqlite")) {
                this.pluginDatabase = new SQLiteDatabase(this);
            } else if (databaseType.equalsIgnoreCase("mysql")) {
                this.pluginDatabase = new MySQLDatabase(this, DatabaseCredentials.fromConfig(this.getConfig()));
            } else {
                this.getLogger().warning(String.format("Error! Unknown database type: %s. Disabling plugin.", databaseType));
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

        } catch (Exception e) {
            this.getLogger().warning("Could not maintain Database Connection. Disabling plugin.");

            e.printStackTrace();

            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.tokens = new BananaPrisonTokens(this);
        this.gems = new BananaPrisonGems(this);
        this.ranks = new BananaPrisonRankup(this);
        this.multipliers = new BananaPrisonMultipliers(this);
        this.enchants = new BananaPrisonEnchants(this);
        this.autoSell = new BananaPrisonAutoSell(this);
        this.autoMiner = new BananaPrisonAutoMiner(this);
        this.pickaxeLevels = new BananaPrisonPickaxeLevels(this);
        this.gangs = new BananaPrisonGangs(this);
        this.bananaCurrency = new BananaPrisonBananaCurrency(this);
        this.bananaArmour = new BananaArmour(this);
        this.pets = new BananaPrisonInventoryPets(this);

        if (!this.setupEconomy()) {
            this.getLogger().warning("Economy provider for Vault not found! Economy provider is strictly required. Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            this.getLogger().info("Economy provider for Vault found - " + this.getEconomy().getName());
        }

        this.registerPlaceholders();
        this.registerJetsPrisonMines();

        if (this.getConfig().getBoolean("modules.tokens")) {
            this.loadModule(tokens);
        }
        if (this.getConfig().getBoolean("modules.gems")) {
            this.loadModule(gems);
        }
        if (this.getConfig().getBoolean("modules.ranks")) {
            this.loadModule(ranks);
        }
        if (this.getConfig().getBoolean("modules.multipliers")) {
            this.loadModule(multipliers);
        }
        if (this.getConfig().getBoolean("modules.enchants")) {
            this.loadModule(enchants);
        }
        if (this.getConfig().getBoolean("modules.autosell")) {
            this.loadModule(autoSell);
        }
        if (this.getConfig().getBoolean("modules.autominer")) {
            this.loadModule(autoMiner);
        }
        if (this.getConfig().getBoolean("modules.gangs")) {
            this.loadModule(gangs);
        }
        if (this.getConfig().getBoolean("modules.pickaxe_levels")) {
            if (!this.isModuleEnabled("Enchants")) {
                this.getLogger().warning(Text.colorize("UltraPrisonCore - Module 'Pickaxe Levels' requires to have enchants module enabled."));
            } else {
                this.loadModule(pickaxeLevels);
            }
        }
        if (this.getConfig().getBoolean("modules.currency.banana")) {
            this.loadModule(bananaCurrency);
        }
        if (this.getConfig().getBoolean("modules.armour.banana")) {
            this.loadModule(bananaArmour);
        }
        if (this.getConfig().getBoolean("modules.pets")) {
            this.loadModule(pets);
        }

        this.startEvents();
        this.registerMainCommand();
        this.registerMainListeners();

        guiManager = new BananaPrisonGuiManager(this);
    }

    private void registerMainListeners() {
        Events.subscribe(FoodLevelChangeEvent.class, EventPriority.MONITOR)
                .filter(e -> e.getEntity() instanceof Player)
                .handler(e -> e.setFoodLevel(20)).bindWith(this);
        Events.subscribe(WeatherChangeEvent.class, EventPriority.MONITOR)
                .filter(WeatherChangeEvent::toWeatherState)
                .handler(e -> e.setCancelled(true)).bindWith(this);
    }

    private void loadModule(BananaPrisonModule module) {
        this.loadedModules.put(module.getName(), module);
        module.enable();
        this.getLogger().info(Text.colorize(String.format("UltraPrisonCore - Module %s loaded.", module.getName())));
    }

    //Always unload via iterator!
    private void unloadModule(BananaPrisonModule module) {
        module.disable();
        this.getLogger().info(Text.colorize(String.format("UltraPrisonCore - Module %s unloaded.", module.getName())));
    }

    private void reloadModule(BananaPrisonModule module) {
        module.reload();
        this.getLogger().info(Text.colorize(String.format("UltraPrisonCore - Module %s reloaded.", module.getName())));

    }

    private void registerMainCommand() {
        Commands.create()
                .assertPermission("ultraprison.admin")
                .handler(c -> {
                    if (c.args().size() == 1 && c.rawArg(0).equalsIgnoreCase("reload")) {
                        this.reload(c.sender());
                    } else if (c.args().size() == 1 && c.rawArg(0).equalsIgnoreCase("cleardb")) {
                        if (c.sender() instanceof Player) {
                            new ClearDBGui(this.pluginDatabase, (Player) c.sender()).open();
                        } else {
                            this.pluginDatabase.resetAllData(c.sender());
                        }
                    } else if (c.args().size() == 1 && (c.rawArg(0).equalsIgnoreCase("version") || c.rawArg(0).equalsIgnoreCase("v"))) {
                        c.sender().sendMessage(Text.colorize("&7This server is running &f" + this.getDescription().getFullName()));
                    }
                }).registerAndBind(this, "prisoncore", "ultraprison", "prison", "ultraprisoncore");
    }

    private void reload(CommandSender sender) {
        for (BananaPrisonModule module : this.loadedModules.values()) {
            this.reloadModule(module);
        }
        sender.sendMessage(Text.colorize("&aUltraPrisonCore - Reloaded."));
    }


    @Override
    protected void disable() {

        Iterator<BananaPrisonModule> it = this.loadedModules.values().iterator();

        while (it.hasNext()) {
            this.unloadModule(it.next());
            it.remove();
        }

        if (this.pluginDatabase != null) {
            if (this.pluginDatabase instanceof SQLDatabase) {
                SQLDatabase sqlDatabase = (SQLDatabase) this.pluginDatabase;
                sqlDatabase.close();
            }
        }
    }

    private void startEvents() {

    }

    public boolean isModuleEnabled(String moduleName) {
        return this.loadedModules.containsKey(moduleName);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BananaPrisonPlaceholder(this).register();
        }

        new BananaPrisonMVdWPlaceholder(this);
    }

    private void registerJetsPrisonMines() {
        if (Bukkit.getPluginManager().getPlugin("JetsPrisonMines") != null) {
            this.jetsPrisonMinesAPI = ((JetsPrisonMines) getServer().getPluginManager().getPlugin("JetsPrisonMines")).getAPI();
        }
    }

    private boolean setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }
}
