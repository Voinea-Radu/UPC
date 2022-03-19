package me.drawethree.ultraprisoncore.gangs;


import lombok.Getter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.BananaPrisonModule;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.gangs.commands.GangCommand;
import me.drawethree.ultraprisoncore.gangs.managers.GangsManager;
import me.lucko.helper.Commands;
import me.lucko.helper.text.Text;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public final class BananaPrisonGangs implements BananaPrisonModule {

    public static final String GANGS_ADMIN_PERM = "gangs.admin";
    @Getter
    private static BananaPrisonGangs instance;

    @Getter
    private FileManager.Config config;

    @Getter
    private GangsManager gangsManager;

    @Getter
    private BananaPrisonCore core;

    private HashMap<String, String> messages;

    private boolean enabled;

    public BananaPrisonGangs(BananaPrisonCore prisonCore) {
        instance = this;
        this.core = prisonCore;
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void reload() {

        this.config.reload();

        this.loadMessages();

        this.gangsManager.reloadConfig();
    }


    @Override
    public void enable() {

        this.enabled = true;
        this.config = this.core.getFileManager().getConfig("gangs.yml").copyDefaults(true).save();

        this.loadMessages();

        this.gangsManager = new GangsManager(this);

        this.registerCommands();
        this.registerEvents();
    }


    @Override
    public void disable() {
        this.gangsManager.saveDataOnDisable();
        this.enabled = false;

    }

    @Override
    public String getName() {
        return "Gangs";
    }

    private void registerEvents() {

    }

    private void registerCommands() {
        Commands.create()
                .handler(c -> {
                    if (c.args().size() == 0 && c.sender() instanceof Player) {
                        GangCommand.getCommand("help").execute(c.sender(), c.args());
                        return;
                    }
                    GangCommand subCommand = GangCommand.getCommand(Objects.requireNonNull(c.rawArg(0)));
                    if (subCommand != null) {
                        subCommand.execute(c.sender(), c.args().subList(1, c.args().size()));
                    } else {
                        GangCommand.getCommand("help").execute(c.sender(), c.args());
                    }
                }).registerAndBind(core, "g", "gang", "gangs");
    }

    private void loadMessages() {
        messages = new HashMap<>();
        for (String key : this.getConfig().get().getConfigurationSection("messages").getKeys(false)) {
            messages.put(key, Text.colorize(this.getConfig().get().getString("messages." + key)));
        }
    }

    public String getMessage(String key) {
        return messages.get(key);
    }
}
