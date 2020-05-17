package me.jetp250.asteroids;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.jetp250.asteroids.commands.AsteroidsCommand;
import me.jetp250.asteroids.controller.ShipControllerManager;
import me.jetp250.asteroids.game.ItemFrameEventListener;
import me.jetp250.asteroids.game.GameManager;
import me.jetp250.asteroids.track.TrackIdCounter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class AsteroidsPlugin extends JavaPlugin {
    private final NamespacedKey itemFrameIDTag = new NamespacedKey(this, "AsteroidsTrackID");

    private Map<World, GameManager> gameManagersByWorld;

    private TrackIdCounter trackIdCounter;

    private ShipControllerManager shipControllerManager;

    @Override
    public void onEnable() {
        getCommand("asteroids").setExecutor(new AsteroidsCommand());
        this.gameManagersByWorld = new HashMap<>();
        this.trackIdCounter = new TrackIdCounter(0);

        this.shipControllerManager = new ShipControllerManager(this);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(shipControllerManager);

        Bukkit.getPluginManager().registerEvents(new ItemFrameEventListener(this, itemFrameIDTag), this);
        Bukkit.getPluginManager().registerEvents(shipControllerManager, this);
    }


    public ShipControllerManager getShipControllerManager() {
        return shipControllerManager;
    }

    public TrackIdCounter getTrackIdCounter() {
        return trackIdCounter;
    }

    public NamespacedKey getItemFrameTag() {
        return itemFrameIDTag;
    }

    public GameManager getGameManagerIfExists(World world) {
        return gameManagersByWorld.get(world);
    }

    public GameManager getOrCreateGameManager(World world) {
        return gameManagersByWorld.computeIfAbsent(world, w -> new GameManager(w, this));
    }

}
