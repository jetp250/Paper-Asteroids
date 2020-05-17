package me.jetp250.asteroids.game.entities;

import me.jetp250.asteroids.game.rendering.Canvas;
import me.jetp250.asteroids.util.PolygonShape;
import me.jetp250.asteroids.util.Vector2f;

public class ShipShape {
    private static final PolygonShape REFERENCE_SHAPE;

    private final PolygonShape shape;

    public ShipShape() {
        this.shape = REFERENCE_SHAPE.clone();
    }

    public PolygonShape getHitbox() {
        return shape;
    }

    public void renderTo(Canvas canvas, Vector2f position, boolean speeding, byte color) {
        Vector2f[] points = shape.getPoints();
        Vector2f A = points[0]; // Tip of the shape
        Vector2f B = points[1];
        Vector2f C = points[2];

        Vector2f AB = B.subtract(A);
        Vector2f AC = C.subtract(A);

        Vector2f D = A.add(AB);
        Vector2f E = A.add(AC);

        Vector2f D2 = A.add(AB.scale(0.7f));
        Vector2f E2 = A.add(AC.scale(0.7f));

        canvas.drawLine(A.x + position.x, A.y + position.y, D.x + position.x, D.y + position.y, color);
        canvas.drawLine(A.x + position.x, A.y + position.y, E.x + position.x, E.y + position.y, color);
        canvas.drawLine(D2.x + position.x, D2.y + position.y, E2.x + position.x, E2.y + position.y, color);

        if (speeding) {

            Vector2f F = D2.add(E2).scale(0.5f);
            Vector2f AF = F.subtract(A).normalized();
            Vector2f AFN = new Vector2f(-AF.y, AF.x);
            Vector2f G = F.add(AF);
            Vector2f H = G.add(AFN);
            Vector2f I = G.subtract(AFN);

            //Vector2f H = G.add(GD.scale(0.1f));
            //Vector2f I = G.add(GE.scale(0.1f));

            canvas.drawLine(F.x + position.x, F.y + position.y, H.x + position.x, H.y + position.y, color);
            canvas.drawLine(F.x + position.x, F.y + position.y, I.x + position.x, I.y + position.y, color);
        }
    }

    static {
        Vector2f[] points = {
                new Vector2f(-4.0f, 4.0f), // The front of the ship, index 0
                new Vector2f(4.0f, 0.0f),
                new Vector2f(0.0f, -4.0f)
        };
        REFERENCE_SHAPE = new PolygonShape(points);
    }

}
