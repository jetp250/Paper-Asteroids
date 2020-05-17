package me.jetp250.asteroids.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Vector2f {
    public final float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f setLength(float length) {
        float scalar = length / (float) Math.sqrt(x*x + y*y);
        return new Vector2f(x * scalar, y * scalar);
    }

    public Vector2f rotate(float angleRadians) {
        float cos = (float) Math.cos(angleRadians);
        float sin = (float) Math.sin(angleRadians);
        return new Vector2f(
                x * cos - y * sin,
                x * sin + y * cos
        );
    }

    public Vector2f scale(float scale) {
        return new Vector2f(x * scale, y * scale);
    }

    public Vector2f add(float x, float y) {
        return new Vector2f(this.x + x, this.y + y);
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(x + other.x, y + other.y);
    }

    public float lengthSquared() {
        return x*x + y*y;
    }

    public float distanceSquared(Vector2f other) {
        float xDiff = x - other.x;
        float yDiff = y - other.y;
        return xDiff * xDiff + yDiff * yDiff;
    }

    public Vector2f subtract(Vector2f position) {
        return new Vector2f(x - position.x, y - position.y);
    }

    public Vector2f normalized() {
        return scale(1.0f / (float) Math.sqrt(x*x + y*y));
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public float angle() {
        return (float) Math.atan2(y, x);
    }

    public static Vector2f getRandom() {
        Random random = ThreadLocalRandom.current();
        return new Vector2f(random.nextFloat()-0.5f, random.nextFloat()-0.5f);
    }
}
