package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class RandomBananaHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        int banana = Utils.generateRandom((int) map.get("min"), (int) map.get("max"));

        plugin.getBananaCurrency().giveBanana(Bukkit.getConsoleSender(), player, banana);

        String rawMessage = (String) map.get("message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);
        parsedMessage = parsedMessage.replace("%banana%", String.valueOf(banana));

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
