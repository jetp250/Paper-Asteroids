package me.jetp250.asteroids.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.jetp250.asteroids.AsteroidsPlugin;
import me.jetp250.asteroids.track.Track;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private final World world;

    private final Map<Track, Game> games;

    private final Int2ObjectMap<Track> tracksByID;

    public GameManager(World world, AsteroidsPlugin asteroidsPlugin) {
        this.world = world;
        this.games = new HashMap<>();
        this.tracksByID = new Int2ObjectOpenHashMap<>();

        Bukkit.getScheduler().runTaskTimer(asteroidsPlugin, this::update, 0L, 1L);
    }

    public Game joinOrCreateGame(int trackID) {
        Track track = tracksByID.get(trackID);
        if (track == null) {
            return null;
        }

        return games.computeIfAbsent(track, k -> new Game(track, world));
    }

    private void update() {
        games.values().forEach(Game::update);
    }

    public void addTrack(Track track) {
        if (tracksByID.containsKey(track.getId())) {
            return;
        }
        tracksByID.put(track.getId(), track);

        track.getDisplay().spawnItemFrames(world, track.getId());
        games.put(track, new Game(track, world));
    }
}
