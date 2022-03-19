package me.drawethree.ultraprisoncore.utils;

import me.lucko.helper.text3.Text;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Utils {

    public static double log(double a, double b) {
        return Math.log(a) / Math.log(b);
    }

    public static ItemStack setNBTInt(ItemStack item, String attribute, int value) {
        //Data getting
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

        //Data setting
        compound.set(attribute, new NBTTagInt(value));

        //Data adding
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack setNBTString(ItemStack item, String attribute, String value) {
        //Data getting
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

        //Data setting
        compound.set(attribute, new NBTTagString(value));

        //Data adding
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack setNBTStringList(ItemStack item, String attribute, List<String> values) {
        //Data getting
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

        //Data setting
        if (values.size() == 0) {
            return item;
        }

        compound.set(attribute + "-size", new NBTTagInt(values.size()));

        for (int i = 0; i < values.size(); i++) {
            compound.set(attribute + "-" + i, new NBTTagString(values.get(i)));
        }

        //Data adding
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static int getNBTInt(ItemStack item, String search) {
        //Data getting
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

        return compound.getInt(search);
    }

    public static String getNBTString(ItemStack item, String search) {
        //Data getting
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

        return compound.getString(search);
    }

    public static List<String> getNBTStringList(ItemStack item, String search) {
        //Data getting
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

        int size = compound.getInt(search + "-size");
        if (size == 0) {
            return null;
        }

        List<String> output = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            output.add(compound.getString(search + "-" + i));
        }
        return output;
    }


    public static List<String> colorizeList(List<String> input) {
        List<String> output = new ArrayList<>();

        for (String line : input) {
            output.add(Text.colorize(line));
        }

        return output;
    }

    public static boolean checkExecute(double chance) {

        double result = Math.random() * 101 + 0;

        return result < chance;
    }

    public static int generateRandom(int a, int b) {
        if (b < a) {
            return (int) Math.floor(Math.random() * (a - b + 1) + b);
        }
        return (int) Math.floor(Math.random() * (b - a + 1) + a);
    }

    public static double generateRandom(double a, double b) {
        if (b < a) {
            return Math.random() * (a - b + 1) + b;
        }
        return Math.random() * (b - a + 1) + a;
    }


}
