package game.platform.types;

import game.element.Clef;
import game.Constants.kClefMarker;
import game.platform.RectPlatform;
import javafx.scene.paint.Color;

/**
 * Invisible room marker used to tag regions as a particular room (TREBLE/BASS/ALTO).
 * Visible in the editor but hidden at runtime by the Level builder.
 */
public class ClefMarker extends RectPlatform {
    private final Clef roomType;

    public ClefMarker(
        double x, double y,
        Clef roomType
    ) {
        super(x, y, kClefMarker.WIDTH, kClefMarker.HEIGHT, Color.web(kClefMarker.COLOR_FILL));

        setStroke(Color.web(kClefMarker.COLOR_STROKE));

        this.roomType = roomType;
    }

    public Clef getRoomType() {
        return roomType;
    }
}
