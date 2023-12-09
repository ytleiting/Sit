package com.ytleiting.sit;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.UUID;

import static com.ytleiting.sit.Sit.chairNamespacedKey;
import static com.ytleiting.sit.Sit.sitPlugin;

public class SitHandler implements Listener {
    public SitHandler(Sit plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void sitDown(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getBlockFace().equals(BlockFace.DOWN)) return;
        Player player = event.getPlayer();
        if (block.getLocation().distance(player.getLocation()) > 3) return;
        BlockData blockData = block.getBlockData();
        if (!(
                (block.getType().toString().endsWith("STAIRS") && sitPlugin.getConfig().getBoolean("chair-blocks.stairs")) ||
                        (block.getType().toString().endsWith("SLAB") && sitPlugin.getConfig().getBoolean("chair-blocks.slabs")) ||
                        (block.getType().toString().endsWith("CARPET") && sitPlugin.getConfig().getBoolean("chair-blocks.carpets"))
        )) return;
        if (block.getLocation().add(0, 1, 0).getBlock().getType().isSolid()) return;

        UUID uuid = player.getUniqueId();


        boolean playerOnChair = Bukkit.getServer().getWorlds().stream().anyMatch(world ->
                world.getEntities().stream().anyMatch(entity ->
                        entity.getPassengers().stream().anyMatch(passenger -> passenger.getUniqueId().equals(uuid) &&
                                Boolean.TRUE.equals(entity.getPersistentDataContainer().get(chairNamespacedKey, PersistentDataType.BOOLEAN))
                        )
                )
        );

        if (playerOnChair) return;

        boolean isCarpet = (block.getType().toString().endsWith("CARPET"));

        Location location = block.getLocation().add(0.5, isCarpet ? -0.2 : 0.3, 0.5);

        Sit.sitDown(location, player);
    }

    @EventHandler
    public void standUp(EntityDismountEvent event) {
        Entity entity = event.getDismounted();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (Boolean.TRUE.equals(data.get(chairNamespacedKey, PersistentDataType.BOOLEAN))) {
            entity.setInvulnerable(false);
            if (entity.getPassengers().size() > 0) {
                Entity passenger = entity.getPassengers().get(0);
                Block block = entity.getLocation().getBlock();

                double y;
                if (block.getBlockData() instanceof Stairs) {
                    y = 1;
                } else if (block.getBlockData() instanceof Slab) {
                    y = 0.5;
                } else {
                    y = 0.6;
                }

                passenger.teleport(passenger.getLocation().add(0, y, 0));
            }
            entity.remove();
        }
    }
}
