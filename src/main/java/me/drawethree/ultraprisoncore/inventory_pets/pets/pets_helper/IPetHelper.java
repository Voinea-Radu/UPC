package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import org.bukkit.entity.Player;

import java.util.Map;

public interface IPetHelper {


    void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet);

}
