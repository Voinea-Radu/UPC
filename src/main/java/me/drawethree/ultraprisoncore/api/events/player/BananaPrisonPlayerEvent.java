package me.drawethree.ultraprisoncore.api.events.player;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;


public abstract class BananaPrisonPlayerEvent extends Event {

	@Getter
	protected OfflinePlayer player;

	/**
	 * Abstract UltraPrisonPlayerEvent
	 * @param player Player
	 */
	public BananaPrisonPlayerEvent(OfflinePlayer player) {
		this.player = player;
	}
}
