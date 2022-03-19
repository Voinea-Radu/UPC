package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;

import java.util.Map;

public class RandomMoneyHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        double money = Utils.generateRandom((double) map.get("min"), (double) map.get("max"));
        plugin.getEconomy().depositPlayer(player, money);

        String rawMessage = (String) map.get("message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);
        parsedMessage = parsedMessage.replace("%money%", String.valueOf((int)money));

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
