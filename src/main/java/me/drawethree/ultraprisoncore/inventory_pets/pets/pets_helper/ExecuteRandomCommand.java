package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ExecuteRandomCommand implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        List<String> commands = (List<String>) map.get("commands");

        int execute = Utils.generateRandom(0, commands.size());

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commands.get(execute));

        String rawMessage = (String) map.get("message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
