package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.util.Vector2f;

public final class Bullet extends GameObject {
    private final GameObject shooter;
    private final byte color;

    public Bullet(GameObject shooter, Vector2f position, Vector2f velocity, byte color) {
        super(position, velocity);
        this.shooter = shooter;
        this.color = color;
    }

    public GameObject getShooter() {
        return shooter;
    }

    @Override
    public void onTick(Game game) {
        // Much efficient, very wow, every single object every single frame
        /*GameObject nearest = getClosestEntity(game);
        if (nearest == null) {
            return;
        }

        if (nearest.checkCollision(position, velocity)) {
            nearest.die(this, game);
            die(null, game);

            if (shooter instanceof PlayerShip) {
                ((PlayerShip) shooter).onKillEntity(nearest);
            }
            return;
        }

        float speed = getVelocity().length();

        Vector2f toNearest = nearest.position.subtract(position);
        Vector2f velocity = getVelocity().scale(150.0f).add(toNearest).normalized().scale(speed);

        this.velocity = velocity;*/


        for (GameObject entity : game.getAllEntities()) {
            if (entity == this || entity == shooter) {
                continue;
            }

            if (!entity.checkCollision(position, velocity)) {
                continue;
            }

            entity.die(this, game);
            die(null, game);

            if (shooter instanceof PlayerShip) {
                ((PlayerShip)shooter).onKillEntity(entity);
            }
            break;
        }
    }

    /*private GameObject getClosestEntity(Game game) {
        GameObject closest = null;
        float minDist = Float.MAX_VALUE;

        for (GameObject entity : game.getAllEntities()) {
            if (entity == this || entity == shooter || entity instanceof Bullet)
                continue;

            float distance = entity.position.distanceSquared(this.position);
            if (distance < minDist) {
                minDist = distance;
                closest = entity;
            }
        }

        return closest;
    }*/

    @Override
    public boolean checkCollision(Vector2f position, Vector2f velocity) {
        return position.distanceSquared(this.position) < 0.08f;
    }

    @Override
    public void renderTo(Canvas canvas) {
        canvas.setPixel(position.x, position.y, color);
    }

}
