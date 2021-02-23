package me.fckml.crates;

import lombok.Getter;
import lombok.Setter;
import me.fckml.crates.commands.CrateCommand;
import me.fckml.crates.config.LanguageConfig;
import me.fckml.crates.handlers.ConfigHandler;
import me.fckml.crates.handlers.CrateHandler;
import me.fckml.crates.handlers.OpenHandler;
import me.fckml.crates.handlers.SettingsHandler;
import me.fckml.crates.listeners.*;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.utils.ConfigFile;
import me.fckml.crates.utils.Version_Util;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Crates extends JavaPlugin {

    @Getter
    private static Crates instance;

    private ConfigFile configFile;
    private ConfigFile languageFile;
    private ConfigFile dataFile;

    private ConfigHandler configHandler;
    private CrateHandler crateHandler;
    private SettingsHandler settingsHandler;
    private Version_Util version_util;

    @Getter
    private static OpenHandler openHandler;

    private final ArrayList<UUID> creatingCrate = new ArrayList<>();

    public void onEnable() {
        instance = this;

        configFile = new ConfigFile("config.yml");
        languageFile = new ConfigFile("language.yml");
        dataFile = new ConfigFile("data.yml");

        new LanguageConfig();

        version_util = new Version_Util(this);

        configHandler = new ConfigHandler(getConfig(), this);
        crateHandler = new CrateHandler(this);

        Arrays.asList(new BlockListener(this),
                new PlayerJoinListener(this),
                new InventoryInteractListener(this),
                new SettingsListener(this),
                new PlayerInteractListener(this))
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));

        openHandler = new OpenHandler(this);

        settingsHandler = new SettingsHandler(this);

        loadMetaData();

        MinecraftServer.getServer().server.getCommandMap().register(this.getName(), new CrateCommand());
    }

    public void reloadPlugin() {
        reloadConfig();

        configHandler = new ConfigHandler(getConfig(), this);

        settingsHandler = new SettingsHandler(this);
    }

    private void loadMetaData() {
        if (!dataFile.isSet("Crate Locations")) return;

        for (String name : dataFile.getConfigurationSection("Crate Locations").getKeys(false)) {
            final Crate crate = configHandler.getCrate(name.toLowerCase());

            if (crate == null) continue;

            String path = "Crate Locations." + name;

            List<String> locations = dataFile.getStringList(path);

            for (String location : locations) {
                List<String> strings = Arrays.asList(location.split("\\|"));
                if (strings.size() < 4) continue;

                if (strings.size() > 4) {
                    for (int i = 0; i < strings.size(); i++) {
                        if (strings.get(i).isEmpty() || strings.get(i).equals("")) {
                            strings.remove(i);
                        }
                    }
                }

                Location locationObj = new Location(Bukkit.getWorld(strings.get(0)), Double.parseDouble(strings.get(1)), Double.parseDouble(strings.get(2)), Double.parseDouble(strings.get(3)));

                Block block = locationObj.getBlock();

                if (block == null || block.getType().equals(Material.AIR)) {
                    getLogger().warning("No block found at " + location + " removing from data.yml");
                    crate.removeFromConfig(locationObj);
                    continue;
                }

                Location location1 = locationObj.getBlock().getLocation().add(0.5, 0.5, 0.5);
                crate.loadHolograms(location1);
                final Crates crates = this;

                block.setMetadata("CrateType", new MetadataValue() {
                    @Override
                    public Object value() {
                        return crate.getName(false);
                    }

                    @Override
                    public int asInt() {
                        return 0;
                    }

                    @Override
                    public float asFloat() {
                        return 0;
                    }

                    @Override
                    public double asDouble() {
                        return 0;
                    }

                    @Override
                    public long asLong() {
                        return 0;
                    }

                    @Override
                    public short asShort() {
                        return 0;
                    }

                    @Override
                    public byte asByte() {
                        return 0;
                    }

                    @Override
                    public boolean asBoolean() {
                        return false;
                    }

                    @Override
                    public String asString() {
                        return value().toString();
                    }

                    @Override
                    public Plugin getOwningPlugin() {
                        return crates;
                    }

                    @Override
                    public void invalidate() {

                    }
                });
            }
        }
    }

    @Override
    public FileConfiguration getConfig() {
        return configFile;
    }

    public boolean isCreating(UUID uuid) {
        return creatingCrate.contains(uuid);
    }

    public void removeCreating(UUID uuid) {
        creatingCrate.remove(uuid);
    }
}
