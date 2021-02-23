package me.fckml.crates.listeners;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import me.fckml.crates.config.LanguageConfig;
import me.fckml.crates.managers.Crate;
import me.fckml.crates.managers.Key;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class BlockListener implements Listener {
    private Crates crates;

    public BlockListener(Crates crates) {
        this.crates = crates;
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!crates.getConfigHandler().isDisableKeySwapping()) return;

        String title;
        ItemStack item = event.getItemDrop().getItemStack();

        for (Map.Entry<String, Crate> crate : crates.getConfigHandler().getCrates().entrySet()) {
            Key key = crate.getValue().getKey();
            if (key == null)
                continue;
            title = key.getName();

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(title)) {
                event.getPlayer().sendMessage(CC.translate(LanguageConfig.CRATE_CANT_DROP_KEY));
                event.setCancelled(true);
                return;
            }
        }

    }

    // meh idc where I put my listeners ;)
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!crates.getConfigHandler().isDisableKeySwapping())
            return;
        String title;
        List<ItemStack> items = event.getDrops();
        for (ItemStack item : items) {

            for (Map.Entry<String, Crate> crate : crates.getConfigHandler().getCrates().entrySet()) {
                Key key = crate.getValue().getKey();
                if (key == null)
                    continue;
                title = key.getName();

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(title)) {
                    // send message?
                    event.getDrops().remove(item);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (!crates.getConfigHandler().isDisableKeySwapping())
            return;
        String title;
        ItemStack item = event.getItem();

        for (Map.Entry<String, Crate> crate : crates.getConfigHandler().getCrates().entrySet()) {
            Key key = crate.getValue().getKey();
            if (key == null)
                continue;
            title = key.getName();

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(title)) {
                // Send message?
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        if (!crates.getConfigHandler().isDisableKeySwapping())
            return;
        if (event.getItem().getItemStack() != null) {
            String title;
            ItemStack item = event.getItem().getItemStack();

            for (Map.Entry<String, Crate> crate : crates.getConfigHandler().getCrates().entrySet()) {
                Key key = crate.getValue().getKey();
                if (key == null)
                    continue;
                title = key.getName();

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(title)) {
                    // Send message?
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!crates.getConfigHandler().isDisableKeySwapping())
            return;
        if (!event.getInventory().getType().toString().contains("PLAYER") && event.getCurrentItem() != null) {
            String title;
            ItemStack item = event.getCurrentItem();

            for (Map.Entry<String, Crate> crate : crates.getConfigHandler().getCrates().entrySet()) {
                Key key = crate.getValue().getKey();
                if (key == null)
                    continue;
                title = key.getName();

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(title)) {
                    // Send message?
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        String title;
        Player player = event.getPlayer();
        ItemStack item = crates.getVersion_util().getItemInPlayersHand(player);
        ItemStack itemOff = crates.getVersion_util().getItemInPlayersOffHand(player);

        for (Map.Entry<String, Crate> crate : crates.getConfigHandler().getCrates().entrySet()) {
            Key key = crate.getValue().getKey();
            if (key == null)
                continue;
            title = key.getName();

            if (itemOff != null && itemOff.hasItemMeta() && itemOff.getItemMeta().hasDisplayName() && itemOff.getItemMeta().getDisplayName() != null && itemOff.getItemMeta().getDisplayName().contains(title)) {
                item = itemOff;
            }

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(title)) {
                event.getPlayer().sendMessage(CC.translate(LanguageConfig.CRATE_PLACE_KEY));
                event.setCancelled(true);
                return;
            }
        }

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Crate!")) {
            final String crateType = item.getItemMeta().getDisplayName().replaceAll(" Crate!", "");
            final Crate crate = crates.getConfigHandler().getCrates().get(ChatColor.stripColor(crateType).toLowerCase());
            Location location = event.getBlock().getLocation();
            crate.addLocation(location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ(), location);
            crate.addToConfig(location);
            // BlockMeta to be used for some stuff in the future!
            event.getBlock().setMetadata("CrateType", new MetadataValue() {
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

            Location location1 = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
            crate.loadHolograms(location1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getMetadata("CrateType") == null || event.getBlock().getMetadata("CrateType").isEmpty()) {
            onBlockBreakLegacy(event);
            return;
        }

        String crateType = event.getBlock().getMetadata("CrateType").get(0).asString();
        Crate crate = crates.getConfigHandler().getCrates().get(crateType.toLowerCase());

        if (crate == null) return;
        Location location = event.getBlock().getLocation();

        if (event.getPlayer().isSneaking() && (crates.getConfig().getBoolean("Crate Protection") && !event.getPlayer().isOp())) {
            event.getPlayer().sendMessage(CC.translate(LanguageConfig.NO_PERMISSION));
            event.setCancelled(true);
            return;
        } else if (!event.getPlayer().isSneaking()) {
            event.getPlayer().sendMessage(CC.translate(LanguageConfig.CRATE_SNEAK_BREAK));
            event.setCancelled(true);
            return;
        }
        location.getBlock().removeMetadata("CrateType", crates);
        crate.removeHologram();
        crate.removeFromConfig(location);
    }

    public void onBlockBreakLegacy(BlockBreakEvent event) { // This is to support legacy breaks
        if (event.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) event.getBlock().getState();
            if (chest.getInventory().getTitle() != null && chest.getInventory().getTitle().contains("Crate!")) {
                Location location = chest.getLocation();

                if (event.getPlayer().isSneaking() && (crates.getConfig().getBoolean("Crate Protection") && !event.getPlayer().isOp())) {
                    event.getPlayer().sendMessage(CC.translate(LanguageConfig.NO_PERMISSION));
                    event.setCancelled(true);
                    return;
                } else if (!event.getPlayer().isSneaking()) {
                    event.getPlayer().sendMessage(CC.translate(LanguageConfig.CRATE_SNEAK_BREAK));
                    event.setCancelled(true);
                    return;
                }
                for (Entity entity : location.getWorld().getEntities()) {
                    if (entity.isDead()) continue;
                    String name = chest.getInventory().getTitle().replace(" Crate!", "");
                    if (name != null && entity.getLocation().getBlockX() == chest.getX() && entity.getLocation().getBlockZ() == chest.getZ()) {
                        entity.remove();
                    }
                }
            }
        }
    }

}
