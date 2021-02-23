package me.fckml.crates.listeners;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.config.LanguageConfig;
import me.fckml.crates.listeners.events.CrateOpenEvent;
import me.fckml.crates.listeners.events.CratePreviewEvent;
import me.fckml.crates.managers.Crate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PlayerInteractListener implements Listener {
    private Crates crates;
    private HashMap<String, Long> lastOpended = new HashMap<String, Long>();

    public PlayerInteractListener(Crates crates) {
        this.crates = crates;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR)
            return;
        ItemStack item = crates.getVersion_util().getItemInPlayersHand(player);
        ItemStack itemOff = crates.getVersion_util().getItemInPlayersOffHand(player);

        String crateType;
        if (event.getClickedBlock().getMetadata("CrateType") == null || event.getClickedBlock().getMetadata("CrateType").isEmpty()) {
            // Try to use the old method of getting the crate!
            if (event.getClickedBlock().getType() != Material.CHEST)
                return;
            Chest chest = (Chest) event.getClickedBlock().getState();
            Inventory chestInventory = chest.getInventory();
            if (chestInventory.getTitle() == null || !chestInventory.getTitle().contains(" Crate!"))
                return;
            crateType = ChatColor.stripColor(chestInventory.getTitle().replaceAll(" Crate!", ""));
        } else {
            crateType = event.getClickedBlock().getMetadata("CrateType").get(0).asString();
        }

        if (!crates.getConfig().isSet("CRATES." + crateType)) {
            return;
        }

        Crate crate = crates.getConfigHandler().getCrates().get(crateType.toLowerCase());

        if (crate == null) {
            return; // Not sure if we should do some warning here? TODO
        }

        if (crate.getPermission() != null && !player.hasPermission(crate.getPermission())) {
            event.setCancelled(true);
            return;
        }
        String title = crate.getKey().getName();
        if (event.getAction().toString().contains("LEFT")) {
            if (event.getPlayer().isSneaking())
                return;
            /** Do preview */
            CratePreviewEvent cratePreviewEvent = new CratePreviewEvent(player, crateType, crates);
            if (!cratePreviewEvent.isCanceled())
                cratePreviewEvent.doEvent();
        } else {
            boolean usingOffHand = false;
            if (itemOff != null && itemOff.hasItemMeta() && itemOff.getItemMeta().getDisplayName() != null && itemOff.getItemMeta().getDisplayName().equals(title)) {
                item = itemOff;
                usingOffHand = true;
            }

            if (crates.getCrateHandler().hasOpening(player.getUniqueId())) {
                crates.getCrateHandler().getOpening(player.getUniqueId()).doReopen(player, crate, event.getClickedBlock().getLocation());
                event.setCancelled(true);
                return;
            }

            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(title)) {
                event.setCancelled(true);

                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(CC.translate(LanguageConfig.CRATE_INVENTORY_FULL));
                    return;
                }

                if (crate.getCooldown() > 0 && lastOpended.containsKey(player.getUniqueId().toString()) && lastOpended.get(player.getUniqueId().toString()) + crate.getCooldown() > TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) {
                    long whenCooldownEnds = lastOpended.get(player.getUniqueId().toString()) + crates.getConfigHandler().getDefaultCooldown();
                    long remaining = whenCooldownEnds - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    player.sendMessage(CC.translate(LanguageConfig.CRATE_COOLDOWN_MESSAGE.replace("<remaining>", Long.toString(remaining))));
                    return;
                }

                lastOpended.put(player.getUniqueId().toString(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())); // Store time in seconds of when the player opened the crate

                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    if (usingOffHand) {
                        crates.getVersion_util().removeItemInOffHand(player);
                    } else {
                        player.setItemInHand(null);
                    }
                }

                CrateOpenEvent crateOpenEvent = new CrateOpenEvent(player, crateType, event.getClickedBlock().getLocation(), crates);
                crateOpenEvent.doEvent();
            } else {
                player.sendMessage(CC.translate(LanguageConfig.CRATE_NO_KEY_IN_HAND.replace("<crate>", crate.getName(false))));
                if (crate.getKnockback() != 0) {
                    player.setVelocity(player.getLocation().getDirection().multiply(-crate.getKnockback()));
                }
                event.setCancelled(true);
            }
        }
    }

}
