package me.drawethree.ultraprisoncore.multipliers;

import lombok.Getter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.BananaPrisonModule;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.multipliers.api.BananaPrisonMultipliersAPI;
import me.drawethree.ultraprisoncore.multipliers.api.BananaPrisonMultipliersAPIImpl;
import me.drawethree.ultraprisoncore.multipliers.multiplier.GlobalMultiplier;
import me.drawethree.ultraprisoncore.multipliers.multiplier.Multiplier;
import me.drawethree.ultraprisoncore.multipliers.multiplier.PlayerMultiplier;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text.Text;
import me.lucko.helper.time.Time;
import me.lucko.helper.utils.Players;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

@SuppressWarnings({"deprecation", "ConstantConditions", "unchecked", "unused"})
public final class BananaPrisonMultipliers implements BananaPrisonModule {

    public static final String MODULE_NAME = "Multipliers";
    private static final String ADD_MULTIPLIERS_TIME = "bananaprison.multipliers.addtime";

    private static GlobalMultiplier GLOBAL_MULTIPLIER;

    @Getter
    private static BananaPrisonMultipliers instance;
    @Getter
    private final BananaPrisonCore core;
    private final List<UUID> armourMultipliers = new ArrayList<>();
    @Getter
    private FileManager.Config config;
    @Getter
    private BananaPrisonMultipliersAPI api;
    private HashMap<UUID, Multiplier> rankMultipliers;
    private HashMap<UUID, PlayerMultiplier> personalMultipliers;
    private HashMap<String, String> messages;
    private LinkedHashMap<String, Double> permissionToMultiplier;
    private boolean enabled;

    public BananaPrisonMultipliers(BananaPrisonCore UltraPrisonCore) {
        instance = this;
        this.core = UltraPrisonCore;
    }


