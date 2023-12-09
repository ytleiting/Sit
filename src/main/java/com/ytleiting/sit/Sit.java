package com.ytleiting.sit;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Cod;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public final class Sit extends JavaPlugin {

    public static Sit sitPlugin;
    public static NamespacedKey chairNamespacedKey;
    static TextComponent actionbarMessage;

    @Override
    public void onEnable() {
        sitPlugin = this;
        chairNamespacedKey = new NamespacedKey(this, "chair");

        this.saveDefaultConfig();

        actionbarMessage = new TextComponent(sitPlugin.getConfig().getString("actionbar-message.message"));

        new SitHandler(this);
        Bukkit.getPluginCommand("sit").setExecutor(new SitCommand());

        Bukkit.getServer().getWorlds().forEach(world -> {
            world.getEntities().forEach(entity -> {
                PersistentDataContainer data = entity.getPersistentDataContainer();
                if (Boolean.TRUE.equals(data.get(chairNamespacedKey, PersistentDataType.BOOLEAN))) {
                    entity.remove();
                }
            });
        });

        Bukkit.getLogger().info("Sit Plugin by ytleiting enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Sit Plugin by ytleiting disabled.");
    }

    public static void sitDown(Location location, Player player) {
        Cod entity = (Cod) location.getWorld().spawnEntity(location, EntityType.COD);

        entity.setAI(false);
        entity.setInvulnerable(true);
        entity.setCollidable(false);
        entity.setSilent(true);
        entity.setGravity(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
                9999999, 1, false, false, false));
        //entity.teleport(pos);
        entity.addPassenger(player);
        entity.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());

        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(chairNamespacedKey, PersistentDataType.BOOLEAN, true);

        if (sitPlugin.getConfig().getBoolean("actionbar-message.enable")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionbarMessage);
                }
            }.runTaskLater(sitPlugin, 2);
        }

    }
}
