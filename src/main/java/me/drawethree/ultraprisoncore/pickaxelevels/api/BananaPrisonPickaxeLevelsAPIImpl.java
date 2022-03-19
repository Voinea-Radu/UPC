package me.drawethree.ultraprisoncore.pickaxelevels.api;

import me.drawethree.ultraprisoncore.pickaxelevels.BananaPrisonPickaxeLevels;
import me.drawethree.ultraprisoncore.pickaxelevels.model.PickaxeLevel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BananaPrisonPickaxeLevelsAPIImpl implements BananaPrisonPickaxeLevelsAPI {

	private BananaPrisonPickaxeLevels plugin;

	public BananaPrisonPickaxeLevelsAPIImpl(BananaPrisonPickaxeLevels plugin) {
		this.plugin = plugin;
	}

	@Override
	public PickaxeLevel getPickaxeLevel(ItemStack item) {
		return this.plugin.getPickaxeLevel(item);
	}

	@Override
	public PickaxeLevel getPickaxeLevel(Player player) {
		ItemStack item = this.plugin.findPickaxe(player);
		return this.getPickaxeLevel(item);
	}
}
