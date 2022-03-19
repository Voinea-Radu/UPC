package me.drawethree.ultraprisoncore.enchants.enchants.implementations;

import me.drawethree.ultraprisoncore.enchants.BananaPrisonEnchants;
import me.drawethree.ultraprisoncore.enchants.enchants.BananaPrisonEnchantment;
import me.drawethree.ultraprisoncore.utils.compat.CompMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MeteorsEnchant extends BananaPrisonEnchantment {

    private double chance;
    private int radius = 5;

    public MeteorsEnchant(BananaPrisonEnchants instance) {
        super(instance, 11);
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (this.chance * enchantLevel >= ThreadLocalRandom.current().nextDouble(100)) {
            Block b = e.getBlock();
            List<IWrappedRegion> regions = WorldGuardWrapper.getInstance().getRegions(b.getLocation()).stream().filter(reg -> reg.getId().toLowerCase().startsWith("mine")).collect(Collectors.toList());
            if (regions.size() > 0) {
                IWrappedRegion region = regions.get(0);
                ICuboidSelection selection = (ICuboidSelection) region.getSelection();

                int minX = Math.min(selection.getMinimumPoint().getBlockX(), selection.getMaximumPoint().getBlockX());
                int maxX = Math.max(selection.getMinimumPoint().getBlockX(), selection.getMaximumPoint().getBlockX());

                int minZ = Math.min(selection.getMinimumPoint().getBlockZ(), selection.getMaximumPoint().getBlockZ());
                int maxZ = Math.max(selection.getMinimumPoint().getBlockZ(), selection.getMaximumPoint().getBlockZ());

                int minY = Math.min(selection.getMinimumPoint().getBlockY(), selection.getMaximumPoint().getBlockY());
                int maxY = Math.max(selection.getMinimumPoint().getBlockY(), selection.getMaximumPoint().getBlockY());

                int fortuneLevel = plugin.getApi().getEnchantLevel(e.getPlayer().getItemInHand(), 3);
                double totalDeposit = 0;
                int amplifier = fortuneLevel == 0 ? 1 : fortuneLevel + 1;
                int blockCount = 0;
                List<Block> blocksAffected = new ArrayList<>();

                for (int i = 0; i < 5; i++) {
                    int ranX = ThreadLocalRandom.current().nextInt(minX, maxX);
                    int ranY = ThreadLocalRandom.current().nextInt(minY, maxY);
                    int ranZ = ThreadLocalRandom.current().nextInt(minZ, maxZ);

                    final Location startLocation = new Location(e.getBlock().getWorld(), ranX, ranY, ranZ);

                    startLocation.getWorld().createExplosion(startLocation.getX(), startLocation.getY(), startLocation.getZ(), 0F, false, false);

                    startLocation.getWorld().strikeLightningEffect(startLocation);

                    for (int x = startLocation.getBlockX() - radius / 2; x <= startLocation.getBlockX() + radius / 2; x++) {
                        for (int z = startLocation.getBlockZ() - radius / 2; z <= startLocation.getBlockZ() + radius / 2; z++) {
                            for (int y = startLocation.getBlockY() - radius / 2; y <= startLocation.getBlockY() + radius / 2; y++) {
                                Block b1 = b.getWorld().getBlockAt(x, y, z);
                                if (region.contains(b1.getLocation()) && b1.getType() != Material.AIR) {
                                    blockCount++;
                                    blocksAffected.add(b1);
                                    if (plugin.getCore().getAutoSell().isEnabled() && plugin.getCore().getAutoSell().hasAutoSellEnabled(e.getPlayer())) {
                                        totalDeposit += ((plugin.getCore().getAutoSell().getPriceForBrokenBlock(region, b1) + 0.0) * amplifier);
                                    } else {
                                        e.getPlayer().getInventory().addItem(new ItemStack(b1.getType(), fortuneLevel + 1));
                                    }
                                    b1.setType(CompMaterial.AIR.toMaterial());
                                }
                            }
                        }
                    }
                }

                if (plugin.getCore().getJetsPrisonMinesAPI() != null) {
                    plugin.getCore().getJetsPrisonMinesAPI().blockBreak(blocksAffected);
                }

                plugin.getCore().getEconomy().depositPlayer(e.getPlayer(), totalDeposit);

                if (plugin.getCore().getAutoSell().isEnabled()) {
                    plugin.getCore().getAutoSell().addToCurrentEarnings(e.getPlayer(), totalDeposit);
                }

                plugin.getEnchantsManager().addBlocksBrokenToItem(e.getPlayer(), blockCount);
                plugin.getCore().getTokens().getTokensManager().addBlocksBroken(null, e.getPlayer(), blockCount);
                plugin.getCore().getTokens().handleBlockBreak(e.getPlayer(), blockCount);
            }
        }
    }

    @Override
    public void reload() {
        this.chance = plugin.getConfig().get().getDouble("enchants." + id + ".Chance");
    }
}
