package me.jetp250.asteroids.game;

import me.jetp250.asteroids.AsteroidsPlugin;
import me.jetp250.asteroids.controller.ShipControllerManager;
import me.jetp250.asteroids.game.entities.PlayerShip;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public final class ItemFrameEventListener implements Listener {
    private final NamespacedKey trackIdTag;
    private final AsteroidsPlugin plugin;

    public ItemFrameEventListener(AsteroidsPlugin plugin, NamespacedKey trackIdTag) {
        this.trackIdTag = trackIdTag;
        this.plugin = plugin;
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.ITEM_FRAME) {
            return;
        }

        boolean tagged = event.getEntity().getPersistentDataContainer().has(trackIdTag, PersistentDataType.INTEGER);
        if (tagged) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ITEM_FRAME || !(event.getDamager() instanceof Player)) {
            return;
        }
        boolean entered = enterGameIfTagged((Player) event.getDamager(), event.getEntity());
        event.setCancelled(entered);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (clicked.getType() != EntityType.ITEM_FRAME) {
            return;
        }

        boolean entered = enterGameIfTagged(event.getPlayer(), clicked);
        event.setCancelled(entered);
    }

    private boolean enterGameIfTagged(Player toJoin, Entity clickedFrame) {
        Integer trackID = clickedFrame.getPersistentDataContainer().get(trackIdTag, PersistentDataType.INTEGER);
        if (trackID == null) {
            return false; // was regular item frame
        }

        GameManager gameManager = plugin.getGameManagerIfExists(clickedFrame.getWorld());
        Game game = gameManager.joinOrCreateGame(trackID);
        if (game == null) {
            return false;
        }

        PlayerShip ship = game.addPlayer(toJoin);
        ShipControllerManager manager = plugin.getShipControllerManager();
        manager.enterControlMode(toJoin, game, ship);
        return true;
    }

}
