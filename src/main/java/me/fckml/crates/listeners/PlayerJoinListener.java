package me.fckml.crates.listeners;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.config.LanguageConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private Crates crates;

    public PlayerJoinListener(Crates crates) {
        this.crates = crates;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (crates.getCrateHandler().hasPendingKeys(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(CC.translate(LanguageConfig.CRATE_CRATES_WAITING));
        }
    }
}
