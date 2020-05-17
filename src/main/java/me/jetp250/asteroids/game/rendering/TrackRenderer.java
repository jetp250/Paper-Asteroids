package me.jetp250.asteroids.game.rendering;

import me.jetp250.asteroids.game.Game;
import me.jetp250.asteroids.game.entities.GameObject;
import me.jetp250.asteroids.track.Track;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class TrackRenderer {
    private final Track track;
    private final Canvas canvas;

    private final MapPacketWrapper[] packets;

    public TrackRenderer(Track track) {
        this.track = track;

        ItemFrameDisplay display = track.getDisplay();
        int widthInMaps = display.getWidth();
        int heightInMaps = display.getHeight();

        int pixelWidth = widthInMaps * 128 + 128; // some extra space for the offsets
        int pixelHeight = heightInMaps * 128 + 128;
        this.canvas = new Canvas(pixelWidth, pixelHeight, 64, 64);


        this.packets = new MapPacketWrapper[widthInMaps * heightInMaps];
        for (int i = 0; i < packets.length; ++i) {
            int mapID = display.getMapID(i);
            packets[i] = new MapPacketWrapper(mapID);
        }
    }

    int frameCount = 0;

    public void render(Game game, @Nullable Collection<? extends Player> viewers) {
        if (viewers == null) {
            viewers = Bukkit.getOnlinePlayers();
        }
        PlayerConnection[] connections = viewers.stream()
                .map(p -> ((CraftPlayer)p).getHandle().playerConnection)
                .toArray(PlayerConnection[]::new);

        List<GameObject> entities = game.getAllEntities();

        frameCount++;

        canvas.fill(Colors.BLACK);
        for (GameObject entity : entities) {
            entity.renderTo(canvas);
        }

        ItemFrameDisplay display = track.getDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        int packetIndex = 0;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                MapPacketWrapper packet = packets[packetIndex++];
                canvas.getPixels(i * 128, j * 128, (i+1)*128, (j+1)*128, packet.getPixelsMutable());

                for (PlayerConnection connection : connections) {
                    connection.sendPacket(packet.getPacket());
                }
            }
        }
    }

}
