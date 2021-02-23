package me.fckml.crates.managers;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.handlers.ConfigHandler;
import me.fckml.crates.utils.EnchantmentUtil;
import me.fckml.crates.utils.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Winning {
    private Crates crates;
    private Crate crate;
    private boolean valid = false;
    private boolean command = false;
    private double percentage = 0;
    private ItemStack previewItemStack;
    private ItemStack winningItemStack;
    private List<String> commands = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    private String entityType = "";
    private int slot = 0;
    public Winning(Crate crate, String path, Crates crates, ConfigHandler configHandler) {
        this.crates = crates;
        this.crate = crate;

        if (configHandler.isDebugMode()) {
            crates.getLogger().info("Loading data for \"" + path + "\"");
        }

        FileConfiguration config = crates.getConfig();
        if (!config.isSet(path))
            return;

        if (!config.isSet(path + ".TYPE"))
            return;
        String type = config.getString(path + ".TYPE");
        ItemStack itemStack;
        
        if (type.equalsIgnoreCase("item") || type.equalsIgnoreCase("block")) {
            Material itemType = null;
            if (config.isSet(path + ".ITEM-TYPE"))
                itemType = Material.getMaterial(config.getString(path + ".ITEM-TYPE").toUpperCase());
            else if (config.isSet(path + ".BLOCK-TYPE"))
                itemType = Material.getMaterial(config.getString(path + ".BLOCK-TYPE").toUpperCase());
            else if (config.isSet(path + ".ITEM-ID"))
                itemType = Material.getMaterial(config.getInt(path + ".ITEM-ID"));

            if (itemType == null) return;

            Integer itemData = 0;
            if (config.isSet(path + ".ITEM-DATA"))
                itemData = config.getInt(path + ".ITEM-DATA");

            if (config.isSet(path + ".ENTITY-TYPE"))
                entityType = config.getString(path + ".ENTITY-TYPE");

            if (config.isSet(path + ".PERCENTAGE"))
                percentage = config.getDouble(path + ".PERCENTAGE");

            Integer amount = 1;
            if (config.isSet(path + ".AMOUNT"))
                amount = config.getInt(path + ".AMOUNT");

            if (!entityType.isEmpty() && itemType == Material.MONSTER_EGG) {
                itemStack = crates.getVersion_util().getSpawnEgg(EntityType.valueOf(entityType.toUpperCase()), amount);
            } else
                itemStack = new ItemStack(itemType, amount, Short.parseShort(String.valueOf(itemData)));
        } else if (type.equalsIgnoreCase("command")) {
            command = true;
            if (config.isSet(path + ".COMMANDS") && config.getStringList(path + ".COMMANDS").size() != 0) {
                commands = config.getStringList(path + ".COMMANDS");
            } else if (config.isSet(path + ".COMMANDS") && config.getStringList(path + ".COMMANDS").size() != 0) {
                commands = config.getStringList(path + ".COMMANDS");
            }

            if (commands.isEmpty()) {
                crates.getLogger().warning("No \"Commands\" found for " + path);
                return;
            }


            Material itemType = Material.PAPER;
            if (config.isSet(path + ".ITEM-TYPE"))
                itemType = Material.getMaterial(config.getString(path + ".ITEM-TYPE"));

            if (itemType == null)
                return;

            Integer itemData = 0;
            if (config.isSet(path + ".ITEM-DATA"))
                itemData = config.getInt(path + ".ITEM-DATA");

            if (config.isSet(path + ".PERCENTAGE"))
                percentage = config.getDouble(path + ".PERCENTAGE");

            Integer amount = 1;
            if (config.isSet(path + ".AMOUNT"))
                amount = config.getInt(path + ".AMOUNT");

            itemStack = new ItemStack(itemType, amount, Short.parseShort(String.valueOf(itemData)));
        } else {
            return;
        }
        ItemStack winningItemStack = itemStack.clone();
        ItemStack previewItemStack = itemStack.clone();

        boolean showAmountInTitle = false;
        int originalAmount = 0;
        if (previewItemStack.getAmount() > previewItemStack.getMaxStackSize()) { // Stop multiple stacks for the same item!
            originalAmount = previewItemStack.getAmount();
            showAmountInTitle = true;
            previewItemStack.setAmount(previewItemStack.getMaxStackSize());
        }

        ItemMeta previewItemStackItemMeta = previewItemStack.getItemMeta();
        String displayName = "";
        if (config.isSet(path + ".NAME") && !config.getString(path + ".NAME").equals("NONE"))
            displayName = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".NAME"));
        if (showAmountInTitle)
            displayName = displayName + " x" + originalAmount;
        if (!displayName.equals(""))
            previewItemStackItemMeta.setDisplayName(displayName);
        previewItemStack.setItemMeta(previewItemStackItemMeta);

        if (config.isSet(path + ".ENCHANTMENTS")) {
            List<?> enchtantments = config.getList(path + ".ENCHANTMENTS");
            for (Object object : enchtantments) {
                String enchantment = (String) object;
                String[] args = enchantment.split("-");
                try {
                    Integer level = 1;
                    if (args.length > 1)
                        level = Integer.valueOf(args[1]);
                    previewItemStack.addUnsafeEnchantment(Enchantment.getByName(args[0].toUpperCase()), level);
                } catch (Exception ignored) {
                }
            }
        }

        if (config.isSet(path + ".LORE")) {
            List<String> lines = config.getStringList(path + ".LORE");
            for (String line : lines) {
                this.lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }

        ItemMeta winningItemStackItemMeta = winningItemStack.getItemMeta();
        displayName = "";
        if (config.isSet(path + ".NAME") && !config.getString(path + ".NAME").equals("NONE"))
            displayName = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".NAME"));
        if (!displayName.equals(""))
            winningItemStackItemMeta.setDisplayName(displayName);
        winningItemStackItemMeta.setLore(this.lore);
        winningItemStack.setItemMeta(winningItemStackItemMeta);

        if (config.isSet(path + ".ENCHANTMENTS")) {
            List<?> enchtantments = config.getList(path + ".ENCHANTMENTS");
            for (Object object : enchtantments) {
                String enchantment = (String) object;
                String[] args = enchantment.split("-");
                Integer level = 1;
                if (args.length > 1)
                    level = Integer.valueOf(args[1]);
                Enchantment enchantment1 = EnchantmentUtil.getEnchantmentFromNiceName(args[0].toUpperCase());
                if (enchantment1 == null)
                    Bukkit.getLogger().warning("Invalid enchantment \"" + args[0].toUpperCase() + "\" found for item \"" + ChatColor.stripColor(displayName) + "\"");
                else
                    winningItemStack.addUnsafeEnchantment(enchantment1, level);
            }
        }
        this.winningItemStack = winningItemStack;

        previewItemStackItemMeta = previewItemStack.getItemMeta();

        List<String> lore = new ArrayList<>(this.lore);
        for (String percentageLines : config.getStringList("CHANCE-LORE")) {
            lore.add(CC.translate(percentageLines.replace("<percentage>", Double.toString(percentage))));
        }
        previewItemStackItemMeta.setLore(lore);
        previewItemStack.setItemMeta(previewItemStackItemMeta);

        valid = true;
        this.previewItemStack = previewItemStack;
    }

    public boolean isValid() {
        return valid;
    }

    public ItemStack getPreviewItemStack() {
        return previewItemStack.clone();
    }

    public ItemStack getWinningItemStack() {
        return winningItemStack.clone();
    }

    public void runWin(final Player player) {
        final Winning winning = this;
        if (isCommand() && getCommands().size() > 0) {
            for (String command : getCommands()) {
                command = CC.translate(command);
                command = command.replaceAll("<name>", player.getName());
                command = command.replaceAll("<uuid>", player.getUniqueId().toString());

                if (command.startsWith("broadcast") || command.startsWith("bc")) {
                    command = command.replace("broadcast ", "");
                    command = command.replace("bc ", "");
                    String finalCommand = command;
                    TaskUtil.runTaskAsync(() -> Bukkit.broadcastMessage(CC.translate(finalCommand)));
                    continue;
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        } else if (!isCommand()) {
            HashMap<Integer, ItemStack> left = player.getInventory().addItem(winning.getWinningItemStack());
            for (Map.Entry<Integer, ItemStack> item : left.entrySet()) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item.getValue());
            }
        }
    }

    public boolean isCommand() {
        return command;
    }

    public double getPercentage() {
        return percentage;
    }

    public List<String> getCommands() {
        return commands;
    }
}
