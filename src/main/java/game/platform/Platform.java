package game.platform;

import javafx.geometry.Bounds;

/**
 * Common interface for uniting Rectangle-based and ImageView-based
 * platforms' getters for geometry processing
 */
public interface Platform {
    double getX();

    double getY();

    double getWidth();

    double getHeight();

    Bounds getBoundsInParent();
}
