package me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.Pet;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.jet315.prisonmines.JetsPrisonMines;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import me.jet315.prisonmines.mine.Mine;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpawnBananaTreeHelper implements IPetHelper {

    @Override
    public void execute(BananaPrisonCore plugin, Map<String, Object> map, Player player, Pet pet) {
        JetsPrisonMinesAPI api = ((JetsPrisonMines) Bukkit.getPluginManager().getPlugin("JetsPrisonMines")).getAPI();

        Location location = player.getLocation();
        ArrayList<Mine> mines;

        mines = api.getMinesByLocation(location);
        if (mines.size() == 0) {
            location.setY(location.getY() - 1);
            mines = api.getMinesByLocation(location);
            if (mines.size() == 0) {
                String parsedMessage = (String) map.get("fail-message");

                player.sendMessage(Text.colorize(parsedMessage));
                return;
            }
        }

        Location minPoint = mines.get(0).getMineRegion().getMinPoint();
        Location maxPoint = mines.get(0).getMineRegion().getMaxPoint();

        Location placeLocation = new Location(location.getWorld(), Utils.generateRandom((int) minPoint.getX(), (int) maxPoint.getX()), Utils.generateRandom((int) minPoint.getY(), (int) maxPoint.getY()), Utils.generateRandom((int) minPoint.getZ(), (int) maxPoint.getZ()));

        Block block = location.getWorld().getBlockAt(placeLocation);

        block.setType(Material.LEAVES);

        String rawMessage = (String) map.get("spawn-message");
        String parsedMessage = pet.parsePlaceholders(rawMessage);

        HashMap<Player, Map<String, Object>> tmp = new HashMap<>();
        tmp.put(player, map);
        plugin.getPets().addBananaTree(block, tmp);

        player.sendMessage(Text.colorize(parsedMessage));
    }
}
