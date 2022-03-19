package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ExecuteCommandsHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        List<String> commands = (List<String>) map.get("commands");
        for (String command : commands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        String rawMessage = (String) map.get("message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);
        player.sendMessage(Text.colorize(parsedMessage));
    }
}
