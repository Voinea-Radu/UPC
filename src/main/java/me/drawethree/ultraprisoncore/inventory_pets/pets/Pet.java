package me.drawethree.ultraprisoncore.inventory_pets.pets;

import lombok.Getter;
import lombok.Setter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.utils.ItemBuilder;
import me.drawethree.ultraprisoncore.utils.Utils;
import me.lucko.helper.text3.Text;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unchecked", "FieldCanBeLocal", "MismatchedReadAndWriteOfArray", "unused"})
public class Pet {

    @Getter
    private final int[] xpGain = new int[4];
    private BananaPrisonCore plugin;
    private PetID id;
    private UUID owner;
    private String name = "";
    @Getter
    private int xp;
    private int level;
    @Getter
    private ItemStack petItem;
    private PetFunction function;
    @Getter
    @Setter
    private double functionsOccurrenceMultiplier = 1;
    private Map<String, Object> map;


    public Pet(BananaPrisonCore core, PetID id, UUID owner, int level) {
        if (id == null) {
            return;
        }
        this.owner = owner;
        this.plugin = core;
        this.id = convertToShortID(id);
        this.xp = 0;
        this.level = level;

        List<Map<String, Object>> mapList = (List<Map<String, Object>>) core.getFileManager().getConfig("pets.yml").get("pets");
        for (Map<String, Object> map : mapList) {
            if (map.get("id").equals(id.toString())) {
                this.map = map;
                this.name = (String) map.get("name");
                //TODO: Support heads
                this.petItem = new ItemBuilder(Material.getMaterial((String) map.get("material")))
                        .setDisplayName(Text.colorize(name))
                        .setLore(Utils.colorizeList(parsePlaceholders((List<String>) map.get("lore"))))
                        .addEnchant(Enchantment.DURABILITY, 1)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .addNBTString("type", id.toString())
                        .addNBTInt("xp", xp)
                        .addNBTInt("level", level)
                        .build();


                //TODO: If pet type is VP increase the occurrence of level 1 by 1% for every level

                List<Double> occurrence = (List<Double>) map.get("occurrence");

                List<Double> tmp = new ArrayList<>();
                for (Double var : occurrence) {
                    tmp.add(var * functionsOccurrenceMultiplier);
                }
                occurrence = tmp;

                function = new PetFunction(core, (List<Map<String, Object>>) map.get("pet-functions"), occurrence, owner, this);

                List<Map<String, Integer>> xpGainMapList = (List<Map<String, Integer>>) map.get("pet-functions");
                int i = 0;
                for (Map<String, Integer> levelXpGainMap : xpGainMapList) {
                    xpGain[i] = levelXpGainMap.get("xp-gain");
                    i++;
                }
                break;
            }
        }
    }


    public Pet(BananaPrisonCore core, ItemStack item, UUID owner) {
        this.owner = owner;
        this.plugin = core;
        id = stringToPetID(Utils.getNBTString(item, "type"));
        this.xp = Utils.getNBTInt(item, "xp");
        this.level = Utils.getNBTInt(item, "level");
        this.petItem = item;

        List<Map<String, Object>> mapList = (List<Map<String, Object>>) core.getFileManager().getConfig("pets.yml").get("pets");
        for (Map<String, Object> map : mapList) {
            if (map.get("id").equals(id.toString())) {
                this.map = map;
                this.name = (String) map.get("name");

                //TODO: If pet type is VP increase the occurrence of level 1 by 1% for every level

                List<Double> occurrence = (List<Double>) map.get("occurrence");

                List<Double> tmp = new ArrayList<>();
                for (Double var : occurrence) {
                    tmp.add(var * functionsOccurrenceMultiplier);
                }
                occurrence = tmp;

                function = new PetFunction(core, (List<Map<String, Object>>) map.get("pet-functions"), occurrence, owner, this);

                List<Map<String, Integer>> xpGainMapList = (List<Map<String, Integer>>) map.get("pet-functions");
                int i = 0;
                for (Map<String, Integer> levelXpGainMap : xpGainMapList) {
                    xpGain[i] = levelXpGainMap.get("xp-gain");
                    i++;
                }
                break;
            }
        }
    }

