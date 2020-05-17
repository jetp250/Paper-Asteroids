package me.jetp250.asteroids.game.spawners;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.entities.Asteroid;
import me.jetp250.asteroids.util.AsteroidShapeGenerator;
import me.jetp250.asteroids.util.PolygonShape;
import me.jetp250.asteroids.util.Vector2f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class AsteroidSpawner {
    private static final float ASTEROIDS_PER_MAP_PER_FRAME = 1.0f / 50.0f;

    private final AsteroidShapeGenerator asteroidShapeGenerator;

    private final float mapWidth;
    private final float mapHeight;

    private final float spawnsPerFrame;
    private float asteroidsToSpawn;
    
    public AsteroidSpawner(float mapWidth, float mapHeight) {
        this.asteroidShapeGenerator = new AsteroidShapeGenerator(6.0f, 18.0f, 0.5f);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        this.spawnsPerFrame = (mapWidth/128) * (mapHeight/128) * ASTEROIDS_PER_MAP_PER_FRAME;
    }

    public void doSpawning(Game game) {
        asteroidsToSpawn += spawnsPerFrame;
        while(asteroidsToSpawn > 0.0f) {
            ThreadLocalRandom random = ThreadLocalRandom.current();

            Vector2f pos = computeAsteroidSpawnPosition(random);
            Vector2f dir = computeAsteroidVelocity(random);
            
            Asteroid asteroid = createAsteroid(pos, dir);
            game.addEntity(asteroid);

            asteroidsToSpawn -= 1;
        }
    }

    private Vector2f computeAsteroidSpawnPosition(Random random) {
        float ratio = mapWidth / (mapWidth + mapHeight);
        if (random.nextFloat() < ratio) {
            float yPos = random.nextBoolean() ? -15.0f : (mapHeight + 15.0f);
            return new Vector2f(random.nextFloat() * mapWidth, yPos);
        }

        float xPos = random.nextBoolean() ? -15.0f : (mapWidth + 15.0f);
        return new Vector2f(xPos, random.nextFloat() * mapHeight);
    }

    private Vector2f computeAsteroidVelocity(Random random) {
        Vector2f direction = new Vector2f(random.nextFloat()-0.5f, random.nextFloat()-0.5f).normalized();
        return direction.scale(0.5f + random.nextFloat() * 1.5f + random.nextFloat());
    }

    public Asteroid createAsteroid(Vector2f position, Vector2f velocity) {
        float size = asteroidShapeGenerator.generateBaseSize();

        PolygonShape shape = asteroidShapeGenerator.generateShape(size);
        return new Asteroid(position, velocity, size, shape);
    }

    public AsteroidShapeGenerator getShapeGenerator() {
        return asteroidShapeGenerator;
    }
}
