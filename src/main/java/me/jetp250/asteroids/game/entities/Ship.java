package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.util.Vector2f;
import org.bukkit.Sound;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Ship extends GameObject {
    private final ShipShape shape;

    private int ticksLived;
    private int lastShot;

    private float bulletSpreadRadians;
    private int shootingCooldown;

    private boolean shoot;
    private byte projectileColor;
    private float projectileSpeed;
    private float projectileInaccuracy;

    public Ship(Vector2f position, Vector2f velocity) {
        super(position, velocity);
        this.shape = new ShipShape();
    }

    public ShipShape getShape() {
        return shape;
    }

    public void setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
    }

    public void setProjectileInaccuracy(float projectileInaccuracy) {
        this.projectileInaccuracy = projectileInaccuracy;
    }

    public void setShootingCooldown(int shootingCooldown) {
        this.shootingCooldown = shootingCooldown;
    }

    public void setBulletSpread(float angleRadians) {
        this.bulletSpreadRadians = angleRadians;
    }

    public void rotate(float angleRad) {
        shape.getHitbox().rotate(angleRad);
    }

    public void accelerate(float speed) {
        velocity = velocity.add(getHeading().scale(speed));
    }

    public void setProjectileColor(byte projectileColor) {
        this.projectileColor = projectileColor;
    }

    public void shoot() {
        if (ticksLived - lastShot >= shootingCooldown) {
            shoot = true;
        }
    }

    public Vector2f getHeading() {
        return shape.getHitbox().getPoints()[0].normalized();
    }

    @Override
    protected void onTick(Game game) {
        velocity = velocity.scale(0.96f);
        if (shoot) {
            Vector2f forwardVec = getHeading();
            int numBullets = getNumberOfBullets();

            float angle = -(numBullets - 1) * bulletSpreadRadians / 2.0f;

            for (int i = 0; i < numBullets; ++i) {
                float a = angle + ThreadLocalRandom.current().nextFloat() * 2.0f * projectileInaccuracy - projectileInaccuracy;
                Vector2f direction = forwardVec.rotate(a);
                Vector2f velocity = direction.scale(projectileSpeed);
                angle += bulletSpreadRadians;

                Bullet bullet = new Bullet(this, position.add(shape.getHitbox().getPoints()[0]), velocity, projectileColor);
                game.addEntity(bullet);
            }
            shoot = false;
            lastShot = ticksLived;

            game.playSound(Sound.ENTITY_EGG_THROW, position, 0.5f, 2.0f);
        }
        ticksLived += 1;
    }

    protected int getNumberOfBullets() {
        return 1;
    }

    @Override
    public boolean checkCollision(Vector2f position, Vector2f velocity) {
        Vector2f segmentStart = position.subtract(this.position);
        Vector2f segmentEnd = segmentStart.add(velocity);

        return shape.getHitbox().edgeIntersectsWithSegment(segmentStart, segmentEnd);
        //return shape.getHitbox().containsPoint(position.x - this.position.x, position.y - this.position.y);
    }

    protected void renderShapeTo(Canvas canvas, byte color) {
        shape.getHitbox().renderTo(canvas, position.x, position.y, color);
        //shape.renderTo(canvas, position, false, color);
    }
}
