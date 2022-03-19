package me.drawethree.ultraprisoncore.inventory_pets.pets;

import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.inventory_pets.pets.pets_helper.*;
import me.drawethree.ultraprisoncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"FieldCanBeLocal", "unchecked"})
public class PetFunction {

    private final boolean DEBUGGING = true;

    private final BananaPrisonCore plugin;
    private final List<Map<String, Object>> functionMap;
    private final List<Double> occurrences;
    private final Player player;
    private final Pet pet;
    private final HashMap<String, IPetHelper> helperFunctions;

    public PetFunction(BananaPrisonCore core, List<Map<String, Object>> functionMap, List<Double> occurrences, UUID uuid, Pet pet) {
        this.plugin = core;
        this.functionMap = functionMap;
        this.occurrences = occurrences;
        this.player = Bukkit.getPlayer(uuid);
        this.pet = pet;
        HashMap<String, IPetHelper> tmp = new HashMap<>();
        tmp.put("random-token", new RandomTokenHelper());
        tmp.put("random-money", new RandomMoneyHelper());
        tmp.put("random-banana", new RandomBananaHelper());
        tmp.put("token-multiplier", new TokenMultiplierHelper());
        tmp.put("occurrence-multiplier", new OccurrenceMultiplierHelper());
        tmp.put("spawn-golden-wool", new SpawnGoldenWoolHelper());
        tmp.put("spawn-banana-tree", new SpawnBananaTreeHelper());
        tmp.put("spawn-slime-chest", new SpawnSlimeChestHelper());
        tmp.put("execute-commands", new ExecuteCommandsHelper());
        tmp.put("execute-random-commands", new ExecuteRandomCommand());
        tmp.put("give-keys-on-explosion", new GiveKeysOnExplosionHelper());
        tmp.put("replace-item", new ReplaceItemHelper());
        tmp.put("give-autominer-or-voucher", new GiveAutoMinerOrVoucherHelper());
        tmp.put("give-black-market-voucher", new GiveBlackMarketVoucher());
        this.helperFunctions = tmp;
    }

    public void execute(int ID) {
        if (!Utils.checkExecute(occurrences.get(ID)) && !DEBUGGING) {
            return;
        }
        pet.addXP(pet.getXpGain()[ID]);
        Map<String, Object> map = functionMap.get(ID);
        for(String function : (List<String>) map.get("functions")){
            helperFunctions.get(function).execute(plugin, map, player, pet);
        }
    }

}
