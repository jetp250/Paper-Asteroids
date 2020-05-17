package me.jetp250.asteroids.util;

import com.google.common.base.Preconditions;
import me.jetp250.asteroids.game.rendering.Canvas;
import org.bukkit.Bukkit;

public final class PolygonShape {
    private final Vector2f[] points;
    private final float radius;

    public PolygonShape(Vector2f[] points) {
        Preconditions.checkArgument(points.length >= 3, "PolygonShape needs at least 3 points");
        this.points = points;
        this.radius = computeRadius(points);
    }

    public void renderTo(Canvas canvas, float xPos, float yPos, byte color) {
        Vector2f last = points[points.length-1];
        float lastX = last.x + xPos;
        float lastY = last.y + yPos;

        for (Vector2f vertex : points) {
            float x = vertex.x + xPos;
            float y = vertex.y + yPos;
            canvas.drawLine(lastX, lastY, x, y, color);

            lastX = x;
            lastY = y;
        }
    }

    public Vector2f[] getPoints() {
        return points;
    }

    public void rotate(float angleInRadians) {
        float cosAngle = (float) Math.cos(angleInRadians);
        float sinAngle = (float) Math.sin(angleInRadians);

        for (int i = 0; i < points.length; ++i) {
            Vector2f point = points[i];
            float rotatedX = point.x * cosAngle - point.y * sinAngle;
            float rotatedY = point.x * sinAngle + point.y * cosAngle;

            points[i] = new Vector2f(rotatedX, rotatedY);
        }
    }

    public boolean containsPoint(float x, float y) {
        if (isPointInRadius(x, y)) { // Do a cheap & rough cull operation before accurate check
            Bukkit.broadcastMessage("isPointInRadius check failed");
            return false;
        }

        Bukkit.broadcastMessage(String.format("Testing point %.2f, %.2f%n", x, y));

        // From https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
        boolean inside = false;
        for (int i = 0, j = points.length-1; i < points.length; j = i, i++ ) {
            Vector2f point1 = points[i];
            Vector2f point2 = points[j];
            
            if (((point1.y > y) != (point2.y > y)) &&
                    (x < (point2.x - point1.x) * (y - point1.y) / (point2.y - point1.y) + point1.x)){
                inside = !inside;
            }
        }
        return inside;
    }

    public boolean intersects(PolygonShape other, float xPos, float yPos) {
        float maxDistance = radius + other.radius;
        if (xPos*xPos + yPos*yPos > maxDistance*maxDistance) {
            return false;
        }

        Bukkit.broadcastMessage(String.format("DX: %.2f, DY: %.2f, R1: %.2f, R2: %.2f", xPos, yPos, radius, other.radius));
        //Bukkit.broadcastMessage("Testing for intersection. Distance: " + Math.sqrt(xPos*xPos + yPos*yPos));

        for (Vector2f point : other.points) {
            if (containsPoint(point.x - xPos, point.y - yPos)) {
                Bukkit.broadcastMessage("Colliding");
                return true;
            }
        }
        return false;
    }

    boolean intersect2(Vector2f segStart, Vector2f segEnd) {
        Vector2f prev = points[points.length-1];
        float a = segStart.x - prev.x;
        float b = segStart.y - prev.y;
        float c = segEnd.x - prev.x;
        float d = segEnd.y - prev.y;

        for (Vector2f point : points) {
            float e = segStart.x - point.x;
            float f = segStart.y - point.y;
            float g = segEnd.x - point.x;
            float h = segEnd.y - point.y;

            float i = point.x - prev.x;
            float j = point.y - prev.y;

            if (((d * a > b * c) != (h * e > f * g)) &&
                    ((b * i > j * a) != (d * i > j * c))) {
                return true;
            }
            prev = point;
            a = e;
            b = f;
            c = g;
            d = h;
        }
        return false;
    }

    public boolean edgeIntersectsWithSegment(Vector2f segStart, Vector2f segEnd) {
        if (!isPointInRadius(segStart.x, segStart.y) && !isPointInRadius(segEnd.x, segEnd.y)) {
            return false;
        }

        /*
        def ccw(A,B,C):
            return (C.y-A.y) * (B.x-A.x) > (B.y-A.y) * (C.x-A.x)

        # Return true if line segments AB and CD intersect
        def intersect(A,B,C,D):
            return ccw(A,C,D) != ccw(B,C,D) and ccw(A,B,C) != ccw(A,B,D)
        */

        Vector2f prev = points[points.length-1];
        float a = segStart.x - prev.x;
        float b = segStart.y - prev.y;
        float c = segEnd.x - prev.x;
        float d = segEnd.y - prev.y;

        for (Vector2f point : points) {
            float e = segStart.x - point.x;
            float f = segStart.y - point.y;
            float g = segEnd.x - point.x;
            float h = segEnd.y - point.y;

            float i = point.x - prev.x;
            float j = point.y - prev.y;

            if (((d * a > b * c) != (h * e > f * g)) &&
                    ((b * i > j * a) != (d * i > j * c))) {
                return true;
            }
            prev = point;
            a = e;
            b = f;
            c = g;
            d = h;
        }
        return false;
    }

    private boolean isPointInRadius(float x, float y) {
        return x*x + y*y < radius*radius;
    }

    private static float computeRadius(Vector2f[] shape) {
        float maxDistance = 0.0f;
        for (Vector2f point : shape) {
            float distance = point.lengthSquared(); // Distance to origin (0,0)
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        return (float) Math.sqrt(maxDistance);
    }

    @Override
    public PolygonShape clone()  {
        return new PolygonShape(points.clone());
    }
}