    private void loadRankMultipliers() {
        permissionToMultiplier = new LinkedHashMap<>();

        ConfigurationSection section = getConfig().get().getConfigurationSection("ranks");

        if (section == null) {
            return;
        }

        for (String rank : section.getKeys(false)) {
            String perm = "bananaprison.multiplier." + rank;
            double multiplier = getConfig().get().getDouble("ranks." + rank);
            permissionToMultiplier.put(perm, multiplier);
            core.getLogger().info("Loaded rank multiplier." + rank + " with multiplier " + multiplier);
        }
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void reload() {
        this.config.reload();

        this.loadMessages();
        this.loadRankMultipliers();
    }

    @Override
    public void enable() {

        this.enabled = true;
        this.config = this.core.getFileManager().getConfig("multipliers.yml").copyDefaults(true).save();

        this.rankMultipliers = new HashMap<>();
        this.personalMultipliers = new HashMap<>();

        this.loadMessages();
        this.loadRankMultipliers();
        this.registerCommands();
        this.registerEvents();
        this.removeExpiredMultipliers();
        this.loadGlobalMultiplier();
        this.loadOnlineMultipliers();
        api = new BananaPrisonMultipliersAPIImpl(this);
    }

    /**
     * Loads the multipliers for all online players
     *
     * @author Unknown
     */
    private void loadOnlineMultipliers() {
        Players.all().forEach(p -> {
            rankMultipliers.put(p.getUniqueId(), this.calculateRankMultiplier(p));
            this.loadPersonalMultiplier(p);
        });
    }

    /**
     * Registers the events
     */
    private void registerEvents() {
        Events.subscribe(PlayerJoinEvent.class)
                .handler(e -> {
                    rankMultipliers.put(e.getPlayer().getUniqueId(), this.calculateRankMultiplier(e.getPlayer()));
                    this.loadPersonalMultiplier(e.getPlayer());
                }).bindWith(core);
        Events.subscribe(PlayerQuitEvent.class)
                .handler(e -> {
                    rankMultipliers.remove(e.getPlayer().getUniqueId());
                    this.savePersonalMultiplier(e.getPlayer(), true);
                }).bindWith(core);
    }

    /**
     * Saves the personal multiplier for player
     *
     * @param player The player whom multiplier is saved
     * @param async  Whether the operation should be run asynchronously or not
     * @author Unkown
     */
    private void savePersonalMultiplier(Player player, boolean async) {

        if (!personalMultipliers.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerMultiplier multiplier = personalMultipliers.get(player.getUniqueId());

        if (async) {
            Schedulers.async().run(() -> {
                this.core.getPluginDatabase().savePersonalMultiplier(player, multiplier);
                this.personalMultipliers.remove(player.getUniqueId());
                this.core.getLogger().info(String.format("Saved multiplier of player %s", player.getName()));
            });
        } else {
            this.core.getPluginDatabase().savePersonalMultiplier(player, multiplier);
            this.personalMultipliers.remove(player.getUniqueId());
            this.core.getLogger().info(String.format("Saved multiplier of player %s", player.getName()));
        }
    }

    /**
     * Loads the global multiplier
     *
     * @author Unknown
     */
    private void loadGlobalMultiplier() {
        double multi = this.config.get().getDouble("global-multiplier.multiplier");
        long timeLeft = this.config.get().getLong("global-multiplier.timeLeft");

        GLOBAL_MULTIPLIER = new GlobalMultiplier(0.0, 0);
        GLOBAL_MULTIPLIER.setMultiplier(multi);

        if (timeLeft > Time.nowMillis()) {
            GLOBAL_MULTIPLIER.setDuration(timeLeft);
        } else {
            GLOBAL_MULTIPLIER.setDuration(0);
        }

        this.core.getLogger().info(String.format("Loaded Global Multiplier %.2fx", multi));
    }

    /**
     * Saves the global multiplier
     *
     * @author Unknown
     */
    private void saveGlobalMultiplier() {
        this.config.set("global-multiplier.multiplier", GLOBAL_MULTIPLIER.getMultiplier());
        this.config.set("global-multiplier.timeLeft", GLOBAL_MULTIPLIER.getEndTime());
        this.config.save();
        this.core.getLogger().info("Saved Global Multiplier into multipliers.yml");
    }

    /**
     * Loads the personal multiplier for player
     *
     * @param player The player whom multiplier is loaded
     * @author Unknown
     */
    private void loadPersonalMultiplier(Player player) {
        Schedulers.async().run(() -> {
            PlayerMultiplier multiplier = this.core.getPluginDatabase().getPlayerPersonalMultiplier(player);

            if (multiplier == null) {
                multiplier = new PlayerMultiplier(player.getUniqueId(), 0, 0);
            }

            System.out.println(multiplier.toString());

            personalMultipliers.put(player.getUniqueId(), multiplier);

            this.core.getLogger().info(String.format("Loaded multiplier %.2fx for player %s", multiplier.getMultiplier(), player.getName()));
        });
    }

    /**
     * Removes the expired multiplier
     *
     * @author Unkown
     */
    private void removeExpiredMultipliers() {
        Schedulers.async().run(() -> {
            this.core.getPluginDatabase().removeExpiredMultipliers();
            this.core.getLogger().info("Removed expired multipliers from database");
        });
    }


    @Override
    public void disable() {
        this.saveAllMultipliers();
        this.enabled = false;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    /**
     * Saves all the multipliers
     *
     * @author Unknown
     */
    private void saveAllMultipliers() {
        Players.all().forEach(p -> savePersonalMultiplier(p, false));
        this.saveGlobalMultiplier();
    }

    /**
     * Loads the messages from the config
     *
     * @author Unknown
     */
    private void loadMessages() {
        messages = new HashMap<>();
        for (String key : getConfig().get().getConfigurationSection("messages").getKeys(false)) {
            messages.put(key.toLowerCase(), Text.colorize(getConfig().get().getString("messages." + key)));
        }
    }

    /**
     * Registers the commands /gmulti /pmulti /multi /pmat /mh
     *
     * @author Unknown /gmulti /pmulti /multi
     * @author _LightDream /pmat /mh
     */
    private void registerCommands() {
        Commands.create()
                .assertPermission("ultraprison.multipliers.admin")
                .handler(c -> {
                    if (c.args().size() == 2) {
                        double amount = c.arg(0).parseOrFail(Double.class);
                        int minutes = c.arg(1).parseOrFail(Integer.class);
                        setupGlobalMultiplier(c.sender(), minutes, amount);
                    }
                }).registerAndBind(core, "globalmultiplier", "gmulti");
        Commands.create()
                .assertPermission("ultraprison.multipliers.admin")
                .handler(c -> {
                    if (c.args().size() == 3) {
                        Player onlinePlayer = Players.getNullable(c.rawArg(0));
                        double amount = c.arg(1).parseOrFail(Double.class);
                        int minutes = c.arg(2).parseOrFail(Integer.class);
                        setupPersonalMultiplier(c.sender(), onlinePlayer, amount, minutes);
                    }
                }).registerAndBind(core, "personalmultiplier", "pmulti");
        Commands.create()
                .assertPlayer()
                .handler(c -> {
                    c.sender().sendMessage(messages.get("global_multi").replace("%multiplier%", String.valueOf(GLOBAL_MULTIPLIER.getMultiplier())).replace("%duration%", GLOBAL_MULTIPLIER.getTimeLeft()));
                    c.sender().sendMessage(messages.get("rank_multi").replace("%multiplier%", String.valueOf(rankMultipliers.getOrDefault(c.sender().getUniqueId(), Multiplier.getDefaultPlayerMultiplier()).getMultiplier())));
                    c.sender().sendMessage(messages.get("vote_multi").replace("%multiplier%", String.valueOf(personalMultipliers.getOrDefault(c.sender().getUniqueId(), Multiplier.getDefaultPlayerMultiplier(c.sender().getUniqueId())).getMultiplier())).replace("%duration%", personalMultipliers.getOrDefault(c.sender().getUniqueId(), Multiplier.getDefaultPlayerMultiplier(c.sender().getUniqueId())).getTimeLeft()));
                }).registerAndBind(core, "multiplier", "multi");
        /*
        Commands.create()
                .assertPlayer()
                .assertPermission(ADD_MULTIPLIERS_TIME)
                .handler(c -> {
                    if (c.args().size() == 2) {
                        Player player = Players.getNullable(c.rawArg(0));
                        int time = Integer.parseInt(c.rawArg(1));
                        addMultiplierTime(c.sender(), player, time);
                    }
                }).registerAndBind(core, "pmultiaddtime", "pmat");
         */
        Commands.create()
                .assertPlayer()
                .assertPermission(ADD_MULTIPLIERS_TIME)
                .handler(c -> {
                    if (c.args().size() == 0) {
                        helpCommand(c.sender());
                    }
                }).registerAndBind(core, "multihelp", "mh");
    }

    /**
     * Functions setts up the multi for a player with amount% and time minutes
     *
     * @param sender       The sender of the multiplier command
     * @param onlinePlayer The player which will be affected
     * @param amount       The multiplier amount
     * @param minutes      The multiplier time
     * @author Unkown
     * @author _LightDream adding denying case for different mutli amount
     */
    private void setupPersonalMultiplier(CommandSender sender, Player onlinePlayer, double amount, int minutes) {

        if (onlinePlayer == null || !onlinePlayer.isOnline()) {
            sender.sendMessage(Text.colorize("&cPlayer must be online!"));
            return;
        }

        if (personalMultipliers.containsKey(onlinePlayer.getUniqueId())) {
            PlayerMultiplier multiplier = personalMultipliers.get(onlinePlayer.getUniqueId());

            if (multiplier.getMultiplier() == 0) {
                personalMultipliers.put(onlinePlayer.getUniqueId(), new PlayerMultiplier(onlinePlayer.getUniqueId(), amount, minutes));
            } else if (multiplier.getMultiplier() >= amount) {
                multiplier.addDuration(minutes);
                personalMultipliers.put(onlinePlayer.getUniqueId(), multiplier);
            } else {
                return;
            }
        } else {
            personalMultipliers.put(onlinePlayer.getUniqueId(), new PlayerMultiplier(onlinePlayer.getUniqueId(), amount, minutes));
        }

        System.out.println(personalMultipliers.get(onlinePlayer.getUniqueId()).getEndTime());

        onlinePlayer.sendMessage(messages.get("personal_multi_apply").replace("%multiplier%", String.valueOf(amount)).replace("%minutes%", String.valueOf(minutes)));
        sender.sendMessage(Text.colorize(String.format("&aYou have set &e%s's &ePersonal Multiplier &ato &e%.2f &afor &e%d &aminutes.", onlinePlayer.getName(), amount, minutes)));
    }

    /**
     * Command executor for /gmulti
     *
     * @param sender The command executor
     * @param time   The duration of the multiplier
     * @param amount Time multiplier$
     * @author Unknown
     */
    private void setupGlobalMultiplier(CommandSender sender, int time, double amount) {

        GLOBAL_MULTIPLIER.addMultiplier(amount);
        GLOBAL_MULTIPLIER.addDuration(time);
        sender.sendMessage(Text.colorize(String.format("&aYou have set the &eGlobal Multiplier &ato &e%.2f &afor &e%d &aminutes.", amount, time)));
    }

    /**
     * Return the global multiplier
     *
     * @return The global multiplier
     * @author Unknown
     */
    public double getGlobalMultiplier() {
        return GLOBAL_MULTIPLIER.getMultiplier();
    }

    /**
     * Return the player's personal multiplier
     *
     * @param p The player
     * @return The player's personal multiplier
     * @author Unknown
     */
    public double getPersonalMultiplier(Player p) {
        return personalMultipliers.getOrDefault(p.getUniqueId(), Multiplier.getDefaultPlayerMultiplier(p.getUniqueId())).getMultiplier();
    }

    /**
     * Return the player's rank multiplier
     *
     * @param p The player
     * @return The player's rank multiplier
     * @author Unknown
     */
    public double getRankMultiplier(Player p) {
        return rankMultipliers.getOrDefault(p.getUniqueId(), Multiplier.getDefaultPlayerMultiplier()).getMultiplier();
    }

    /**
     * Returns player's armour multiplier
     * @author _LightDream
     * @param p Player
     * @return player's armour multiplier
     */
    public double getArmourMultiplier(Player p) {
        if (armourMultipliers.contains(p.getUniqueId())) {
            return (int) core.getFileManager().getConfig("banana-currency.yml").get("banana-armour-multi") / 100.0;
        } else {
            return 0;
        }
    }

    /**
     * Removes the player's personal multiplier
     *
     * @param uuid The UUID of the player
     * @author Unknown
     */
    public void removePersonalMultiplier(UUID uuid) {
        personalMultipliers.remove(uuid);
    }

    /**
     * Calculates the player's rank multiplier
     *
     * @param p The player
     * @return The multiplier
     * @author Unknown
     */
    private Multiplier calculateRankMultiplier(Player p) {
        PlayerMultiplier toReturn = new PlayerMultiplier(p.getUniqueId(), 0.0, -1);

        for (String perm : permissionToMultiplier.keySet()) {
            if (p.hasPermission(perm)) {
                toReturn.addMultiplier(permissionToMultiplier.get(perm));
                break;
            }
        }

        return toReturn;
    }

    /**
     * Function adds multi time to the existing player's multi if he has any
     *
     * @param sender  The command executor
     * @param player  The name of the player that will get the added time to the multiplier
     * @param minutes Time added to the player's multiplier
     * @author _LightDream
     */
    @Deprecated
    private void addMultiplierTime(CommandSender sender, Player player, int minutes) {
        UUID targetPlayerUUID = player.getUniqueId();
        if (personalMultipliers.containsKey(targetPlayerUUID)) {
            personalMultipliers.get(targetPlayerUUID).addDuration(minutes);
        }

        String senderMessage = messages.get("added_multi_sent");
        String playerMessage = messages.get("added_multi_received");

        senderMessage = senderMessage.replace("%time%", String.valueOf(minutes));
        senderMessage = senderMessage.replace("%player%", player.getDisplayName());
        playerMessage = playerMessage.replace("%time%", String.valueOf(minutes));

        sender.sendMessage(Text.colorize(senderMessage));
        player.sendMessage(Text.colorize(playerMessage));
    }

    /**
     * Sends the help commands to the sender
     *
     * @param sender The player who requests the help commands
     */
    private void helpCommand(CommandSender sender) {
        List<String> helpList = (List<String>) config.get("help-command");

        for (String line : helpList) {
            sender.sendMessage(Text.colorize(line));
        }
    }

    /**
     * Gives player armour multiplier
     *
     * @param player The player's UUID
     * @author _LightDream
     */
    public void addArmourMultiplier(UUID player) {
        armourMultipliers.add(player);
    }

    /**
     * Removes the player's armour multiplier
     *
     * @param player The player's UUID
     * @author _LightDream
     */
    public void removeArmourMultiplier(UUID player) {
        armourMultipliers.remove(player);
    }


}
