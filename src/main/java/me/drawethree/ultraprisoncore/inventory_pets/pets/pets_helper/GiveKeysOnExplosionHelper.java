package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.BananaPrisonInventoryPets;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveKeysOnExplosionHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        BananaPrisonInventoryPets pets = plugin.getPets();

        pets.addExplosiveBreakPlayer(player, map);
        int duration = (int) map.get("duration");

        Schedulers.async().runLater(() -> pets.removeExplosiveBreakPlayer(player), duration * 20L);
    }
}
