package game.element;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/** The displayed Key */
public class Key extends ImageView {
    private Image imgLocked;
    private Image imgCollected;

    /**
     * Create a Key
     * @param clef The associated Clef
     */
    public Key(Clef clef) {
        super();

        try (
            InputStream fL = getClass().getResourceAsStream("/sprites/keys/key_" + clef.name().toLowerCase() + "_locked.png");
            InputStream fC = getClass().getResourceAsStream("/sprites/keys/key_" + clef.name().toLowerCase() + ".png")
        ) {
            imgLocked = new Image(fL);
            imgCollected = new Image(fC);
        } catch (Exception ignored) {}

        setImage(imgLocked); // show locked by default
    }

    /** Called when this Key is collected */
    public void collect() {
        setImage(imgCollected);
    }
}
