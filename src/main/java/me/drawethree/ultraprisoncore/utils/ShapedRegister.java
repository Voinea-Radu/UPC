package me.drawethree.ultraprisoncore.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ShapedRegister {
    private ItemStack output;
    private String[] rows;
    private Map<Character, ItemStack> ingredients = new HashMap<>();

    /**
     * Create a shaped recipe to craft the specified ItemStack. The
     * constructor merely determines the result and type; to set the actual
     * recipe, you'll need to call the appropriate methods.
     *
     * @param result The item you want the recipe to create.
     * @see ShapedRecipe#shape(String...)
     * @see ShapedRecipe#setIngredient(char, Material)
     * @see ShapedRecipe#setIngredient(char, ItemStack)
     */
    public ShapedRegister(ItemStack result) {
        output = new ItemStack(result);
    }

    /**
     * Set the shape of this recipe to the specified rows. Each character
     * represents a different ingredient; exactly what each character
     * represents is set separately. The first row supplied corresponds with
     * the upper most part of the recipe on the workbench e.g. if all three
     * rows are supplies the first string represents the top row on the
     * workbench.
     *
     * @param shape The rows of the recipe (up to 3 rows).
     * @return The changed recipe, so you can chain calls.
     */
    public ShapedRegister shape(String... shape) {

        Validate.notNull(shape, "Must provide a shape");
        Validate.isTrue(shape.length > 0 && shape.length < 4, "Crafting recipes should be 1, 2, 3 rows, not ",
                shape.length);

        for (String row : shape) {
            Validate.notNull(row, "Shape cannot have null rows");
            Validate.isTrue(row.length() > 0 && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ",
                    row.length());
        }

        this.rows = new String[shape.length];
        for (int i = 0; i < shape.length; i++) {
            this.rows[i] = shape[i];
        }

        Map<Character, ItemStack> newIng = new HashMap<>();
        for (String row : shape)
            for (char c : row.toCharArray())
                newIng.put(c, ingredients.get(c));
        ingredients = newIng;

        rows = shape;

        return this;
    }

    public ShapedRegister setIngredient(char key, Material mat) {
        return setIngredient(key, new ItemStack(mat, 1));
    }

    public ShapedRegister setIngredient(char key, ItemStack item) {
        Validate.isTrue(ingredients.containsKey(Character.valueOf(key)), "Symbol does not appear in the shape:", key);
        ingredients.put(key, item);
        return this;
    }

    /**
     * Registers the Recipe in the server.
     */
    public void register() {
        ShapedRecipe sr = new ShapedRecipe(output);
        sr.shape(rows);
        try {
            Field f = sr.getClass().getDeclaredField("ingredients");
            f.setAccessible(true);
            f.set(sr, ingredients);
            Bukkit.addRecipe(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}