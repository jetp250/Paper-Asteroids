package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.game.rendering.Colors;
import me.jetp250.asteroids.util.AsteroidShapeGenerator;
import me.jetp250.asteroids.util.PolygonShape;
import me.jetp250.asteroids.util.Vector2f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Asteroid extends GameObject {
    private final PolygonShape shape;
    private final float rotationSpeed; // radians per update
    private final float size;

    public Asteroid(Vector2f position, Vector2f velocity, float size, PolygonShape shape) {
        super(position, velocity);

        this.size = size;
        this.shape = shape;
        this.rotationSpeed = computeRotationSpeed();
    }

    @Override
    public void die(GameObject killer, Game game) {
        super.die(killer, game);

        if (this.size >= 5.0f) {
            Random random = ThreadLocalRandom.current();
            AsteroidShapeGenerator shapeGenerator = game.getAsteroidSpawner().getShapeGenerator();

            float baseSpeed = this.velocity.length() * 1.05f;
            float baseSize = this.size / 2.0f;
            for (int i = 0; i < 2; ++i) {
                float speed = Math.max(0.3f, baseSpeed + baseSpeed * (random.nextFloat() * 0.15f - 0.1f));
                Vector2f velocity = Vector2f.getRandom().normalized().scale(speed);

                PolygonShape shape = shapeGenerator.generateShape(baseSize);

                Asteroid asteroid = new Asteroid(position, velocity, this.size / 2.0f, shape);
                game.addEntity(asteroid);
            }
        }
    }

    @Override
    public void onTick(Game game) {
        shape.rotate(rotationSpeed);
    }

    @Override
    public boolean checkCollision(Vector2f position, Vector2f velocity) {
        return shape.containsPoint(position.x - this.position.x, position.y - this.position.y);
    }

    @Override
    public void renderTo(Canvas canvas) {
        shape.renderTo(canvas, position.x, position.y, Colors.WHITE);
    }

    private static float computeRotationSpeed() {
        return (float) Math.toRadians(ThreadLocalRandom.current().nextDouble(1.0f, 5.0f));
    }

    public PolygonShape getShape() {
        return shape;
    }
}
