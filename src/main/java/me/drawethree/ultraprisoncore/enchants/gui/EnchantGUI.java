package me.drawethree.ultraprisoncore.enchants.gui;

import lombok.Getter;
import lombok.Setter;
import me.drawethree.ultraprisoncore.BananaPrisonCore;
import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import me.drawethree.ultraprisoncore.utils.compat.CompMaterial;
import me.jet315.prisonmines.events.MinePreResetEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.List;
import java.util.stream.Collectors;

public class EnchantGUI extends Gui {


	private static final String GUI_TITLE = Text.colorize(BananaPrisonEnchants.getInstance().getConfig().get().getString("enchant_menu.title"));
	private static final Item EMPTY_SLOT_ITEM = ItemStackBuilder.
			of(CompMaterial.fromString(BananaPrisonEnchants.getInstance().getConfig().get().getString("enchant_menu.empty_slots")).toItem()).buildItem().build();

	private static int PICKAXE_ITEM_SLOT = BananaPrisonEnchants.getInstance().getConfig().get().getInt("enchant_menu.pickaxe_slot");

	private static int GUI_LINES = BananaPrisonEnchants.getInstance().getConfig().get().getInt("enchant_menu.lines");

	@Getter
	@Setter
	private ItemStack pickAxe;

	public EnchantGUI(Player player, ItemStack pickAxe) {
		super(player, GUI_LINES, GUI_TITLE);

		this.pickAxe = pickAxe;

		Events.subscribe(InventoryCloseEvent.class)
				.filter(e -> e.getInventory().equals(this.getHandle()))
				.handler(e -> {

					if (!this.getPlayer().getItemInHand().equals(this.pickAxe)) {
						this.getPlayer().getInventory().remove(this.pickAxe);
					}

					ItemStack inHand = this.getPlayer().getItemInHand();
					this.getPlayer().setItemInHand(this.pickAxe);

					if (inHand != null) {
						this.getPlayer().getInventory().addItem(inHand);
					}

					Schedulers.async().runLater(() -> {
						((Player) e.getPlayer()).updateInventory();
					}, 5);
				}).bindWith(this);

		Events.subscribe(PlayerTeleportEvent.class, EventPriority.LOW)
				.filter(e -> e.getPlayer().getUniqueId().equals(this.getPlayer().getUniqueId()))
				.handler(e -> {
					List<IWrappedRegion> regions = WorldGuardWrapper.getInstance().getRegions(e.getFrom()).stream().filter(reg -> reg.getId().toLowerCase().startsWith("mine")).collect(Collectors.toList());

					if (regions.size() > 0) {
						this.getPlayer().closeInventory();
					}
				}).bindWith(this);

		if (BananaPrisonCore.getInstance().getJetsPrisonMinesAPI() != null) {

			Events.subscribe(MinePreResetEvent.class)
					.handler(e -> {
						if (e.getMine().isLocationInRegion(this.getPlayer().getLocation())) {
							this.getPlayer().closeInventory();
						}
					}).bindWith(this);
		}
	}


	@Override
	public void redraw() {

		// perform initial setup.
		if (isFirstDraw()) {
			for (int i = 0; i < this.getHandle().getSize(); i++) {
				this.setItem(i, EMPTY_SLOT_ITEM);
			}
		}

		for (BananaPrisonEnchantment enchantment : BananaPrisonEnchantment.all()) {
			if (!enchantment.isEnabled()) {
				continue;
			}
			int level = BananaPrisonEnchants.getInstance().getEnchantsManager().getEnchantLevel(this.pickAxe, enchantment.getId());
			this.setItem(enchantment.getGuiSlot(), BananaPrisonEnchants.getInstance().getEnchantsManager().getGuiItem(enchantment, this, level));
		}

		this.setItem(PICKAXE_ITEM_SLOT, Item.builder(pickAxe).build());
		//this.getPlayer().setItemInHand(pickAxe);
	}
}
