package game.platform;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** Non-instantiable class for Image-based static size platform */
public abstract class SpritePlatform extends ImageView implements Platform {
    /**
     * Construct a SpritePlat
     * @param x x position
     * @param y y position
     * @param image The sprite image to use
     */
    public SpritePlatform(
        double x, double y,
        Image image
    ) {
        super(image);

        // position
        setX(x);
        setY(y);
    }

    public abstract double getWidth();

    public abstract double getHeight();
}
