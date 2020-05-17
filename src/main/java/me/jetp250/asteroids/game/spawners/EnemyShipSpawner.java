package me.jetp250.asteroids.game.spawners;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.entities.EnemyShip;
import me.jetp250.asteroids.util.Vector2f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class EnemyShipSpawner {
    private final float mapWidth;
    private final float mapHeight;

    private int nextSpawn;
    private int ticks;

    public EnemyShipSpawner(float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        this.nextSpawn = 100;
    }

    public void doSpawning(Game game) {
        if (game.getPlayerShips().isEmpty()) {
            return;
        }

        if (ticks == nextSpawn) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int numSpawns = random.nextInt(1, 3);

            for (int i = 0; i < numSpawns; ++i) {
                Vector2f spawnpos = computeShipSpawnLocation(random);
                EnemyShip ship = new EnemyShip(spawnpos, new Vector2f(0.0f, 0.0f));

                game.addEntity(ship);
            }

            nextSpawn = ticks + random.nextInt(160, 200);
        }
        //ticks += 1;
    }

    private Vector2f computeShipSpawnLocation(Random random) {
        float ratio = mapWidth / (mapWidth + mapHeight);
        if (random.nextFloat() < ratio) {
            float yPos = random.nextBoolean() ? -15.0f : (mapHeight + 15.0f);
            return new Vector2f(random.nextFloat() * mapWidth, yPos);
        }

        float xPos = random.nextBoolean() ? -15.0f : (mapWidth + 15.0f);
        return new Vector2f(xPos, random.nextFloat() * mapHeight);
    }

}
