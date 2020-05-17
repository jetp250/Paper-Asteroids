package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.util.Vector2f;

public abstract class GameObject {
    protected Vector2f position;
    protected Vector2f velocity;

    private boolean dead;

    public GameObject(Vector2f position, Vector2f velocity) {
        this.position = position;
        this.velocity = velocity;
        this.dead = false;
    }

    public final Vector2f getPosition() {
        return position;
    }

    public final boolean isDead() {
        return dead;
    }

    public void die(GameObject killer, Game game) {
        this.dead = true;
    }

    public final void update(Game game) {
        position = position.add(velocity);
        onTick(game);
    }

    protected abstract void onTick(Game game);

    public abstract boolean checkCollision(Vector2f position, Vector2f velocity);

    public abstract void renderTo(Canvas canvas);

}
