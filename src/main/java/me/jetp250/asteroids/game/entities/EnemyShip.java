package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.game.rendering.Colors;
import me.jetp250.asteroids.util.Vector2f;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public final class EnemyShip extends Ship {
    private static final float ROTATION_SPEED_RAD = (float) Math.toRadians(3.0f);//2.0f);
    private static final float MOVEMENT_SPEED = 0.03f;//0.1f;

    private float headingAngle;

    public EnemyShip(Vector2f position, Vector2f velocity) {
        super(position, velocity);
        this.headingAngle = getHeading().angle();

        setBulletSpread((float) Math.toRadians(10.0f));
        setShootingCooldown(5);
        setProjectileColor(Colors.RED);
        setProjectileSpeed(4.0f);
        setProjectileInaccuracy((float) Math.toRadians(5.0f));
    }

    @Override
    protected void onTick(Game game) {
        super.onTick(game);

        Collection<PlayerShip> players = game.getPlayerShips();
        PlayerShip closest = findClosestShip(players);
        if (closest == null) {
            return; // do idle movement?
        }

        Vector2f toClosest = closest.getPosition().subtract(position);
        float sqDistance = toClosest.lengthSquared();

        if (sqDistance > 40*40) {
            float angle = toClosest.angle();
            float diff = Math.max(-ROTATION_SPEED_RAD, Math.min(ROTATION_SPEED_RAD, angle - headingAngle));

            rotate(diff);
            accelerate(MOVEMENT_SPEED);
        }

        if (ThreadLocalRandom.current().nextInt(30) == 0) {
            shoot();
        }
    }

    private PlayerShip findClosestShip(Collection<PlayerShip> players) {
        PlayerShip closest = null;
        float minDistance = Float.MAX_VALUE;

        for (PlayerShip ship : players) {
            float distance = ship.getPosition().distanceSquared(this.position);
            if (distance < minDistance) {
                minDistance = distance;
                closest = ship;
            }
        }
        return closest;
    }

    @Override
    public void rotate(float angleRad) {
        super.rotate(angleRad);
        this.headingAngle += angleRad;
    }

    @Override
    public void renderTo(Canvas canvas) {
        renderShapeTo(canvas, Colors.RED);
    }
}
