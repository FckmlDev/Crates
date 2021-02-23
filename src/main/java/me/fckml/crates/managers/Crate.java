package me.fckml.crates.managers;

import com.battlehcf.chatcolor.CC;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.fckml.crates.Crates;
import me.fckml.crates.handlers.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Crate {

	private Crates crates;
	private String name;
	private String slug;
	private ChatColor color = ChatColor.WHITE;
	private Material block = Material.CHEST;
	private boolean firework = false;
	private boolean broadcast = false;
	private boolean preview = true;
	private boolean hidePercentages = false;
	private double knockback = 0.0;
	private ArrayList<Winning> winnings = new ArrayList<>();
	private double totalPercentage = 0;
	private Key key;
	private HashMap<String, Location> locations = new HashMap<>();
	private String permission = null;
	private String opener = null;
	private Integer cooldown = null;
	private Hologram hologram;

	public Crate(String name, Crates crates, ConfigHandler configHandler) {
		this.crates = crates;
		this.name = name;
		this.slug = name.toLowerCase();

		if (crates.getConfig().isSet("CRATES." + name + ".COLOR"))
			this.color = ChatColor.valueOf(crates.getConfig().getString("CRATES." + name + ".COLOR").toUpperCase());
		if (crates.getConfig().isSet("CRATES." + name + ".BLOCK"))
			this.block = Material.valueOf(crates.getConfig().getString("CRATES." + name + ".BLOCK").toUpperCase());
		if (crates.getConfig().isSet("CRATES." + name + ".FIREWORK"))
			this.firework = crates.getConfig().getBoolean("CRATES." + name + ".FIREWORK");
		if (crates.getConfig().isSet("CRATES." + name + ".BROADCAST"))
			this.broadcast = crates.getConfig().getBoolean("CRATES." + name + ".BROADCAST");
		if (crates.getConfig().isSet("CRATES." + name + ".PREVIEW"))
			this.preview = crates.getConfig().getBoolean("CRATES." + name + ".PREVIEW");
		if (crates.getConfig().isSet("CRATES." + name + ".KNOCKBACK"))
			this.knockback = crates.getConfig().getDouble("CRATES." + name + ".KNOCKBACK");
		if (crates.getConfig().isSet("CRATES." + name + ".PERMISSION"))
			this.permission = crates.getConfig().getString("CRATES." + name + ".PERMISSION");
		if (crates.getConfig().isSet("CRATES." + name + ".OPENER"))
			this.opener = crates.getConfig().getString("CRATES." + name + ".OPENER");
		if (crates.getConfig().isSet("CRATES." + name + ".COOLDOWN"))
			this.cooldown = crates.getConfig().getInt("CRATES." + name + ".COOLDOWN");

		if (!crates.getConfig().isSet("CRATES." + name + ".KEY") || !crates.getConfig().isSet("CRATES." + name + ".KEY.ITEM") || !crates.getConfig().isSet("CRATES." + name + ".KEY.NAME") || !crates.getConfig().isSet("CRATES." + name + ".KEY.ENCHANTED"))
			return;

		this.key = new Key(name, Material.valueOf(crates.getConfig().getString("CRATES." + name + ".KEY.ITEM")), crates.getConfig().getString("CRATES." + name + ".KEY.NAME").replaceAll("%type%", getName(true)), crates.getConfig().getBoolean("CRATES." + name + ".KEY.ENCHANTED"), crates, crates.getConfig().getStringList("CRATES." + name + ".KEY.LORE"));

		if (!crates.getConfig().isSet("CRATES." + name + ".WINNINGS"))
			return;

		for (String id : crates.getConfig().getConfigurationSection("CRATES." + name + ".WINNINGS").getKeys(false)) {
			String path = "CRATES." + name + ".WINNINGS." + id;
			Winning winning = new Winning(this, path, crates, configHandler);
			totalPercentage = totalPercentage + winning.getPercentage();
			winnings.add(winning);
		}
	}

	public String getName() {
		return getName(false);
	}

	public String getName(boolean includecolor) {
		if (includecolor) return getColor() + this.name;
		return this.name;
	}

	public String getSlug() {
		return slug;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public Material getBlock() {
		return this.block;
	}

	public boolean isFirework() {
		return this.firework;
	}

	public boolean isBroadcast() {
		return this.broadcast;
	}

	public boolean isPreview() {
		return preview;
	}

	public boolean isHidePercentages() {
		return hidePercentages;
	}

	public double getKnockback() {
		return this.knockback;
	}

	public void reloadWinnings() {
		crates.reloadConfig();
		winnings.clear();
		for (String id : crates.getConfig().getConfigurationSection("CRATES." + name + ".WINNINGS").getKeys(false)) {
			String path = "CRATES." + name + ".WINNINGS." + id;
			Winning winning = new Winning(this, path, crates, crates.getConfigHandler());
			if (winning.isValid())
				winnings.add(winning);
		}
	}

	public List<Winning> getWinnings() {
		return winnings;
	}

	public void clearWinnings() {
		winnings.clear();
	}

	public void addWinning(Winning winning) {
		winnings.add(winning);
	}

	public double getTotalPercentage() {
		return totalPercentage;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setColor(String color) {
		this.color = ChatColor.valueOf(color);
		String path = "CRATES." + name + ".COLOR";
		crates.getConfig().set(path, color);
		crates.saveConfig();
		crates.reloadPlugin();
	}

	public HashMap<String, Location> getLocations() {
		return locations;
	}

	public void addLocation(String string, Location location) {
		locations.put(string, location);
	}

	public Location getLocation(String key) {
		return locations.get(key);
	}

	public Location removeLocation(String key) {
		return locations.remove(key);
	}

	public String getPermission() {
		return permission;
	}

	public void addToConfig(Location location) {
		List<String> locations = new ArrayList<>();
		if (crates.getDataFile().isSet("Crate Locations." + this.getName(false).toLowerCase()))
			locations = crates.getDataFile().getStringList("Crate Locations." + this.getName(false).toLowerCase());
		locations.add(location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ());
		crates.getDataFile().set("Crate Locations." + this.getName(false).toLowerCase(), locations);
		crates.getDataFile().save();
	}

	public void removeFromConfig(Location location) {
		List<String> locations = new ArrayList<>();
		if (crates.getDataFile().isSet("Crate Locations." + this.getName(false).toLowerCase()))
			locations = crates.getDataFile().getStringList("Crate Locations." + this.getName(false).toLowerCase());
		if (locations.contains(location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ()))
			locations.remove(location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ());
		crates.getDataFile().set("Crate Locations." + this.getName(false).toLowerCase(), locations);
		crates.getDataFile().save();
	}

	public void removeHologram() {
		if (hologram != null) {
			hologram.clearLines();
			hologram.delete();
			hologram = null;
		}
	}

	public void loadHolograms(Location location) {
		// Do holograms
		if (crates.getConfigHandler().getHolograms(this.slug) == null || crates.getConfigHandler().getHolograms(this.slug).isEmpty())
			return;

		ArrayList<String> list = new ArrayList<>();
		for (String string : crates.getConfigHandler().getHolograms(this.slug)) list.add(string.replace("%crate%", getName(true)));

		if (hologram != null) {
			hologram.clearLines();
			hologram.delete();
			hologram = null;
		}

		hologram = HologramsAPI.createHologram(Crates.getInstance(), location.add(0, (0.5 * list.size()), 0));

		hologram.appendItemLine(new ItemStack(Material.getMaterial(Crates.getInstance().getConfig().getString("CRATES." + this.getName() + ".HOLOGRAM-ITEM"))));

		for (String listTotal : list) {
			hologram.appendTextLine(CC.translate(listTotal));
		}

		hologram.getVisibilityManager().setVisibleByDefault(true);
	}

	public String getOpener() {
		return opener;
	}

	public Integer getCooldown() {
		if (cooldown == null || cooldown < 0)
			return crates.getConfigHandler().getDefaultCooldown();
		return cooldown;
	}

	public void setOpener(String opener) {
		this.opener = opener;
		crates.getConfig().set("CRATES." + getName(false) + ".OPENER", opener);
		crates.saveConfig();
	}

	public boolean containsCommandItem() {
		for (Winning winning : getWinnings()) {
			if (winning.isCommand())
				return true;
		}
		return false;
	}

}
