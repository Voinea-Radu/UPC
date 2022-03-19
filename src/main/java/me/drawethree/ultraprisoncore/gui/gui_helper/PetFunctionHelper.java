package me.drawethree.ultraprisoncore.gui.gui_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import org.bukkit.entity.Player;

public class PetFunctionHelper implements IGuiHelper {

    @Override
    public void execute(BananaPrisonCore core, Player player, Pet pet) {
        pet.executeFunction();
    }
}
