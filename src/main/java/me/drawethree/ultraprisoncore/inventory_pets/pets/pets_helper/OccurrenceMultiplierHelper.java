package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;

import java.util.Map;

public class OccurrenceMultiplierHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        double multiplier = (double) map.get("multiplier");
        int duration = (int) map.get("duration");

        pet.setFunctionsOccurrenceMultiplier(multiplier);

        Schedulers.async().runLater(() -> pet.setFunctionsOccurrenceMultiplier(1), duration * 20L);

        String rawMessage = (String) map.get("message");

        String parsedMessage = pet.parsePlaceholders(rawMessage);
        parsedMessage = parsedMessage.replace("%multiplier%", String.valueOf(multiplier));
        parsedMessage = parsedMessage.replace("%duration%", String.valueOf(duration));

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
