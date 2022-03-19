package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ReplaceItemHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        List<String> replaceName = (List<String>) map.get("itemstack-names-to-remove");

        int count = 0;

        for (int inventoryIndex = 0; inventoryIndex < player.getInventory().getSize(); inventoryIndex++) {
            ItemStack item = player.getInventory().getItem(inventoryIndex);

            if (item != null) {
                if (item.getItemMeta() != null) {
                    if (item.getItemMeta().getDisplayName() != null) {
                        if (replaceName.contains(item.getItemMeta().getDisplayName())) {
                            count += item.getAmount();
                            player.getInventory().setItem(inventoryIndex, null);
                        }
                    } else {
                        if (replaceName.contains(item.getType().toString())) {
                            count += item.getAmount();
                            player.getInventory().setItem(inventoryIndex, null);
                        }
                    }
                } else {
                    if (replaceName.contains(item.getType().toString())) {
                        count += item.getAmount();
                        player.getInventory().setItem(inventoryIndex, null);
                    }
                }
            }
        }

        String command = (String) map.get("command");
        command = command.replace("%count%", String.valueOf(count));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);

        String rawMessage = (String) map.get("message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);
        parsedMessage = parsedMessage.replace("%count%", String.valueOf(count));

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
