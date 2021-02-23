package me.fckml.crates.listeners;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.config.LanguageConfig;
import me.fckml.crates.listeners.events.PlayerInputEvent;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.utils.ReflectionUtil;
import me.fckml.crates.utils.SignInputHandler;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SettingsListener implements Listener {
	private Crates crates;
	private HashMap<UUID, String> renaming = new HashMap<>();

	public SettingsListener(Crates crates) {
		this.crates = crates;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		if (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains("Crate Winnings")) {
			String crateName = ChatColor.stripColor(event.getInventory().getTitle().replaceAll("Edit ", "").replaceAll(" Crate Winnings", ""));
			Crate crate = crates.getConfigHandler().getCrates().get(crateName.toLowerCase());
			if (crate == null) {
				return;
			}

			crates.getConfig().set("CRATES." + crateName + ".WINNINGS", null);
			crates.saveConfig();
			for (ItemStack itemStack : event.getInventory().getContents()) {
				if (itemStack == null)
					continue;
				int id = getFreeID(crateName, 1);

				String type = "ITEM";
				double percentage = 10;
				String itemtype = itemStack.getType().toString().toUpperCase();
				Byte itemData = itemStack.getData().getData();
				String name = "NONE";
				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
					name = itemStack.getItemMeta().getDisplayName().replaceAll("§", "&");
				Integer amount = itemStack.getAmount();
				List<String> enchantments = new ArrayList<>();

				if (itemStack.getEnchantments() != null && !itemStack.getEnchantments().isEmpty()) {
					for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
						Enchantment enchantment = entry.getKey();
						Integer level = entry.getValue();
						enchantments.add(enchantment.getName().toUpperCase() + "-" + level);
					}
				}

				EntityType entityType = null;
				if (itemStack.getType() == Material.MONSTER_EGG) {
					entityType = crates.getVersion_util().getEntityTypeFromItemStack(itemStack);
				}


				List<String> lore = new ArrayList<>();
				List<String> commands = new ArrayList<>();

				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
					for (String lores : itemStack.getItemMeta().getLore()) {
						if (lores.contains("TYPE:")) {
							type = lores.replace("TYPE:", "");
						} else if (lores.contains("COMMAND:")) {
							commands.add(lores.replace("COMMAND:", ""));
						} else if (lores.contains("CHANCE:")) {
							percentage = Double.parseDouble(lores.replace("CHANCE:", ""));
						} else {
							lore.add(lores);
						}
					}
				}

				// Save to config and creating winning instance
				FileConfiguration config = crates.getConfig();
				String path = "CRATES." + crateName + ".WINNINGS." + id;
				config.set(path + ".TYPE", type);
				config.set(path + ".ITEM-TYPE", itemtype);
				config.set(path + ".ITEM-DATA", itemData);
				config.set(path + ".NAME", name);
				config.set(path + ".AMOUNT", amount);
				config.set(path + ".ENCHANTMENTS", enchantments);
				config.set(path + ".LORE", lore);
				config.set(path + ".PERCENTAGE", percentage);
				if (!commands.isEmpty() && type.equalsIgnoreCase("command")) {
					config.set(path + ".COMMANDS", commands);
				}
				if (entityType != null)
					config.set(path + ".ENTITY-TYPE", entityType.toString());
			}

			crates.saveConfig();
			crates.reloadPlugin();

			crate.reloadWinnings();
			if (event.getPlayer() instanceof Player) {
				Player player = (Player) event.getPlayer();
				player.sendMessage(CC.translate(LanguageConfig.CRATE_WINNINGS_UPDATED));
			}
		}
	}

	private int getFreeID(String crate, int check) {
		if (crates.getConfig().isSet("CRATES." + crate + ".WINNINGS." + check))
			return getFreeID(crate, check + 1);
		return check;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();

		if (event.getInventory().getTitle() == null)
			return;


		if (event.getInventory().getTitle().contains("Crates Settings")) {

			if (itemStack == null || itemStack.getType() == Material.AIR || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
				event.setCancelled(true);
				return;
			}

			if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Edit Crates")) {
				event.setCancelled(true);
				player.closeInventory();
				crates.getSettingsHandler().openCrates(player);
				return;
			}

			if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Reload Config")) {
				event.setCancelled(true);
				player.closeInventory();
				crates.reloadPlugin();
				player.sendMessage(CC.translate(LanguageConfig.CONFIG_RELOADED));
				return;
			}

			if (event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.RED + "")) {
				event.setCancelled(true);
				player.closeInventory();
				player.sendMessage(ChatColor.RED + "Coming Soon");
			}
		} else if (event.getInventory().getTitle().contains("Crates")) {

			if (itemStack == null || itemStack.getType() == Material.AIR || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
				event.setCancelled(true);
				return;
			}

			if (event.getCurrentItem().getType() == Material.CHEST) {
				player.closeInventory();
				crates.getSettingsHandler().openCrate(player, ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
			}

		} else if (event.getInventory().getTitle().contains("Edit Crate Color")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WOOL) {
				player.closeInventory();
				ChatColor color = ChatColor.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_")));
				if (color != null) {
					String lastCrate = crates.getSettingsHandler().getLastCrateEditing().get(player.getUniqueId().toString());
					if (lastCrate == null)
						return;
					Crate crate = crates.getConfigHandler().getCrates().get(lastCrate.toLowerCase());
					crate.setColor(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_")));
					player.sendMessage(CC.translate(LanguageConfig.CRATE_COLOR_EDITED));
					crate.removeHologram();
					crate.loadHolograms(crate.getLocation(lastCrate.toLowerCase()));
				}
			}
		} else if (event.getInventory().getTitle().contains("Edit ") && !event.getInventory().getTitle().contains("Winnings") && !event.getInventory().getTitle().contains("Color")) {
			if (itemStack == null || itemStack.getType() == Material.AIR || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
				event.setCancelled(true);
				return;
			}

			if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Edit Crate Winnings")) {
				event.setCancelled(true);
				String name = ChatColor.stripColor(event.getInventory().getTitle().replaceAll("Edit ", "").replaceAll(" Crate", ""));
				crates.getSettingsHandler().openCrateWinnings(player, name);
			} else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Delete")) {
				event.setCancelled(true);
				String name = ChatColor.stripColor(event.getInventory().getTitle().replaceAll("Edit ", "").replaceAll(" Crate", ""));
				crates.getConfig().set("CRATES." + name, null);
				crates.saveConfig();
				crates.reloadConfig();
				crates.getConfigHandler().getCrates().remove(name.toLowerCase());
				crates.getSettingsHandler().setupCratesInventory();
				player.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_DELETED.replace("<name>", name)));
			} else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Rename Crate")) {
				// Let's handle renaming using sign packets ;D
				String name = ChatColor.stripColor(event.getInventory().getTitle().replaceAll("Edit ", "").replaceAll(" Crate", ""));
				renaming.put(player.getUniqueId(), name);
				try {
					Constructor signConstructor = ReflectionUtil.getNMSClass("PacketPlayOutOpenSignEditor").getConstructor(ReflectionUtil.getNMSClass("BlockPosition"));
					Object packet = signConstructor.newInstance(ReflectionUtil.getBlockPosition(player));
					SignInputHandler.injectNetty(player);
					ReflectionUtil.sendPacket(player, packet);
				} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
					e.printStackTrace();
					renaming.remove(player.getUniqueId());
				}
				event.setCancelled(true);
			} else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Edit Crate Color")) {
				event.setCancelled(true);
				Inventory inventory = Bukkit.createInventory(null, 18, "Edit Crate Color");

				ItemStack aqua = new ItemStack(Material.WOOL, 1, (short) 3);
				ItemMeta aquaMeta = aqua.getItemMeta();
				aquaMeta.setDisplayName(ChatColor.AQUA + "Aqua");
				aqua.setItemMeta(aquaMeta);
				inventory.addItem(aqua);

				ItemStack black = new ItemStack(Material.WOOL, 1, (short) 15);
				ItemMeta blackMeta = black.getItemMeta();
				blackMeta.setDisplayName(ChatColor.BLACK + "Black");
				black.setItemMeta(blackMeta);
				inventory.addItem(black);

				ItemStack blue = new ItemStack(Material.WOOL, 1, (short) 9);
				ItemMeta blueMeta = blue.getItemMeta();
				blueMeta.setDisplayName(ChatColor.BLUE + "Blue");
				blue.setItemMeta(blueMeta);
				inventory.addItem(blue);

				ItemStack darkAqua = new ItemStack(Material.WOOL, 1, (short) 3);
				ItemMeta darkAquaMeta = darkAqua.getItemMeta();
				darkAquaMeta.setDisplayName(ChatColor.DARK_AQUA + "Dark Aqua");
				darkAqua.setItemMeta(darkAquaMeta);
				inventory.addItem(darkAqua);

				ItemStack darkBlue = new ItemStack(Material.WOOL, 1, (short) 11);
				ItemMeta darkBlueMeta = darkBlue.getItemMeta();
				darkBlueMeta.setDisplayName(ChatColor.DARK_BLUE + "Dark Blue");
				darkBlue.setItemMeta(darkBlueMeta);
				inventory.addItem(darkBlue);

				ItemStack darkGray = new ItemStack(Material.WOOL, 1, (short) 7);
				ItemMeta darkGrayMeta = darkGray.getItemMeta();
				darkGrayMeta.setDisplayName(ChatColor.DARK_GRAY + "Dark Gray");
				darkGray.setItemMeta(darkGrayMeta);
				inventory.addItem(darkGray);

				ItemStack darkGreen = new ItemStack(Material.WOOL, 1, (short) 13);
				ItemMeta darkGreenMeta = darkGreen.getItemMeta();
				darkGreenMeta.setDisplayName(ChatColor.DARK_GREEN + "Dark Green");
				darkGreen.setItemMeta(darkGreenMeta);
				inventory.addItem(darkGreen);

				ItemStack darkPurple = new ItemStack(Material.WOOL, 1, (short) 10);
				ItemMeta darkPurpleMeta = darkPurple.getItemMeta();
				darkPurpleMeta.setDisplayName(ChatColor.DARK_PURPLE + "Dark Purple");
				darkPurple.setItemMeta(darkPurpleMeta);
				inventory.addItem(darkPurple);

				ItemStack darkRed = new ItemStack(Material.WOOL, 1, (short) 14);
				ItemMeta darkRedMeta = darkRed.getItemMeta();
				darkRedMeta.setDisplayName(ChatColor.DARK_RED + "Dark Red");
				darkRed.setItemMeta(darkRedMeta);
				inventory.addItem(darkRed);

				ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8);
				ItemMeta glassMeta = glass.getItemMeta();
				glassMeta.setDisplayName(ChatColor.GOLD + "");
				glass.setItemMeta(glassMeta);
				inventory.addItem(glass);

				ItemStack gold = new ItemStack(Material.WOOL, 1, (short) 1);
				ItemMeta goldMeta = gold.getItemMeta();
				goldMeta.setDisplayName(ChatColor.GOLD + "Gold");
				gold.setItemMeta(goldMeta);
				inventory.addItem(gold);

				ItemStack gray = new ItemStack(Material.WOOL, 1, (short) 8);
				ItemMeta grayMeta = gray.getItemMeta();
				grayMeta.setDisplayName(ChatColor.GRAY + "Gray");
				gray.setItemMeta(grayMeta);
				inventory.addItem(gray);

				ItemStack green = new ItemStack(Material.WOOL, 1, (short) 5);
				ItemMeta greenMeta = gray.getItemMeta();
				greenMeta.setDisplayName(ChatColor.GREEN + "Green");
				green.setItemMeta(greenMeta);
				inventory.addItem(green);

				ItemStack lightPurple = new ItemStack(Material.WOOL, 1, (short) 2);
				ItemMeta lightPurpleMeta = lightPurple.getItemMeta();
				lightPurpleMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Light Purple");
				lightPurple.setItemMeta(lightPurpleMeta);
				inventory.addItem(lightPurple);

				ItemStack red = new ItemStack(Material.WOOL, 1, (short) 14);
				ItemMeta redMeta = red.getItemMeta();
				redMeta.setDisplayName(ChatColor.RED + "Red");
				red.setItemMeta(redMeta);
				inventory.addItem(red);

				ItemStack white = new ItemStack(Material.WOOL, 1, (short) 0);
				ItemMeta whiteMeta = white.getItemMeta();
				whiteMeta.setDisplayName(ChatColor.WHITE + "White");
				white.setItemMeta(whiteMeta);
				inventory.addItem(white);

				ItemStack yellow = new ItemStack(Material.WOOL, 1, (short) 4);
				ItemMeta yellowMeta = yellow.getItemMeta();
				yellowMeta.setDisplayName(ChatColor.YELLOW + "Yellow");
				yellow.setItemMeta(yellowMeta);
				inventory.addItem(yellow);

				glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8);
				glassMeta = glass.getItemMeta();
				glassMeta.setDisplayName(ChatColor.GOLD + "");
				glass.setItemMeta(glassMeta);
				inventory.setItem(17, glass);

				String name = ChatColor.stripColor(event.getInventory().getTitle().replaceAll("Edit ", "").replaceAll(" Crate", ""));
				crates.getSettingsHandler().getLastCrateEditing().put(player.getUniqueId().toString(), name);

				player.openInventory(inventory);
			}

		}

	}

	@EventHandler
	public void onPlayerInput(PlayerInputEvent event) {
		if (renaming.containsKey(event.getPlayer().getUniqueId())) {
			String name = renaming.get(event.getPlayer().getUniqueId());
			renaming.remove(event.getPlayer().getUniqueId());
			StringBuilder newName = new StringBuilder();
			for (IChatBaseComponent line : event.getLines()) {
				newName.append(line.c());
			}
			if (!name.isEmpty() && (newName.length() > 0))
				Bukkit.getScheduler().runTask(crates, () -> Bukkit.dispatchCommand(event.getPlayer(), "crate rename " + name + " " + newName));

			crates.getSettingsHandler().openCrate(event.getPlayer(), newName.toString());
		} else if (crates.isCreating(event.getPlayer().getUniqueId())) {
			crates.removeCreating(event.getPlayer().getUniqueId());
			StringBuilder name = new StringBuilder();
			for (IChatBaseComponent line : event.getLines()) {
				name.append(line.c());
			}
			if (name.length() > 0)
				Bukkit.getScheduler().runTask(crates, () -> Bukkit.dispatchCommand(event.getPlayer(), "crate create " + name));
		}
	}

}
