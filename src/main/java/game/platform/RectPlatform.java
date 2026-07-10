package game.platform;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** Non-instantiable class for Rectangle-based dynamic size platforms */
public abstract class RectPlatform extends Rectangle implements Platform {
    /**
     * Construct platform.
     * @param x x position
     * @param y y position
     * @param width platform width
     * @param height platform height
     * @param fillColor platform fill color
     */
    public RectPlatform(
        double x, double y,
        double width, double height,
        Color fillColor
    ) {
        super();

        // position
        setX(x);
        setY(y);

        // size
        setWidth(width);
        setHeight(height);

        setFill(fillColor);
    }
}
