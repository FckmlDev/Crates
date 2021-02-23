package me.fckml.crates.handlers;

import me.fckml.crates.Crates;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.managers.Winning;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsHandler {
	private Crates crates;
	private Inventory settings;
	private Inventory crates;
	private HashMap<String, String> lastCrateEditing = new HashMap<>();

	public SettingsHandler(Crates crates) {
		this.crates = crates;
		setupSettingsInventory();
		setupCratesInventory();
	}

	public void setupSettingsInventory() {
		settings = Bukkit.createInventory(null, 9, "Crates Settings");

		ItemStack itemStack;
		ItemMeta itemMeta;
		List<String> lore;


		/** Crates */

		itemStack = new ItemStack(Material.CHEST);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Edit Crates");
		lore = new ArrayList<>();
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		settings.setItem(2, itemStack);


		/** Reload Config */

		Material material = Material.REDSTONE_TORCH_ON;

		itemStack = new ItemStack(material);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Reload Config");
		lore = new ArrayList<>();
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		settings.setItem(6, itemStack);
	}

	public void setupCratesInventory() {
		crates = Bukkit.createInventory(null, 54, "Crates");

		ItemStack itemStack;
		ItemMeta itemMeta;

		for (Map.Entry<String, Crate> entry : crates.getConfigHandler().getCrates().entrySet()) {
			Crate crate = entry.getValue();

			itemStack = new ItemStack(Material.CHEST);
			itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(crate.getName(true));
			itemStack.setItemMeta(itemMeta);
			crates.addItem(itemStack);
		}
	}

	public void openSettings(final Player player) {
		Bukkit.getScheduler().runTaskLater(crates, new Runnable() {
			@Override
			public void run() {
				player.openInventory(settings);
			}
		}, 1L);
	}

	public void openCrates(final Player player) {
		Bukkit.getScheduler().runTaskLater(crates, new Runnable() {
			@Override
			public void run() {
				player.openInventory(crates);
			}
		}, 1L);
	}

	public void openCrateWinnings(final Player player, String crateName) {
		Crate crate = crates.getConfigHandler().getCrates().get(crateName.toLowerCase());
		if (crate == null) {
			player.sendMessage(ChatColor.RED + "Unable to find " + crateName + " crate");
			return;
		}

		final Inventory inventory = Bukkit.createInventory(null, 54, "Edit " + crate.getName(false) + " Crate Winnings");

		for (Winning winning : crate.getWinnings()) {
			if (winning.isCommand()) {
				ItemStack winningItem = winning.getWinningItemStack();
				ItemMeta itemMeta = winningItem.getItemMeta();

				List<String> lore = itemMeta.getLore();

				for (String commands : winning.getCommands()) {
					if (lore == null) lore = new ArrayList<>();

					lore.add("COMMAND:" + commands);
				}

				itemMeta.setLore(lore);
				continue;
			}
			inventory.addItem(winning.getWinningItemStack());
		}

		Bukkit.getScheduler().runTaskLater(crates, new Runnable() {
			@Override
			public void run() {
				player.openInventory(inventory);
			}
		}, 1L);

	}

	public void openCrate(final Player player, String crateName) {
		Crate crate = crates.getConfigHandler().getCrates().get(crateName.toLowerCase());
		if (crate == null) {
			return; // TODO Error handling here
		}

		final Inventory inventory = Bukkit.createInventory(null, 9, "Edit " + crate.getName(false) + " Crate");

		ItemStack itemStack;
		ItemMeta itemMeta;
		List<String> lore;


		/** Rename Crate */

		itemStack = new ItemStack(Material.NAME_TAG);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.WHITE + "Rename Crate");
		lore = new ArrayList<>();
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(1, itemStack);


		/** Edit Crate Winnings */

		itemStack = new ItemStack(Material.DIAMOND);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.WHITE + "Edit Crate Winnings");
		lore = new ArrayList<>();
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(3, itemStack);


		/** Edit Crate Color */

		itemStack = new ItemStack(Material.WOOL, 1, (short) 3);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.WHITE + "Edit Crate Color");
		lore = new ArrayList<>();
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(5, itemStack);


		/** Delete Crate */

		Material material = Material.REDSTONE_TORCH_ON;


		itemStack = new ItemStack(material);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.WHITE + "Delete Crate");
		lore = new ArrayList<>();
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(7, itemStack);

		Bukkit.getScheduler().runTaskLater(crates, new Runnable() {
			@Override
			public void run() {
				player.openInventory(inventory);
			}
		}, 1L);

	}

	public HashMap<String, String> getLastCrateEditing() {
		return lastCrateEditing;
	}
}
