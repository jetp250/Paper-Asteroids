package me.jetp250.asteroids.controller;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.entities.PlayerShip;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ShipController {
    private final Entity vehicleEntity;
    private final Game game;
    private final PlayerShip ship;
    private final Player player;

    public ShipController(Entity vehicleEntity, Player player, PlayerShip ship, Game game) {
        this.vehicleEntity = vehicleEntity;
        this.ship = ship;
        this.game = game;
        this.player = player;
    }

    public boolean isShipDead() {
        return ship.isDead();
    }

    public void cleanup() {
        player.eject();
        vehicleEntity.remove();
        player.setGameMode(GameMode.CREATIVE);
    }

    public void move(float forward, float sideways, boolean spacePressed, boolean shiftPressed) {
        if (shiftPressed) {
            game.removePlayer(this.player);
            return;
        }

        ship.accelerate(0.5f * forward);
        ship.rotate(-sideways * 0.18f);

        if (spacePressed) {
            ship.shoot();
        }

        Location loc = game.getLocationInWorld(ship.getPosition()).toLocation(vehicleEntity.getWorld());

        net.minecraft.server.v1_15_R1.Entity entity = ((CraftEntity)vehicleEntity).getHandle();
        entity.setPosition(loc.getX(), loc.getY() - 0.5f, loc.getZ());
        entity.impulse = true;
    }

    public static ShipController spawnAndMount(Player player, Game game, PlayerShip ship) {
        Entity pig = player.getWorld().spawn(player.getLocation(), AreaEffectCloud.class, primer -> {
            primer.setInvulnerable(true);
            primer.setSilent(true);
            primer.setGravity(false);
            primer.setDuration(9999999);
            primer.setRadius(0.1f);
            primer.setDurationOnUse(0);
        });

        player.setGameMode(GameMode.SPECTATOR);
        pig.addPassenger(player);

        return new ShipController(pig, player, ship, game);
    }
}
