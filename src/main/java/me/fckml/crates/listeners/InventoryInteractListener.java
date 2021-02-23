package me.fckml.crates.listeners;

import me.fckml.crates.Crates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryInteractListener implements Listener {
	private Crates crates;

	public InventoryInteractListener(Crates crates) {
		this.crates = crates;
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null || (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains("Edit ")))
			return;
		if (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains("Loot")) {
			event.setCancelled(true);
		} else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains("Claim Crate Keys")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				ItemStack itemStack = event.getCurrentItem();
				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasLore()) {
					// We assume it's a key
					HashMap<String, Integer> keys = crates.getCrateHandler().getPendingKey(event.getWhoClicked().getUniqueId());
					Object[] keyNames = keys.keySet().toArray();
					if (event.getSlot() >= keyNames.length)
						return;
					String keyName = (String) keyNames[event.getSlot()];
					if (keyName != null) {
						crates.getCrateHandler().claimKey(event.getWhoClicked().getUniqueId(), keyName);
						if (crates.getCrateHandler().hasPendingKeys(event.getWhoClicked().getUniqueId()))
							((Player) event.getWhoClicked()).performCommand("crate claim");
						else
							event.getWhoClicked().closeInventory();
					}
				}
			}
		}
	}

}
