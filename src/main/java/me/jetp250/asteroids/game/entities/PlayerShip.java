package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.game.rendering.Colors;
import me.jetp250.asteroids.util.PolygonShape;
import me.jetp250.asteroids.util.Vector2f;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public final class PlayerShip extends Ship {
    private static final int SHOOTING_COOLDOWN = 5; // ticks

    private final Player player;

    private int invulnerableTicks;
    private int score;

    public PlayerShip(Player player, Vector2f position) {
        super(position, new Vector2f(0.0f, 0.0f));
        this.player = player;
        this.invulnerableTicks = 80;

        setBulletSpread((float) Math.toRadians(10.0f));
        setShootingCooldown(SHOOTING_COOLDOWN);
        setProjectileColor(Colors.WHITE);
        setProjectileSpeed(2.0f);
    }

    // Called from Bullet
    public void onKillEntity(GameObject entity) {
        score += (entity instanceof EnemyShip) ? 5 : 1;
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);

        player.sendActionBar(ChatColor.GREEN + "Score: " + score);
    }

    @Override
    public void die(GameObject killer, Game game) {
        super.die(killer, game);
        game.removePlayer(this.player);
    }

    @Override
    protected void onTick(Game game) {
        super.onTick(game);

        this.position = new Vector2f(
                position.x < 0.0f ? 0.0f : Math.min(position.x, game.getMapWidth()),
                position.y < 0.0f ? 0.0f : Math.min(position.y, game.getMapHeight())
        );

        if (invulnerableTicks == 0) {
            PolygonShape hitbox = getShape().getHitbox();
            for (GameObject entity : game.getAllEntities()) {
                if (!(entity instanceof Asteroid)) {
                    continue;
                }
                PolygonShape shape = ((Asteroid)entity).getShape();
                if (shape.intersects(hitbox, position.x - entity.position.x, position.y - entity.position.y)) {
                    die(entity, game);
                    break;
                }
            }
        } else {
            invulnerableTicks -= 1;
        }
    }

    @Override
    public void renderTo(Canvas canvas) {
        byte color = Colors.WHITE;

        if (invulnerableTicks > 0 && (invulnerableTicks / 3) % 2 == 0) {
            color = Colors.GRAY;
        }

        renderShapeTo(canvas, color);
    }

    @Override
    protected int getNumberOfBullets() {
        return (score > 150) ? 2 : 1;
    }
}
