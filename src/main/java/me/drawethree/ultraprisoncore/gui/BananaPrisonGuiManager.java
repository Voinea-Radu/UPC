package me.drawethree.ultraprisoncore.gui;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.gui.gui_helper.BananaPriceHelper;
import me.drawethree.ultraprisoncore.gui.gui_helper.ChosePetHelper;
import me.drawethree.ultraprisoncore.gui.gui_helper.IGuiHelper;
import me.drawethree.ultraprisoncore.gui.gui_helper.PetFunctionHelper;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.ItemBuilder;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.Events;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "unchecked", "MismatchedQueryAndUpdateOfCollection"})
public class BananaPrisonGuiManager {

    private final Inventory petInventory;
    private final BananaPrisonCore core;
    private final FileManager.Config config;
    private final HashMap<String, IGuiHelper> clickFunctionMap = new HashMap<>();
    private final HashMap<String, IGuiHelper> priceFunctionMap = new HashMap<>();
    private final List<Map<String, Object>> mapList;


    public BananaPrisonGuiManager(BananaPrisonCore core) {
        this.core = core;
        this.config = core.getFileManager().getConfig("gui.yml");

        //PetFoodInventory
        petInventory = Bukkit.createInventory(null, 54, Text.colorize((String) config.get("pet-gui.title")));

        ItemStack fillItem = new ItemStack(Material.getMaterial((String) config.get("pet-gui.fill.material")), 1, (short) ((int) config.get("pet-gui.fill.data")));

        for (int i = 0; i < 54; i++) {
            petInventory.setItem(i, fillItem);
        }

        registerEvents();

        clickFunctionMap.put("pet-function", new PetFunctionHelper());
        clickFunctionMap.put("chose-pet", new ChosePetHelper());

        priceFunctionMap.put("banana", new BananaPriceHelper());

        this.mapList = (List<Map<String, Object>>) config.get("pet-gui.items");
    }

    public Inventory getPetInventory(Pet pet, Player player) {

        if (player == null) {
            return null;
        }

        Inventory inventory = petInventory;

        if (pet == null) {

            List<Map<String, Object>> mapList = (List<Map<String, Object>>) config.get("pet-gui.items");

            for (Map<String, Object> map : mapList) {
                System.out.println(map);
                System.out.println((String) map.get("material"));
                ItemStack item = new ItemBuilder(Material.getMaterial((String) map.get("material")))
                        .setLore(Utils.colorizeList((List<String>) map.get("lore")))
                        .setDisplayName(Text.colorize((String) map.get("name")))
                        .addNBTStringList("click-function", (List<String>) map.get("click-function"))
                        .addNBTString("price-function", (String) map.get("price-function"))
                        .addNBTString("type", (String) map.get("id"))
                        .build();

                inventory.setItem((Integer) map.get("position"), item);
            }

            return inventory;
        } else {
            //TODO: Ask the player to click on the pet he wants to feed
            return null;
        }
    }

    public void registerEvents() {
        Events.subscribe(InventoryClickEvent.class)
                .handler(event -> {
                    if (event.getInventory().getTitle().equals(Text.colorize((String) config.get("pet-gui.title")))) {
                        event.setCancelled(true);
                        ItemStack item = event.getCurrentItem();
                        Player player = (Player) event.getWhoClicked();
                        ItemStack petItem = null;
                        Pet pet = new Pet(core, petItem, player.getUniqueId());

                        for (Map<String, Object> map : mapList){
                            if(map.get("id").equals("PET")){
                                petItem = event.getInventory().getItem((Integer) map.get("position"));
                            }
                        }
                        if(!core.getPets().validatePet(petItem)){
                            if(core.getPets().validatePet(item)){
                                player.openInventory(getPetInventory(pet, player));
                            }
                            return;
                        }
                        List<String> functions = Utils.getNBTStringList(item, "click-function");
                        if(functions == null || functions.size() == 0) {
                            return;
                        }

                        for (String function : functions){
                            clickFunctionMap.get(function).execute(core, player, pet);
                        }

                        String priceFunction = Utils.getNBTString(item, "price-function");

                        if(priceFunction==null|| priceFunction.equals("")){
                            return;
                        }

                        //priceFunctionMap.get(priceFunction).execute(core,player,);



                    }
                }).bindWith(core);
    }
}
