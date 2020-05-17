package me.jetp250.asteroids.util;

import java.util.concurrent.ThreadLocalRandom;

public final class AsteroidShapeGenerator {
    private final float minSize;
    private final float maxSize;

    private final float sizeVariation;

    public AsteroidShapeGenerator(float minSize, float maxSize, float sizeVariation) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.sizeVariation = sizeVariation;
    }

    public float generateBaseSize() {
        return (float) ThreadLocalRandom.current().nextDouble(minSize, maxSize);
    }

    public PolygonShape generateShape(float baseSize) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int numPoints = random.nextInt(6, 8);
        Vector2f[] shape = new Vector2f[numPoints];

        for (int i = 0; i < numPoints; ++i) {
            float distanceFromOrigin = baseSize + (float) random.nextDouble(-sizeVariation, sizeVariation) * baseSize;
            float angle = (i / (float) numPoints) * 2.0f * (float) Math.PI;

            shape[i] = new Vector2f(
                (float) Math.cos(angle) * distanceFromOrigin,
                (float) Math.sin(angle) * distanceFromOrigin
            );
        }
        return new PolygonShape(shape);
    }

}
