package me.fckml.crates.handlers;

import me.fckml.crates.Crates;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.opener.BasicGUIOpener;
import me.fckml.crates.opener.NoGUIOpener;
import me.fckml.crates.opener.Opener;

import java.util.HashMap;

/**
 * Public handler for CratesPlus to be able to modify the way crates open.
 */
public class OpenHandler {

	private Crates crates;
	private HashMap<String, Opener> registered = new HashMap<>();
	private String defaultOpener;

	public OpenHandler(Crates crates) {
		this.crates = crates;
		registerDefaults();
	}

	private void registerDefaults() {
		registerOpener(new BasicGUIOpener(crates));
		registerOpener(new NoGUIOpener(crates));
		defaultOpener = crates.getConfigHandler().getDefaultOpener();
	}

	public void registerOpener(Opener opener) {
		if (registered.containsKey(opener.getName())) {
			getCratesPlugin().getLogger().warning("An opener with the name \"" + opener.getName() + "\" already exists and will not be registered");
			return;
		}
		try {
			opener.doSetup();
			registered.put(opener.getName(), opener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCratesPlusVersion() {
		return getCratesPlugin().getDescription().getVersion();
	}

	public Opener getOpener(Crate crate) {
		if (registered.containsKey(crate.getOpener()))
			return registered.get(crate.getOpener());
		return getDefaultOpener();
	}

	public Opener getDefaultOpener() {
		if (registered.containsKey(defaultOpener))
			return registered.get(defaultOpener);
		return registered.get("NoGUI");
	}

	public void setDefaultOpener(String defaultOpener) {
		this.defaultOpener = defaultOpener;
		crates.getConfig().set("Default Opener", defaultOpener);
		crates.saveConfig();
	}

	public Crates getCratesPlugin() {
		return crates;
	}

	public boolean openerExist(String name) {
		return registered.containsKey(name);
	}

	public HashMap<String, Opener> getRegistered() {
		return registered;
	}

}
