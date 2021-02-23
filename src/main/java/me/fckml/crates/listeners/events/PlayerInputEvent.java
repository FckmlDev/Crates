package me.fckml.crates.listeners.events;

import me.fckml.crates.utils.SignInputHandler;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInputEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	public IChatBaseComponent[] lines;

	public PlayerInputEvent(Player player, IChatBaseComponent[] lines) {
		this.player = player;
		this.lines = lines;
		SignInputHandler.ejectNetty(player);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public IChatBaseComponent[] getLines() {
		return lines;
	}

}