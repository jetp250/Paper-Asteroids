package me.jetp250.asteroids.game.rendering;

import me.jetp250.asteroids.AsteroidsPlugin;
import me.jetp250.asteroids.util.BlockPos;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public final class ItemFrameDisplay {
    private final BlockFace facing;
    private final BlockPos minCorner;
    private final BlockPos maxCorner;

    private final int width;
    private final int height;

    private ItemFrame[] entities;
    private int[] mapIDs;

    public ItemFrameDisplay(BlockFace facing, BlockPos minCorner, BlockPos maxCorner) {
        this.facing = facing;
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;

        int[] widthHeight = computeWidthHeight(minCorner, maxCorner);
        this.width = widthHeight[0];
        this.height = widthHeight[1];
    }

    private int[] computeWidthHeight(BlockPos minCorner, BlockPos maxCorner) {
        int[] result = {1, 1};
        int index = 0;

        if (minCorner.x != maxCorner.x) {
            result[index++] = maxCorner.x - minCorner.x + 1;
        }
        if (minCorner.y != maxCorner.y) {
            result[index++] = maxCorner.y - minCorner.y + 1;
        }
        if (minCorner.z != maxCorner.z) {
            if (index == 2) {
                throw new IllegalArgumentException("cubical selection");
            }
            result[index] = maxCorner.z - minCorner.z + 1;
        }
        return result;
    }

    public void killItemFrames() {
        if (entities == null) {
            return;
        }
        for (ItemFrame entity : entities) {
            entity.remove();
        }
    }

    public void spawnItemFrames(World world, int trackID) {
        killItemFrames(); // avoid spawning twice
        int width = maxCorner.x - minCorner.x + 1;
        int height = maxCorner.y - minCorner.y + 1;
        int length = maxCorner.z - minCorner.z + 1;

        int numItemFrames = width*height*length;
        this.entities = new ItemFrame[numItemFrames];
        this.mapIDs = new int[numItemFrames];

        NamespacedKey trackTag = JavaPlugin.getPlugin(AsteroidsPlugin.class).getItemFrameTag();

        int entityIndex = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    Location location = new Location(world, minCorner.x + x, minCorner.y + y, minCorner.z + z);
                    ItemFrame entity = spawnItemFrame(location, facing);

                    entity.getPersistentDataContainer().set(trackTag, PersistentDataType.INTEGER, trackID);
                    entities[entityIndex] = entity;
                    mapIDs[entityIndex] = ((MapMeta)entity.getItem().getItemMeta()).getMapView().getId();

                    entityIndex++;
                }
            }
        }
    }

    private ItemFrame spawnItemFrame(Location location, BlockFace facing) {
        Location blockLocation = location.toBlockLocation();

        ItemFrame entity = location.getWorld().spawn(blockLocation, ItemFrame.class, frame -> {
            frame.setInvulnerable(true);
            frame.setFacingDirection(facing);

            ItemStack stack = new ItemStack(Material.FILLED_MAP);
            MapMeta meta = (MapMeta) stack.getItemMeta();
            meta.setMapView(Bukkit.createMap(location.getWorld()));
            stack.setItemMeta(meta);

            frame.setItem(stack);
        });

        // Because of a bug (I believe??), the 'entity' is not actually a valid handle, so.. workaround to get the real one.
        Collection<ItemFrame> nearby = entity.getWorld().getNearbyEntitiesByType(ItemFrame.class, location, 1.0);
        for (ItemFrame other : nearby) {
            double dsq = other.getLocation().toBlockLocation().distanceSquared(blockLocation);
            if (dsq < 0.001) { // Room for slight error due to fp precision
                return other;
            }
        }
        return entity;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Index is 'x + y * width'
    public int getMapID(int index) {
        return mapIDs[index];
    }

    public BlockPos getMinCorner() {
        return minCorner;
    }

    public BlockFace getFacing() {
        return facing;
    }
}
