package me.fckml.crates.commands;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.config.LanguageConfig;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.utils.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class CrateCommand extends Command {

    private Crates plugin = Crates.getInstance();

    public CrateCommand() {
        super("crate");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        TaskUtil.runTaskAsync(() -> {
            if (sender instanceof Player && !sender.hasPermission("crate.command.admin")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("claim")) {
                    doClaim((Player) sender);
                    return;
                }
                sender.sendMessage(CC.translate(LanguageConfig.NO_PERMISSION));
                return;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("claim")) {
                    if (sender instanceof Player) {
                        doClaim((Player) sender);
                        return;
                    }

                    sender.sendMessage(CC.translate(LanguageConfig.PLAYERS_ONLY));
                    return;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadPlugin();
                    sender.sendMessage(CC.translate(LanguageConfig.CONFIG_RELOADED));
                    return;
                }

                if (args[0].equalsIgnoreCase("settings") || args[0].equalsIgnoreCase("menu")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(CC.translate(LanguageConfig.PLAYERS_ONLY));
                        return;
                    }

                    plugin.getSettingsHandler().openSettings((Player) sender);
                    return;
                }

            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("setkey")) {
                    String name = args[1];
                    Player player = (Player) sender;
                    FileConfiguration config = plugin.getConfig();

                    if (player.getItemInHand() == null) {
                        player.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_ITEM_NOT_FOUND));
                        return;
                    }

                    if (!config.isSet("CRATES." + name)) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_NOT_FOUND.replace("<name>", args[1])));
                        return;
                    }

                    ItemStack itemStack = player.getItemInHand();

                    config.set("CRATES." + name + ".KEY.ITEM", itemStack.getType().name());

                    if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
                        config.set("CRATES." + name + ".KEY.NAME", "%type% Crate Key");
                    } else {
                        config.set("CRATES." + name + ".KEY.NAME", itemStack.getItemMeta().getDisplayName());
                    }

                    if (itemStack.getItemMeta() == null || !itemStack.getItemMeta().hasEnchants()) {
                        config.set("CRATES." + name + ".KEY.ENCHANTED", true);
                    } else {
                        config.set("CRATES." + name + ".KEY.ENCHANTED", itemStack.getItemMeta().hasEnchants());
                    }

                    if (itemStack.getItemMeta() == null || !itemStack.getItemMeta().hasLore()) {
                        config.set("CRATES." + name + ".KEY.LORE", Arrays.asList("", "EDIT"));
                    } else {
                        config.set("CRATES." + name + ".KEY.LORE", itemStack.getItemMeta().getLore());
                    }

                    plugin.getConfigFile().save();
                    plugin.reloadConfig();

                    player.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_KEY_SET.replace("<name>", name)));
                    return;
                }

                if (args[0].equalsIgnoreCase("hologramitem")) {
                    String name = args[1];
                    Player player = (Player) sender;
                    FileConfiguration config = plugin.getConfig();

                    if (player.getItemInHand() == null) {
                        player.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_ITEM_NOT_FOUND));
                        return;
                    }

                    if (!config.isSet("CRATES." + name)) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_NOT_FOUND.replace("<name>", args[1])));
                        return;
                    }

                    ItemStack itemStack = player.getItemInHand();

                    config.set("CRATES." + name + ".HOLOGRAM-ITEM", itemStack.getType().name());

                    plugin.getConfigFile().save();
                    plugin.reloadConfig();
                    plugin.getConfigHandler().getCrate(name).removeHologram();
                    plugin.getConfigHandler().getCrate(name).loadHolograms(plugin.getConfigHandler().getCrate(name).getLocation(name));
                    
                    player.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_KEY_SET.replace("<name>", name)));
                    return;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    String name = args[1];
                    FileConfiguration config = plugin.getConfig();

                    if (config.isSet("CRATES." + name)) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_ALREADY_EXISTS.replace("<name>", args[1])));
                        return;
                    }

                    // Setup example item
                    config.set("CRATES." + name + ".WINNINGS.1.TYPE", "ITEM");
                    config.set("CRATES." + name + ".WINNINGS.1.ITEM-TYPE", "IRON_SWORD");
                    config.set("CRATES." + name + ".WINNINGS.1.ITEM-DATA", 0);
                    config.set("CRATES." + name + ".WINNINGS.1.PERCENTAGE", 0);
                    config.set("CRATES." + name + ".WINNINGS.1.NAME", "&6&lExample Sword");
                    config.set("CRATES." + name + ".WINNINGS.1.AMOUNT", 1);

                    // Setup key with defaults
                    config.set("CRATES." + name + ".KEY.ITEM", "TRIPWIRE_HOOK");
                    config.set("CRATES." + name + ".KEY.NAME", "%type% Crate Key");
                    config.set("CRATES." + name + ".KEY.ENCHANTED", true);
                    config.set("CRATES." + name + ".KEY.LORE", Arrays.asList("", "EDIT"));

                    config.set("CRATES." + name + ".KNOCKBACK", 0.0);
                    config.set("CRATES." + name + ".BROADCAST", false);
                    config.set("CRATES." + name + ".FIREWORK", false);
                    config.set("CRATES." + name + ".PREVIEW", true);
                    config.set("CRATES." + name + ".BLOCK", "CHEST");
                    config.set("CRATES." + name + ".HOLOGRAM-ITEM", "DIAMOND_SWORD");
                    config.set("CRATES." + name + ".COLOR", "WHITE");

                    plugin.saveConfig();
                    plugin.reloadConfig();

                    plugin.getConfigHandler().getCrates().put(name.toLowerCase(), new Crate(name, plugin, plugin.getConfigHandler()));
                    plugin.getSettingsHandler().setupCratesInventory();

                    sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_CRATE_CREATED.replace("<name>", name)));
                    return;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    String name = args[1];
                    FileConfiguration config = plugin.getConfig();

                    if (!config.isSet("CRATES." + name)) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_NOT_FOUND.replace("<name>", name)));
                        return;
                    }

                    config.set("CRATES." + name, null);
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    plugin.getConfigHandler().getCrates().remove(name.toLowerCase());
                    plugin.getSettingsHandler().setupCratesInventory();

                    sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_DELETED.replace("<name>", name)));
                    return;
                }

                if (args[0].equalsIgnoreCase("crate")) {
                    Player player = (Player) sender;

                    String crateType = args[1];

                    if (plugin.getConfigHandler().getCrates().get(crateType.toLowerCase()) == null) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_NOT_FOUND.replace("<name>", args[1])));
                        return;
                    }

                    plugin.getCrateHandler().giveCrate(player, crateType);
                    return;
                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("rename")) {
                    FileConfiguration config = plugin.getConfig();

                    String oldName = args[1];
                    String newName = args[2];

                    if (!plugin.getConfigHandler().getCrates().containsKey(oldName.toLowerCase())) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_NOT_FOUND).replace("<name>", oldName));
                        return;
                    }

                    Crate crate = plugin.getConfigHandler().getCrates().get(oldName.toLowerCase());

                    if (config.isSet("CRATES." + newName)) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_ALREADY_EXISTS.replace("<name>", newName)));
                        return;
                    }

                    for (String id : plugin.getConfig().getConfigurationSection("CRATES." + crate.getName(false) + ".WINNINGS").getKeys(false)) {
                        String path = "CRATES." + crate.getName(false) + ".WINNINGS." + id;
                        String newPath = "CRATES." + newName + ".WINNINGS." + id;

                        if (config.isSet(path + ".TYPE"))
                            config.set(newPath + ".TYPE", config.getString(path + ".TYPE"));
                        if (config.isSet(path + ".ITEM-TYPE"))
                            config.set(newPath + ".ITEM-TYPE", config.getString(path + ".ITEM-TYPE"));
                        if (config.isSet(path + ".ITEM-DATA"))
                            config.set(newPath + ".ITEM-DATA", config.getInt(path + ".ITEM-DATA"));
                        if (config.isSet(path + ".PERCENTAGE"))
                            config.set(newPath + ".PERCENTAGE", config.getDouble(path + ".PERCENTAGE"));
                        if (config.isSet(path + ".NAME"))
                            config.set(newPath + ".NAME", config.getString(path + ".NAME"));
                        if (config.isSet(path + ".AMOUNT"))
                            config.set(newPath + ".AMOUNT", config.getInt(path + ".AMOUNT"));
                        if (config.isSet(path + ".ENCHANTMENTS"))
                            config.set(newPath + ".ENCHANTMENTS", config.getList(path + ".ENCHANTMENTS"));
                        if (config.isSet(path + ".COMMANDS"))
                            config.set(newPath + ".COMMANDS", config.getList(path + ".COMMANDS"));
                    }

                    config.set("CRATES." + newName + ".KNOCKBACK", config.getDouble("CRATES." + crate.getName(false) + ".KNOCKBACK"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".BLOCK"))
                        config.set("CRATES." + newName + ".Block", config.getString("CRATES." + crate.getName(false) + ".BLOCK"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".COLOR"))
                        config.set("CRATES." + newName + ".COLOR", config.getString("CRATES." + crate.getName(false) + ".COLOR"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".KNOCKBACK"))
                        config.set("CRATES." + newName + ".KNOCKBACK", config.getDouble("CRATES." + crate.getName(false) + ".KNOCKBACK"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".BROADCAST"))
                        config.set("CRATES." + newName + ".BROADCAST", config.getBoolean("CRATES." + crate.getName(false) + ".BROADCAST"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".FIREWORK"))
                        config.set("CRATES." + newName + ".FIREWORK", config.getBoolean("CRATES." + crate.getName(false) + ".FIREWORK"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".PREVIEW"))
                        config.set("CRATES." + newName + ".PREVIEW", config.getBoolean("CRATES." + crate.getName(false) + ".PREVIEW"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".PERMISSION"))
                        config.set("CRATES." + newName + ".PERMISSION", config.getString("CRATES." + crate.getName(false) + ".PERMISSION"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".OPENER"))
                        config.set("CRATES." + newName + ".OPENER", config.getString("CRATES." + crate.getName(false) + ".OPENER"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".COOLDOWN"))
                        config.set("CRATES." + newName + ".COOLDOWN", config.getInt("CRATES." + crate.getName(false) + ".COOLDOWN"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".HOLOGRAM-ITEM"))
                        config.set("CRATES." + newName + ".HOLOGRAM-ITEM", config.getInt("CRATES." + crate.getName(false) + ".HOLOGRAM-ITEM"));

                    // Clone the crate key
                    if (config.isSet("CRATES." + crate.getName(false) + ".KEY.ITEM"))
                        config.set("CRATES." + newName + ".KEY.ITEM", config.getString("CRATES." + crate.getName(false) + ".KEY.ITEM"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".KEY.NAME"))
                        config.set("CRATES." + newName + ".KEY.NAME", config.getString("CRATES." + crate.getName(false) + ".KEY.NAME"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".KEY.ENCHANTED"))
                        config.set("CRATES." + newName + ".KEY.ENCHANTED", config.getBoolean("CRATES." + crate.getName(false) + ".KEY.ENCHANTED"));
                    if (config.isSet("CRATES." + crate.getName(false) + ".KEY.LORE"))
                        config.set("CRATES." + newName + ".KEY.LORE", config.getStringList("CRATES." + crate.getName(false) + ".KEY.LORE"));

                    // Rename data fields
                    FileConfiguration dataConfig = plugin.getDataFile();
                    if (dataConfig.isSet("Crate Locations." + crate.getName(false).toLowerCase())) {
                        dataConfig.set("Crate Locations." + newName.toLowerCase(), dataConfig.getStringList("Crate Locations." + crate.getName(false).toLowerCase()));
                        dataConfig.set("Crate Locations." + crate.getName(false).toLowerCase(), null);
                        plugin.getDataFile().save();
                    }

                    config.set("CRATES." + crate.getName(false), null);
                    plugin.saveConfig();
                    plugin.reloadConfig();

                    plugin.getConfigHandler().getCrates().remove(oldName.toLowerCase());
                    plugin.getConfigHandler().getCrates().put(newName.toLowerCase(), new Crate(newName, plugin, plugin.getConfigHandler()));
                    plugin.getSettingsHandler().setupCratesInventory();

                    sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_RENAMED.replace("<oldName>", oldName).replace("<newName>", newName)));
                    return;
                }
            }

            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("key")) {
                    Integer amount = 1;

                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (Exception ignored) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_INVALID_AMOUNT));
                        return;
                    }

                    OfflinePlayer offlinePlayer = null;

                    if (!args[1].equalsIgnoreCase("all")) {
                        offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                        if (offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())) {
                            sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_PLAYER_NOT_FOUND.replace("<name>", args[1])));
                            return;
                        }
                    }

                    String crateType = args[2];

                    if (crateType != null) {
                        if (plugin.getConfigHandler().getCrates().get(crateType.toLowerCase()) == null) {
                            sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_NOT_FOUND.replace("<name>", crateType)));
                            return;
                        }

                        if (offlinePlayer == null) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                plugin.getCrateHandler().giveCrateKey(p, crateType, amount);
                            }
                        } else {
                            plugin.getCrateHandler().giveCrateKey(offlinePlayer, crateType, amount);
                        }
                    } else {
                        if (offlinePlayer == null) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                plugin.getCrateHandler().giveCrateKey(p);
                            }
                        } else {
                            plugin.getCrateHandler().giveCrateKey(offlinePlayer);
                        }
                    }

                    if (offlinePlayer == null) {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_KEY_GIVE_ALL));
                    } else {
                        sender.sendMessage(CC.translate(LanguageConfig.CRATE_COMMAND_KEY_GIVE_PLAYER.replace("<name>", offlinePlayer.getName())));
                    }
                    return;
                }
            }

            for (String configLines : LanguageConfig.CRATE_COMMAND_HELP) {
                sender.sendMessage(CC.translate(configLines));
            }
        });
        return false;
    }


    private void doClaim(Player player) {
        if (!plugin.getCrateHandler().hasPendingKeys(player.getUniqueId())) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You currently don't have any keys to claim");
            return;
        }
        Integer size = plugin.getCrateHandler().getPendingKey(player.getUniqueId()).size();
        if (size < 9)
            size = 9;
        else if (size <= 18)
            size = 18;
        else if (size <= 27)
            size = 27;
        else if (size <= 36)
            size = 36;
        else if (size <= 45)
            size = 45;
        else
            size = 54;
        Inventory inventory = Bukkit.createInventory(null, size, "Claim Crate Keys");
        Integer i = 0;
        for (Map.Entry<String, Integer> map : plugin.getCrateHandler().getPendingKey(player.getUniqueId()).entrySet()) {
            String crateName = map.getKey();
            Crate crate = plugin.getConfigHandler().getCrates().get(crateName.toLowerCase());
            if (crate == null)
                return; // Crate must have been removed?
            ItemStack keyItem = crate.getKey().getKeyItem(1);
            if (map.getValue() > 1) {
                ItemMeta itemMeta = keyItem.getItemMeta();
                itemMeta.setDisplayName(itemMeta.getDisplayName() + " x" + map.getValue());
                keyItem.setItemMeta(itemMeta);
            }
            inventory.setItem(i, keyItem);
            i++;
        }
        player.openInventory(inventory);
    }
}
