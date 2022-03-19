package me.drawethree.ultraprisoncore.inventory_pets;

import lombok.Getter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.BananaPrisonModule;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "ConstantConditions"})
public class BananaPrisonInventoryPets implements BananaPrisonModule {

    public static final String MODULE_NAME = "BananaInventoryPets";

    @Getter
    private final BananaPrisonInventoryPets instance;
    @Getter
    private final BananaPrisonCore core;
    @Getter
    private final FileManager.Config config;
    private final HashMap<Player, Map<String, Object>> explosiveBreakPlayerList = new HashMap<>();
    private final HashMap<Block, HashMap<Player, Map<String, Object>>> goldenWool = new HashMap<>();
    private final HashMap<Block, HashMap<Player, Map<String, Object>>> bananaTree = new HashMap<>();
    private final HashMap<Block, HashMap<Player, Map<String, Object>>> slimeChest = new HashMap<>();
    private boolean enabled;

    public BananaPrisonInventoryPets(BananaPrisonCore UltraPrisonCore) {
        instance = this;
        this.core = UltraPrisonCore;
        this.config = core.getFileManager().getConfig("pets.yml");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void reload() {
        //TODO: Implement
    }

    @Override
    public void enable() {
        enabled = true;
        registerSchedule();
        registerEvents();
        registerCommands();
    }

    @Override
    public void disable() {
        enabled = false;

    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    private void registerSchedule() {
        Schedulers.bukkit().runTaskTimer(core, () -> {
            for (Player player : Players.all()) {
                for (int inventoryIndex = 0; inventoryIndex < player.getInventory().getSize(); inventoryIndex++) {
                    ItemStack item = player.getInventory().getItem(inventoryIndex);

                    if (validatePet(item)) {
                        Pet pet = new Pet(core, item, player.getUniqueId());
                        pet.executeFunction();
                        pet.updateItem();
                        player.getInventory().setItem(inventoryIndex, pet.getPetItem());
                    }
                }
            }
        }, (int) config.get("occurrence-interval-check"), (int) config.get("occurrence-interval-check"));
    }

    private void registerEvents() {
        Events.subscribe(BlockBreakEvent.class)
                .handler(event -> {
                    if (explosiveBreakPlayerList.containsKey(event.getPlayer())) {
                        Block block = event.getBlock();

                        Map<String, Object> map = explosiveBreakPlayerList.get(event.getPlayer());

                        double chance = (double) map.get("occurrence");

                        if (!Utils.checkExecute(chance)) {
                            return;
                        }

                        int radius = (int) map.get("radius");

                        block.getWorld().createExplosion(block.getLocation(), radius);
                        String parsedMessage = (String) map.get("explosion-message");
                        event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                    }

                    if (goldenWool.containsKey(event.getBlock())) {
                        HashMap<Player, Map<String, Object>> playerMap = goldenWool.get(event.getBlock());
                        for (Player player : playerMap.keySet()) {
                            Map<String, Object> map = playerMap.get(player);
                            if (player.equals(event.getPlayer())) {
                                double money = Utils.generateRandom((double) map.get("min"), (double) map.get("max"));
                                core.getEconomy().depositPlayer(player, money);

                                String parsedMessage = (String) map.get("found-message");
                                parsedMessage = parsedMessage.replace("%money%", String.valueOf((int) money));

                                player.sendMessage(Text.colorize(parsedMessage));
                                removeGoldenWool(event.getBlock());
                                event.getBlock().setType(Material.AIR);
                            } else {
                                String parsedMessage = (String) map.get("someone-else");

                                event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (bananaTree.containsKey(event.getBlock())) {
                        HashMap<Player, Map<String, Object>> playerMap = bananaTree.get(event.getBlock());
                        for (Player player : playerMap.keySet()) {
                            Map<String, Object> map = playerMap.get(player);
                            if (player.equals(event.getPlayer())) {
                                double bananas = Utils.generateRandom((double) map.get("min"), (double) map.get("max"));
                                core.getEconomy().depositPlayer(player, bananas);

                                String parsedMessage = (String) map.get("found-message");
                                parsedMessage = parsedMessage.replace("%bananas%", String.valueOf((int) bananas));

                                player.sendMessage(Text.colorize(parsedMessage));
                                removeBananaTree(event.getBlock());
                                event.getBlock().setType(Material.AIR);
                            } else {
                                String parsedMessage = (String) map.get("someone-else");

                                event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (slimeChest.containsKey(event.getBlock())) {
                        HashMap<Player, Map<String, Object>> playerMap = slimeChest.get(event.getBlock());
                        for (Player player : playerMap.keySet()) {
                            Map<String, Object> map = playerMap.get(player);
                            if (player.equals(event.getPlayer())) {
                                String command = (String) map.get("command");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                                String parsedMessage = (String) map.get("found-message");

                                event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                                removeSlimeChest(event.getBlock());
                                event.getBlock().setType(Material.AIR);
                                event.setCancelled(true);
                            } else {
                                String parsedMessage = (String) map.get("someone-else");

                                event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                            }
                        }
                    }

                }).bindWith(core);
        Events.subscribe(PlayerInteractEvent.class)
                .handler(event -> {
                    if (goldenWool.containsKey(event.getClickedBlock())) {
                        HashMap<Player, Map<String, Object>> playerMap = goldenWool.get(event.getClickedBlock());
                        for (Player player : playerMap.keySet()) {
                            Map<String, Object> map = playerMap.get(player);
                            if (player.equals(event.getPlayer())) {
                                double money = Utils.generateRandom((double) map.get("min"), (double) map.get("max"));
                                core.getEconomy().depositPlayer(player, money);

                                String parsedMessage = (String) map.get("found-message");
                                parsedMessage = parsedMessage.replace("%money%", String.valueOf((int) money));

                                player.sendMessage(Text.colorize(parsedMessage));
                                removeGoldenWool(event.getClickedBlock());
                                event.getClickedBlock().setType(Material.AIR);
                            } else {
                                String parsedMessage = (String) map.get("someone-else");

                                event.getPlayer().getPlayer().sendMessage(Text.colorize(parsedMessage));
                            }
                        }
                    }
                    if (bananaTree.containsKey(event.getClickedBlock())) {
                        HashMap<Player, Map<String, Object>> playerMap = bananaTree.get(event.getClickedBlock());
                        for (Player player : playerMap.keySet()) {
                            Map<String, Object> map = playerMap.get(player);
                            if (player.equals(event.getPlayer())) {
                                int bananas = Utils.generateRandom((int) map.get("min"), (int) map.get("max"));
                                core.getBananaCurrency().giveBanana(Bukkit.getConsoleSender(), player, bananas);

                                String parsedMessage = (String) map.get("found-message");
                                parsedMessage = parsedMessage.replace("%bananas%", String.valueOf(bananas));

                                player.sendMessage(Text.colorize(parsedMessage));
                                removeBananaTree(event.getClickedBlock());
                                event.getClickedBlock().setType(Material.AIR);
                            } else {
                                String parsedMessage = (String) map.get("someone-else");

                                event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                            }
                        }
                    }

                    if (slimeChest.containsKey(event.getClickedBlock())) {
                        HashMap<Player, Map<String, Object>> playerMap = slimeChest.get(event.getClickedBlock());
                        for (Player player : playerMap.keySet()) {
                            Map<String, Object> map = playerMap.get(player);
                            if (player.equals(event.getPlayer())) {
                                String command = (String) map.get("command");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                                String parsedMessage = (String) map.get("found-message");

                                player.sendMessage(Text.colorize(parsedMessage));
                                removeSlimeChest(event.getClickedBlock());
                                event.getClickedBlock().setType(Material.AIR);
                            } else {
                                String parsedMessage = (String) map.get("someone-else");

                                event.getPlayer().sendMessage(Text.colorize(parsedMessage));
                            }
                        }
                    }
                }).bindWith(core);
    }

    private void registerCommands() {
        Commands.create()
                .assertPermission("bananaprison.pets.givepet")
                .handler(c -> {
                    if (c.args().size() == 3) {
                        Player player = Players.getNullable(c.rawArg(0));
                        try {
                            String type = c.rawArg(1);
                            int level = Integer.parseInt(c.rawArg(2));
                            player.getInventory().addItem(new Pet(core, Pet.stringToPetID(type), player.getUniqueId(), level).getPetItem());

                        } catch (Exception ignored) {
                        }
                    }
                }).registerAndBind(core, "givepet", "gp");
        Commands.create()
                .assertPermission("bananaprison.pets.openinventory")
                .assertPlayer()
                .handler(c -> {
                    if (c.args().size() == 0) {
                        c.sender().openInventory(core.getGuiManager().getPetInventory(null, c.sender()));
                    }
                }).registerAndBind(core, "pets", "pet");
    }

    public boolean validatePet(ItemStack item) {
        return Utils.getNBTString(item, "type") != null && !Utils.getNBTString(item, "type").equals("");
    }

    public void addExplosiveBreakPlayer(Player player, Map<String, Object> map) {
        explosiveBreakPlayerList.put(player, map);
    }

    public void removeExplosiveBreakPlayer(Player player) {
        explosiveBreakPlayerList.remove(player);
    }

    public void addGoldenWool(Block block, HashMap<Player, Map<String, Object>> data) {
        goldenWool.put(block, data);
    }

    public void removeGoldenWool(Block block) {
        goldenWool.remove(block);
    }

    public void addBananaTree(Block block, HashMap<Player, Map<String, Object>> data) {
        bananaTree.put(block, data);
    }

    public void removeBananaTree(Block block) {
        bananaTree.remove(block);
    }

    public void addSlimeChest(Block block, HashMap<Player, Map<String, Object>> data) {
        slimeChest.put(block, data);
    }

    public void removeSlimeChest(Block block) {
        slimeChest.remove(block);
    }
}
