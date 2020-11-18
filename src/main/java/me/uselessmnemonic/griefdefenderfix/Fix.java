package me.uselessmnemonic.griefdefenderfix;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class Fix extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Fix.sanitize(player.getInventory());
                    player.updateInventory();
                });
            }
        }.runTaskTimer(this, 0, 40);
    }

    @EventHandler
    public void onInventoryOpened(InventoryOpenEvent event) {
        Fix.sanitize(event.getInventory());
    }

    @EventHandler
    public void onLoginEvent(PlayerLoginEvent event) {
        Fix.sanitize(event.getPlayer().getInventory());
        Fix.sanitize(event.getPlayer().getEnderChest());
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onItemGrab(PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        item.setItemStack(Fix.sanitize(item.getItemStack()));
    }

    private static void sanitize(@Nonnull Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null) {
                inventory.setItem(i, Fix.sanitize(stack));
            }
        }
    }

    private static ItemStack sanitize(@Nonnull ItemStack stack) {

        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            Plugin gdPlugin = Bukkit.getPluginManager().getPlugin("griefdefender");
            assert gdPlugin != null;
            NamespacedKey key = new NamespacedKey(gdPlugin, "owner");

            if (container.has(key, PersistentDataType.STRING)) {
                container.remove(key);
                stack.setItemMeta(meta);
            }
        }

        return stack;
    }
}
