package game.platform.types;

import game.platform.RectPlatform;
import game.Constants.kConcrete;
import javafx.scene.paint.Color;

/** A completely static rect platform */
public class Concrete extends RectPlatform {
    public Concrete(
        double x, double y,
        double width, double height
    ) {
        super(
            x, y,
            width, height,
            Color.web(kConcrete.COLOR)
        );

        setArcWidth(kConcrete.CORNER_RADIUS * 2);
        setArcHeight(kConcrete.CORNER_RADIUS * 2);
    }
}
