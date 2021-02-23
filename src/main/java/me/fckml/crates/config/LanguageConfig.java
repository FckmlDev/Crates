package me.fckml.crates.config;


import me.fckml.crates.Crates;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class LanguageConfig {

    public static String CONFIG_RELOADED;

    public static String NO_PERMISSION;
    public static String PLAYERS_ONLY;

    public static List<String> CRATE_COMMAND_HELP;
    public static String CRATE_COMMAND_ALREADY_EXISTS;
    public static String CRATE_COMMAND_CRATE_CREATED;
    public static String CRATE_COMMAND_NOT_FOUND;
    public static String CRATE_COMMAND_RENAMED;
    public static String CRATE_COMMAND_DELETED;
    public static String CRATE_COMMAND_INVALID_AMOUNT;
    public static String CRATE_COMMAND_PLAYER_NOT_FOUND;
    public static String CRATE_COMMAND_KEY_GIVE_ALL;
    public static String CRATE_COMMAND_KEY_GIVE_PLAYER;
    public static String CRATE_COMMAND_ITEM_NOT_FOUND;
    public static String CRATE_COMMAND_KEY_SET;

    public static String KEY_NOT_FOUND;
    public static String DO_CRATE_CLAIM;
    public static String RECEIVED_KEY;

    public static String CRATE_COLOR_EDITED;
    public static String CRATE_WINNINGS_UPDATED;
    public static String CRATE_CRATES_WAITING;
    public static String CRATE_INVENTORY_FULL;
    public static String CRATE_COOLDOWN_MESSAGE;
    public static String CRATE_NO_KEY_IN_HAND;
    public static String CRATE_CANT_DROP_KEY;
    public static String CRATE_PLACE_KEY;
    public static String CRATE_SNEAK_BREAK;

    public LanguageConfig() {
        FileConfiguration config = Crates.getInstance().getLanguageFile();

        CONFIG_RELOADED = config.getString("CONFIG-RELOADED");

        NO_PERMISSION = config.getString("NO-PERMISSION");
        PLAYERS_ONLY = config.getString("PLAYERS-ONLY");

        CRATE_COMMAND_HELP = config.getStringList("CRATE-COMMAND.HELP");
        CRATE_COMMAND_ALREADY_EXISTS = config.getString("CRATE-COMMAND.ALREADY-EXISTS");
        CRATE_COMMAND_CRATE_CREATED = config.getString("CRATE-COMMAND.CREATED");
        CRATE_COMMAND_NOT_FOUND = config.getString("CRATE-COMMAND.NOT-EXISTS");
        CRATE_COMMAND_RENAMED = config.getString("CRATE-COMMAND.RENAMED");
        CRATE_COMMAND_DELETED = config.getString("CRATE-COMMAND.DELETED");
        CRATE_COMMAND_INVALID_AMOUNT = config.getString("CRATE-COMMAND.INVALID-AMOUNT");
        CRATE_COMMAND_PLAYER_NOT_FOUND = config.getString("CRATE-COMMAND.PLAYER-NOT-FOUND");
        CRATE_COMMAND_KEY_GIVE_ALL = config.getString("CRATE-COMMAND.KEY-GIVE-ALL");
        CRATE_COMMAND_KEY_GIVE_PLAYER = config.getString("CRATE-COMMAND.KEY-GIVE-PLAYER");
        CRATE_COMMAND_ITEM_NOT_FOUND = config.getString("CRATE-COMMAND.ITEM-NOT-FOUND");
        CRATE_COMMAND_KEY_SET = config.getString("CRATE-COMMAND.KEY-SET");

        KEY_NOT_FOUND = config.getString("KEY-NOT-FOUND");
        DO_CRATE_CLAIM = config.getString("DO-CRATE-CLAIM");
        RECEIVED_KEY = config.getString("RECEIVED-KEY");

        CRATE_COLOR_EDITED = config.getString("CRATE-COLOR-EDITED");
        CRATE_WINNINGS_UPDATED = config.getString("CRATE-WINNINGS-UPDATED");
        CRATE_CRATES_WAITING = config.getString("CRATE-CRATES-WAITING");
        CRATE_INVENTORY_FULL = config.getString("CRATE-INVENTORY-FULL");
        CRATE_COOLDOWN_MESSAGE = config.getString("CRATE-COOLDOWN-MESSAGE");
        CRATE_NO_KEY_IN_HAND = config.getString("CRATE-NO-KEY-IN-HAND");
        CRATE_CANT_DROP_KEY = config.getString("CRATE-CANT-DROP-KEY");
        CRATE_PLACE_KEY = config.getString("CRATE-PLACE-KEY");
        CRATE_SNEAK_BREAK = config.getString("CRATE-SNEAK-BREAK");
    }
}
