package me.drawethree.ultraprisoncore.autominer;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.lucko.helper.utils.Players;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

@Getter
public class AutoMinerRegion {

	private BananaPrisonAutoMiner parent;
	private World world;
	private IWrappedRegion region;
	private double moneyPerSecond;
	private double tokensPerSecond;

	private Task autoMinerTask;


	public AutoMinerRegion(BananaPrisonAutoMiner parent, World world, IWrappedRegion region, double moneyPerSecond, double tokensPerSecond) {
		this.parent = parent;
		this.world = world;
		this.region = region;
		this.moneyPerSecond = moneyPerSecond;
		this.tokensPerSecond = tokensPerSecond;

		this.autoMinerTask = Schedulers.async().runRepeating(() -> {
			for (Player p : Players.all()) {

				if (!p.getWorld().equals(this.world)) {
					continue;
				}

				if (region.contains(p.getLocation())) {
					if (!parent.hasAutoMinerTime(p)) {
						sendActionBar(p, parent.getMessage("auto_miner_disabled"));
						continue;
					} else {
						sendActionBar(p, parent.getMessage("auto_miner_enabled"));
						parent.getCore().getTokens().getApi().addTokens(p, (long) tokensPerSecond);
						if (moneyPerSecond > 0) {
							this.parent.getCore().getEconomy().depositPlayer(p, moneyPerSecond);
						}
						this.parent.decrementTime(p);
					}
				}
			}
		}, 20, 20);
	}

	private void sendActionBar(Player player, String message) {
		ActionBarAPI.sendActionBar(player, message);
	}

}
