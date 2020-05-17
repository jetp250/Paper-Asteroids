package me.jetp250.asteroids.game.rendering;

import java.util.Arrays;

public final class Canvas {
    private final byte[] colors;
    private final int width;

    private final int drawOffsetX;
    private final int drawOffsetY;

    public Canvas(int width, int height, int drawOffsetX, int drawOffsetY) {
        this.colors = new byte[width*height];
        this.width = width;
        this.drawOffsetX = drawOffsetX;
        this.drawOffsetY = drawOffsetY;
    }

    public void getPixels(int x1, int y1, int x2, int y2, byte[] buffer) {
        x1 += drawOffsetX;
        x2 += drawOffsetX;
        y1 += drawOffsetY;
        y2 += drawOffsetY;

        int areaWidth = x2 - x1;
        for (int y = y1; y < y2; ++y) {
            int srcPos = y * this.width + x1;
            int dstPos = (y - y1) * areaWidth;
            System.arraycopy(colors, srcPos, buffer, dstPos, areaWidth);
        }
    }

    public void fill(byte color) {
        Arrays.fill(colors, color);
    }

    public void setPixel(float x, float y, byte color) {
        setPixel(Math.round(x), Math.round(y), color);
    }

    public void setPixel(int x, int y, byte color) {
        colors[(x+drawOffsetX) + (y+drawOffsetY)*width] = color;
    }

    // The license requires the credit to be provided even in the compiled executable so here you go...
    @SuppressWarnings("unused")
    public static final String LINE_ALGO_CREDITS = "Po-Han @ http://www.edepot.com/linee.html";

    public void drawLine(float x1, float y1, float x2, float y2, byte color) {
        drawLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), color);
    }

    public void drawLine(int x1, int y1, int x2, int y2, byte color) {
        x1 += drawOffsetX;
        x2 += drawOffsetX;
        y1 += drawOffsetY;
        y2 += drawOffsetY;

        boolean yLonger = false;

        int shortLen = y2 - y1;
        int longLen = x2 - x1;

        if (Math.abs(shortLen) > Math.abs(longLen)) {
            yLonger=true;

            int swap = shortLen;
            shortLen = longLen;
            longLen = swap;
        }

        int decInc = (longLen == 0) ? 0 : (shortLen << 16) / longLen;

        if (yLonger) {
            if (longLen > 0) {
                longLen += y1;

                for (int j = 0x8000 + (x1 << 16); y1 <= longLen; ++y1, j += decInc) {
                    colors[(j >> 16) + y1 * width] = color;
                }
                return;
            }

            longLen += y1;
            for (int j = 0x8000 + (x1 << 16); y1 >= longLen; --y1, j -= decInc) {
                colors[(j >> 16) + y1 * width] = color;
            }
            return;
        }

        if (longLen > 0) {
            longLen += x1;
            for (int j = 0x8000 + (y1<<16); x1 <= longLen; ++x1, j += decInc) {
                colors[x1 + (j >> 16) * width] = color;
            }
            return;
        }
        longLen += x1;
        for (int j = 0x8000 + (y1 << 16); x1 >= longLen; --x1, j -= decInc) {
            colors[x1 + (j >> 16) * width] = color;
        }
    }
}
