package me.jetp250.asteroids.controller;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.jetp250.asteroids.AsteroidsPlugin;
import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.entities.PlayerShip;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

// I'm starting to get tired of the 'Object + ObjectManager' pattern..
public final class ShipControllerManager extends PacketAdapter implements Listener {
    private final AsteroidsPlugin plugin;
    private final Map<Player, ShipController> controllers;

    public ShipControllerManager(AsteroidsPlugin plugin) {
        super(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.STEER_VEHICLE);
        this.plugin = plugin;
        this.controllers = new HashMap<>();
    }

    public void enterControlMode(Player player, Game game, PlayerShip ship) {
        if (controllers.containsKey(player)) {
            throw new IllegalStateException("Player is already on a controller");
        }

        ShipController controller = ShipController.spawnAndMount(player, game, ship);
        controllers.put(player, controller);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        ShipController controller = controllers.get(event.getPlayer());
        if (controller == null) {
            return;
        }
        if (controller.isShipDead()) {
            controllers.remove(event.getPlayer());
            Bukkit.getScheduler().runTask(plugin, controller::cleanup);
            return;
        }
        event.setCancelled(true);

        PacketContainer packet = event.getPacket();
        StructureModifier<Float> floats = packet.getFloat();
        StructureModifier<Boolean> booleans = packet.getBooleans();

        // https://wiki.vg/Protocol#Steer_Vehicle
        float sideways = floats.read(0);
        float forward = floats.read(1);
        boolean spacePressed = booleans.read(0);
        boolean shiftPressed = booleans.read(1);

        Bukkit.getScheduler().runTask(plugin, () -> controller.move(forward, sideways, spacePressed, shiftPressed));
    }
}
