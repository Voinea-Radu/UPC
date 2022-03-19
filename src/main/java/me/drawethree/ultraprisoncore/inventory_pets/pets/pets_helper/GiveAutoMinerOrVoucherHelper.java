package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveAutoMinerOrVoucherHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {

        if (Utils.generateRandom(1, 2) == 1) {
            int time = Utils.generateRandom((int) map.get("min"), (int) map.get("max"));

            plugin.getAutoMiner().givePlayerAutoMinerTime(Bukkit.getConsoleSender(), player, time);

            String rawMessage = (String) map.get("message-autominer");
            String parsedMessage = pet.parsePlaceholders(rawMessage);

            parsedMessage = parsedMessage.replace("%time%", String.valueOf(time));

            player.sendMessage(Text.colorize(parsedMessage));
        } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), (String) map.get("voucher-command"));

            String rawMessage = (String) map.get("message-voucher");
            String parsedMessage = pet.parsePlaceholders(rawMessage);

            player.sendMessage(Text.colorize(parsedMessage));
        }
    }
}