    public static String convertToShortString(String type) {
        switch (type) {
            case "KEY_PET":
                return "KP";
            case "BANANA_PET":
                return "BP";
            case "MONEY_PET":
                return "MP";
            case "TOKEN_PET":
                return "TP";
            case "VOUCHER_PET":
                return "VP";
            default:
                return type;
        }
    }

    public static PetID stringToPetID(String type) {

        String searchType = convertToShortString(type);

        switch (searchType) {
            case "KP":
                return PetID.KP;
            case "VP":
                return PetID.VP;
            case "BP":
                return PetID.BP;
            case "MP":
                return PetID.MP;
            case "TP":
                return PetID.TP;
        }
        return null;
    }

    private PetID convertToShortID(PetID id) {
        switch (id) {
            case KEY_PET:
                return PetID.KP;
            case BANANA_PET:
                return PetID.BP;
            case MONEY_PET:
                return PetID.MP;
            case TOKEN_PET:
                return PetID.TP;
            case VOUCHER_PET:
                return PetID.VP;
            default:
                return id;
        }
    }

    public void addXP(int xp) {
        this.xp += xp;
    }

    public int calculateXP(int level) {
        if (level == 0)
            return 0;

        double constant = Math.pow(1000, 13.0 / 10.0) - Math.pow(1000, 11.0 / 10.0);
        if (level < 10)
            return (int) ((level - 1) * constant);
        else
            return (int) ((level - 1) * (constant + Math.pow(1000, 12.0 / 10.0)));
    }

    public void executeFunction() {
        function.execute(0);
        if (level >= 10) {
            function.execute(1);
        }
        if (level >= 20) {
            function.execute(2);
        }
        if (level >= 30) {
            function.execute(3);
        }
    }

    public String parsePlaceholders(String rawText) {
        String parsedText = rawText;

        String progressChar = ":";
        StringBuilder progressBar = new StringBuilder();

        //[::::::::::::::::::::::::::::::::::::::::::::::::::] x50

        //xp_needed ... 100
        //xp .......... x

        float progress = (xp * 100f) / calculateXP(level + 1);

        int tmp = (int) (progress / 2);

        for (int i = 0; i < tmp; i++) {
            progressBar.append("&a").append(progressChar);
        }
        for (int i = tmp; i < 50; i++) {
            progressBar.append("&7").append(progressChar);
        }

        parsedText = parsedText.replace("%level%", String.valueOf(level));
        parsedText = parsedText.replace("%xp%", String.valueOf(xp));
        parsedText = parsedText.replace("%next_level_xp%", String.valueOf(calculateXP(level + 1)));
        parsedText = parsedText.replace("%xp_needed%", String.valueOf(calculateXP(level + 1) - xp));
        parsedText = parsedText.replace("%progress%", "&8[" + progressBar + "&8]");
        parsedText = parsedText.replace("%progress_percent%", (int) progress + "%");

        return parsedText;
    }

    public List<String> parsePlaceholders(List<String> rawText) {
        List<String> output = new ArrayList<>();

        for (String line : rawText) {
            output.add(parsePlaceholders(line));
        }

        return output;
    }

    public void updateItem() {

        if (xp >= calculateXP(level + 1)) {
            xp -= calculateXP(level + 1);
            level++;
        }

        petItem = new ItemBuilder(Material.getMaterial((String) map.get("material")))
                .setDisplayName(Text.colorize(name))
                .setLore(Utils.colorizeList(parsePlaceholders((List<String>) map.get("lore"))))
                .addEnchant(Enchantment.DURABILITY, 1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .addNBTString("type", id.toString())
                .addNBTInt("xp", xp)
                .addNBTInt("level", level)
                .build();
    }
}
