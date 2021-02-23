package me.fckml.crates.listeners.events;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.managers.Winning;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CratePreviewEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Crates crates;
	private Player player;
	private Crate crate;
	private boolean canceled = false;

	public CratePreviewEvent(Player player, String crateName, Crates crates) {
		this.crates = crates;
		this.player = player;
		this.crate = crates.getConfigHandler().getCrates().get(crateName.toLowerCase());
	}

	public void doEvent() {
		if (!crate.isPreview())
			return; // Preview is disabled
		List<Winning> items = crate.getWinnings();
		Integer size = 54;
		if (items.size() <= 9) {
			size = 9;
		} else if (items.size() <= 18) {
			size = 18;
		} else if (items.size() <= 27) {
			size = 27;
		} else if (items.size() <= 36) {
			size = 36;
		} else if (items.size() <= 45) {
			size = 45;
		}
		int i = 0;
		Inventory inventory = Bukkit.createInventory(null, size, CC.translate(crate.getName(true) + " " + "&7Loot"));
		for (Winning winning : items) {
			ItemStack itemStack = winning.getPreviewItemStack();
			if (itemStack == null)
				continue;
			inventory.setItem(i, itemStack);
			i++;
		}
		player.openInventory(inventory);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCanceled() {
		return this.canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Crates getCratesPlugin() {
		return crates;
	}

}