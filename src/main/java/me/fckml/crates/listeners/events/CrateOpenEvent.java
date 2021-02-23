package me.fckml.crates.listeners.events;

import me.fckml.crates.Crates;
import me.fckml.crates.managers.Crate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CrateOpenEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Crates crates;
	private Player player;
	private Crate crate;
	private Location blockLocation;

	public CrateOpenEvent(Player player, String crateName, Location blockLocation, Crates crates) {
		this.crates = crates;
		this.player = player;
		this.blockLocation = blockLocation;
		this.crate = crates.getConfigHandler().getCrates().get(crateName.toLowerCase());
	}

	public void doEvent() {
		Crates.getOpenHandler().getOpener(crate).startOpening(player, crate, blockLocation);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Crate getCrate() {
		return this.crate;
	}

	public Location getBlockLocation() {
		return blockLocation;
	}

	public Crates getCratesPlugin() {
		return crates;
	}

}