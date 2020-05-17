package me.jetp250.asteroids.track.builder;

import me.jetp250.asteroids.AsteroidsPlugin;
import me.jetp250.asteroids.game.rendering.ItemFrameDisplay;
import me.jetp250.asteroids.track.Track;
import me.jetp250.asteroids.track.TrackIdCounter;
import me.jetp250.asteroids.util.BlockPos;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;

public final class TrackBuilder implements Listener {
    private static final Particle.DustOptions VALID_POSITION = new Particle.DustOptions(Color.LIME, 1.0f);
    private static final Particle.DustOptions INVALID_POSITION = new Particle.DustOptions(Color.RED, 1.0f);

    private static final Map<Player, TrackBuilder> CURRENTLY_BUILDING = new HashMap<>();

    private final Player creator;
    private final BukkitTask renderTask;

    private BlockPos cornerA;
    private BlockPos cornerB;
    private BlockFace facing;

    private TrackBuilder(AsteroidsPlugin plugin, Player creator) {
        this.creator = creator;

        this.renderTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAndRender, 0L, 1L);
    }

    public void updateAndRender() {
        if (cornerB == null) {
            renderCornerSelection();
        } else {
            renderSelectedArea(cornerA, cornerB);
        }
    }

    private void renderCornerSelection() {
        RayTraceResult result = creator.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
        if (result == null || result.getHitBlockFace() == null) {
            return;
        }
        BlockFace face = result.getHitBlockFace();

        if (cornerA == null) {
            if (result.getHitBlock() == null) {
                return;
            }
            Location cornerA = result.getHitBlock().getRelative(face).getLocation();
            Location particlePos = getParticleSpawnPos(cornerA, face);

            if (face == BlockFace.UP) {
                creator.spawnParticle(Particle.REDSTONE, particlePos, 1, VALID_POSITION);
                creator.sendActionBar(ChatColor.GREEN + "Select the first corner!");
            }
            else {
                creator.spawnParticle(Particle.REDSTONE, particlePos, 1, INVALID_POSITION);
                creator.sendActionBar(ChatColor.RED + "Point must be facing up!");
            }
            return;
        }

        Location particlePos1 = getParticleSpawnPos(cornerA.toLocation(creator.getWorld()), this.facing);
        creator.spawnParticle(Particle.REDSTONE, particlePos1, 1, VALID_POSITION);

        BlockPos cornerB = new BlockPos(result.getHitBlock().getRelative(face));

        if (face != BlockFace.UP) {
            creator.sendActionBar(ChatColor.RED + "Both points must be facing up!");
            Location particlePos2 = getParticleSpawnPos(cornerB.toLocation(creator.getWorld()), face);
            creator.spawnParticle(Particle.REDSTONE, particlePos2, 1, INVALID_POSITION);
            return;
        }

        creator.sendActionBar(ChatColor.GREEN + "Select the second corner!");

        if (!isValidCornerB(cornerB)) {
            creator.sendActionBar(ChatColor.RED + "Selection must be a flat plane!");
            Location particlePos2 = getParticleSpawnPos(cornerB.toLocation(creator.getWorld()), face);
            creator.spawnParticle(Particle.REDSTONE, particlePos2, 1, INVALID_POSITION);
            return;
        }

        Location particlePos2 = getParticleSpawnPos(cornerB.toLocation(creator.getWorld()), this.facing);
        creator.spawnParticle(Particle.REDSTONE, particlePos2, 1, VALID_POSITION);

        BlockPos cornerA = BlockPos.min(this.cornerA, cornerB);
        cornerB = BlockPos.max(this.cornerA, cornerB);
        renderSelectedArea(cornerA, cornerB);
    }

    private void renderSelectedArea(BlockPos cornerA, BlockPos cornerB) {
        int width = (cornerB.x - cornerA.x);
        int height = (cornerB.y - cornerA.y);
        int length = (cornerB.z - cornerA.z);
        for (int i = 0; i <= width; ++i) {
            for (int j = 0; j <= height; ++j) {
                for (int k = 0; k <= length; ++k) {
                    double particleX = cornerA.x + i + 0.5 - 0.5 * facing.getModX();
                    double particleY = cornerA.y + j + 0.5 - 0.5 * facing.getModY();
                    double particleZ = cornerA.z + k + 0.5 - 0.5 * facing.getModZ();
                    creator.spawnParticle(Particle.REDSTONE, particleX, particleY, particleZ, 1, VALID_POSITION);
                }
            }
        }
    }

    private Location getParticleSpawnPos(Location originalPos, BlockFace face) {
        return originalPos.add(face.getDirection().multiply(-0.5)).add(0.5, 0.5, 0.5);
    }

    private boolean isValidCornerB(BlockPos location) {
        int x = (location.x - cornerA.x) * facing.getModX();
        int y = (location.y - cornerA.y) * facing.getModY();
        int z = (location.z - cornerA.z) * facing.getModZ();
        return x == 0 && y == 0 && z == 0;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer() != this.creator) { // != is fine as long as the player doesn't relog
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked != null) {
            BlockPos location = new BlockPos(clicked.getRelative(event.getBlockFace()));

            if (event.getBlockFace() != BlockFace.UP) {
                creator.sendMessage(ChatColor.RED + "Point must be facing up!!");
            }
            else if (cornerA == null) {
                cornerA = location;
                facing = event.getBlockFace();
                creator.sendMessage(ChatColor.GREEN + "First corner set!");
            }
            else if (isValidCornerB(location)) {
                BlockPos tempA = this.cornerA;
                cornerA = BlockPos.min(cornerA, location);
                cornerB = BlockPos.max(tempA, location);
                creator.sendMessage(ChatColor.GREEN + "Selection created!");
                creator.sendActionBar(ChatColor.GREEN + "Selection created!");
            }
        }
        event.setCancelled(true); // prevent e.g. placing a block, pressing a button or modifying an armor stand..
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == this.creator) {
            unregisterAndStop(); // Cancel the process. Only Bukkit's event manager has a reference to this class.
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != this.creator) {
            return;
        }

        String trimmedLowercase = event.getMessage().trim().toLowerCase();
        if (!trimmedLowercase.equals("cancel") && !trimmedLowercase.equals("finish")) {
            return;
        }

        AsteroidsPlugin plugin = JavaPlugin.getPlugin(AsteroidsPlugin.class);
        Bukkit.getScheduler().runTask(plugin, () -> processChatMessageSync(trimmedLowercase));
    }

    private void processChatMessageSync(String message) {
        if (message.equals("cancel")) {
            creator.sendMessage(ChatColor.RED + "Cancelled");
            unregisterAndStop();
        }
        else if (message.equals("finish")) {
            finish();
        }
    }

    private void finish() {
        if (cornerB == null) {
            creator.sendMessage(ChatColor.RED + "Make a selection first!");
            return;
        }

        creator.sendMessage(ChatColor.GREEN + "Track created!");
        unregisterAndStop();

        AsteroidsPlugin plugin = JavaPlugin.getPlugin(AsteroidsPlugin.class);

        TrackIdCounter trackIdCounter = plugin.getTrackIdCounter();
        ItemFrameDisplay display = new ItemFrameDisplay(facing, cornerA, cornerB);

        plugin.getOrCreateGameManager(creator.getWorld()).addTrack(new Track(display, trackIdCounter));
    }

    private void unregisterAndStop() {
        PlayerInteractEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        renderTask.cancel();

        CURRENTLY_BUILDING.remove(this.creator);
    }

    public static void startCreatingTrack(Player creator) {
        TrackBuilder existing = CURRENTLY_BUILDING.remove(creator);
        if (existing != null) {
            existing.finish();
            return;
        }

        AsteroidsPlugin plugin = JavaPlugin.getPlugin(AsteroidsPlugin.class);

        TrackBuilder builder = new TrackBuilder(plugin, creator);
        Bukkit.getPluginManager().registerEvents(builder, plugin);

        CURRENTLY_BUILDING.put(creator, builder);
    }

}
