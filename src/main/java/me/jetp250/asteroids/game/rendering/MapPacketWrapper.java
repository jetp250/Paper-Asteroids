package me.jetp250.asteroids.game.rendering;

import net.minecraft.server.v1_15_R1.PacketPlayOutMap;

import java.lang.reflect.Field;
import java.util.Collections;

// Probably one of the ugliest classes in the project...
public final class MapPacketWrapper {
    private static final Field PIXEL_ARRAY_FIELD;

    private final PacketPlayOutMap packet;
    private final byte[] pixels; // reference to the pixel array stored in PacketPlayOutMap, size 128x128

    public MapPacketWrapper(int id) {
        this.packet = createPacket(id);
        byte[] pixels;
        try {
            pixels = (byte[]) PIXEL_ARRAY_FIELD.get(packet);
        } catch (IllegalAccessException impossible) {
            // Impossible on a regular Paper/Spigot server, because as long as version is correct, the field *will* exist.
            // Now if you're using some custom server software shenanigans... here's a beautiful error for you:
            impossible.printStackTrace();
            pixels = new byte[128*128];
        }
        this.pixels = pixels;
    }

    public PacketPlayOutMap getPacket() {
        return packet;
    }

    public byte[] getPixelsMutable() {
        return pixels;
    }

    private static PacketPlayOutMap createPacket(int id) {
        return new PacketPlayOutMap(
                id, // map ID
                (byte) 0, // scale: 0 for 1:1
                false, // false = don't display players and icons
                false, // false = not locked in cartography table
                Collections.emptyList(), // list of icons
                new byte[128*128], // source array
                0, // (source array width)-128
                0, // (source array height)-128
                128, // pixel array width
                128 // pixel array height
        );
    }

    static {
        Field pixelArrayField = null;
        try {
            pixelArrayField = PacketPlayOutMap.class.getDeclaredField("j");
        } catch (Exception impossible) {
            impossible.printStackTrace();
        }
        PIXEL_ARRAY_FIELD = pixelArrayField;
        PIXEL_ARRAY_FIELD.setAccessible(true);
    }

}
