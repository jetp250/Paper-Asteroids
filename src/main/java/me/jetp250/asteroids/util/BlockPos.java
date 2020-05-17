package me.jetp250.asteroids.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class BlockPos {
    public final int x, y, z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        hash = 31 * hash + z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlockPos)) {
            return false;
        }
        BlockPos other = (BlockPos) obj;
        return x == other.x && y == other.y && z == other.z;
    }


    @Override
    public String toString() {
        return "BlockPos{ x: " + x + ", y: " + y + ", z: " + z + " }";
    }

    public static BlockPos min(BlockPos a, BlockPos b) {
        return new BlockPos(
                Math.min(a.x, b.x),
                Math.min(a.y, b.y),
                Math.min(a.z, b.z)
        );
    }

    public static BlockPos max(BlockPos a, BlockPos b) {
        return new BlockPos(
                Math.max(a.x, b.x),
                Math.max(a.y, b.y),
                Math.max(a.z, b.z)
        );
    }
}
