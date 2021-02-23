package me.fckml.crates.utils;

import me.fckml.crates.Crates;
import org.bukkit.Bukkit;

public class TaskUtil {


    public static void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Crates.getInstance(), runnable);
    }
}
