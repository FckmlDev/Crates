package me.fckml.crates.handlers;

import me.fckml.crates.Crates;
import me.fckml.crates.managers.Crate;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class ConfigHandler {

	private Integer defaultCooldown = 5;
	private Integer crateGUITime = 10;
	private String defaultOpener = "NoGUI";
	private List<String> defaultHologramText;
	private HashMap<String, List<String>> holograms = new HashMap<>();
	private HashMap<String, Crate> crates = new HashMap<>();
	private boolean disableKeySwapping = false;
	private boolean debugMode = false;

	public ConfigHandler(FileConfiguration config, Crates crates) {
		if (config.isSet("DISABLE-KEY-DROPPING")) disableKeySwapping = config.getBoolean("DISABLE-KEY-DROPPING");
		if (config.isSet("COOLDOWN")) setDefaultCooldown(config.getInt("COOLDOWN"));

		if (config.isSet("CRATES")) {
			for (String crate : config.getConfigurationSection("CRATES").getKeys(false)) {
				addCrate(crate.toLowerCase(), new Crate(crate, crates, this));
			}
		}

		defaultHologramText = config.getStringList("HOLOGRAM-TEXT");

		for (String crateLowerName : this.crates.keySet()) {
			Crate crate = this.crates.get(crateLowerName);
			List<String> crateSpecificHologram = config.getStringList("CRATES." + crate.getName() + ".HOLOGRAM-TEXT");
			holograms.put(crate.getName().toLowerCase(), (config.isSet("CRATES." + crate.getName() + ".HOLOGRAM-TEXT")) ? crateSpecificHologram : defaultHologramText);
		}
	}

	public Integer getDefaultCooldown() {
		return defaultCooldown;
	}

	public void setDefaultCooldown(int defaultCooldown) {
		this.defaultCooldown = defaultCooldown;
	}

	public void setCrates(HashMap<String, Crate> crates) {
		this.crates = crates;
	}

	public void addCrate(String name, Crate crate) {
		this.crates.put(name, crate);
	}

	public Crate getCrate(String name) {
		if (this.crates.containsKey(name)) return this.crates.get(name);
		return null;
	}

	public HashMap<String, Crate> getCrates() {
		return this.crates;
	}

	public List<String> getHolograms(String crateType) {
		return this.holograms.get(crateType.toLowerCase());
	}

	@Deprecated
	public Integer getCrateGUITime() {
		return crateGUITime;
	}

	public String getDefaultOpener() {
		return defaultOpener;
	}

	public boolean isDisableKeySwapping() {
		return disableKeySwapping;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

}
