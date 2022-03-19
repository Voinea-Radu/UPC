package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.api.enums.ReceiveCause;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class RandomTokenHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        int tokens = Utils.generateRandom((int) map.get("min"), (int) map.get("max"));
        plugin.getTokens().getTokensManager().giveTokens(player, tokens, Bukkit.getConsoleSender(), ReceiveCause.PET);

        String rawMessage = (String) map.get("message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);
        parsedMessage = parsedMessage.replace("%tokens%", String.valueOf(tokens));

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
