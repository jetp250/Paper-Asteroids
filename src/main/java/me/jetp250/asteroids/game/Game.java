package me.jetp250.asteroids.game;

import me.jetp250.asteroids.game.entities.Bullet;
import me.jetp250.asteroids.game.entities.GameObject;
import me.jetp250.asteroids.game.entities.PlayerShip;
import me.jetp250.asteroids.game.rendering.ItemFrameDisplay;
import me.jetp250.asteroids.game.rendering.TrackRenderer;
import me.jetp250.asteroids.game.spawners.AsteroidSpawner;
import me.jetp250.asteroids.game.spawners.EnemyShipSpawner;
import me.jetp250.asteroids.track.Track;
import me.jetp250.asteroids.util.BlockPos;
import me.jetp250.asteroids.util.Vector2f;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public final class Game {
    private final Track track;
    private final World world;
    private final int mapWidth;
    private final int mapHeight;

    private final TrackRenderer renderer;

    private final Map<Player, PlayerShip> ships;
    private final List<GameObject> allEntities;

    private final List<GameObject> spawnQueue;

    private final AsteroidSpawner asteroidSpawner;
    private final EnemyShipSpawner enemyShipSpawner;

    public Game(Track track, World world) {
        this.track = track;
        this.world = world;
        this.ships = new HashMap<>();
        this.allEntities = new ArrayList<>();
        this.renderer = new TrackRenderer(track);
        this.spawnQueue = new ArrayList<>();

        ItemFrameDisplay display = track.getDisplay();
        this.mapWidth = display.getWidth() * 128;
        this.mapHeight = display.getHeight() * 128;

        this.asteroidSpawner = new AsteroidSpawner(mapWidth, mapHeight);
        this.enemyShipSpawner = new EnemyShipSpawner(mapWidth, mapHeight);
    }

    public void playSound(Sound sound, Vector2f at, float volume, float pitch) {
        Location worldPos = getLocationInWorld(at).toLocation(world);

        world.getNearbyPlayers(worldPos, 15.0).forEach(player -> player.playSound(worldPos, sound, SoundCategory.MASTER, volume, pitch));
    }

    public void addEntity(GameObject object) {
        spawnQueue.add(object);
    }

    public List<GameObject> getAllEntities() {
        return Collections.unmodifiableList(allEntities);
    }

    public Collection<PlayerShip> getPlayerShips() {
        return Collections.unmodifiableCollection(ships.values());
    }

    public PlayerShip addPlayer(Player player) {
        PlayerShip existing = ships.get(player);
        if (existing != null && !existing.isDead()) {
            throw new IllegalArgumentException("Player is already in-game!");
        }

        PlayerShip ship = new PlayerShip(player, new Vector2f(mapWidth/2.0f, mapHeight/2.0f));
        ships.put(player, ship);
        addEntity(ship);

        return ship;
    }

    public void removePlayer(Player player) {
        PlayerShip ship = ships.remove(player);
        if (ship != null) {
            if (!ship.isDead()) {
                ship.die(null, this);
                return;
            }

            for (GameObject entity : allEntities) {
                if (entity instanceof Bullet && ((Bullet)entity).getShooter() == ship) {
                    entity.die(null, this);
                }
            }
        }
    }

    public Vector getLocationInWorld(Vector2f pos) {
        ItemFrameDisplay display = track.getDisplay();
        BlockPos minCorner = display.getMinCorner();

        Vector offset = switch(display.getFacing()) {
            case DOWN, UP -> new Vector(pos.x, 0.0f, pos.y);
            case SOUTH, NORTH -> new Vector(pos.x, pos.y, 0.0);
            case EAST, WEST -> new Vector(0.0, pos.x, pos.y);
            default -> throw new AssertionError("illegal facing?");
        };

        return offset.multiply(1.0/128.0).add(new Vector(minCorner.x, minCorner.y, minCorner.z));
    }

    public void update() {
        allEntities.addAll(spawnQueue);
        spawnQueue.clear();

        asteroidSpawner.doSpawning(this);
        enemyShipSpawner.doSpawning(this);

        Iterator<GameObject> iterator = allEntities.iterator();
        while(iterator.hasNext()) {
            GameObject entity = iterator.next();
            if (entity.isDead() || isOutOfBounds(entity)) {
                iterator.remove();
            }

            entity.update(this);
        }
        
        render();
    }

    public void render() {
        renderer.render(this, null);
    }

    private boolean isOutOfBounds(GameObject entity) {
        Vector2f pos = entity.getPosition();
        return pos.x <= -20 || pos.y <= -20 || pos.x >= mapWidth + 20 || pos.y >= mapHeight + 20;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public AsteroidSpawner getAsteroidSpawner() {
        return asteroidSpawner;
    }
}
