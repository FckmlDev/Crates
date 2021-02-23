package me.fckml.crates.utils;

import com.battlehcf.chatcolor.CC;
import lombok.Getter;
import me.fckml.crates.Crates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigFile extends YamlConfiguration {

    @Getter
    private final File file;

    public ConfigFile(String name) throws RuntimeException {
        this.file = new File(Crates.getInstance().getDataFolder(), name);

        if (!this.file.exists()) {
            Crates.getInstance().saveResource(name, false);
        }

        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException();
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getSection(String name) {
        return super.getConfigurationSection(name);
    }

    @Override
    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    @Override
    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    @Override
    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    @Override
    public String getString(String path) {
        return CC.translate(super.getString(path, ""));
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }
}
