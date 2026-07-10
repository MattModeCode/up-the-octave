package game.platform.types;

import game.Constants.kExit;
import game.platform.SpritePlatform;
import javafx.scene.image.Image;

/** Simple door that the player can touch to finish the level. */
public class Exit extends SpritePlatform {
	public Exit(double x, double y) {
        super(
            x, y,
            new Image(Exit.class.getResourceAsStream("/sprites/misc/exit.png"))
        );
	}

    @Override
    public double getWidth() {
        return kExit.WIDTH;
    }

    @Override
    public double getHeight() {
        return kExit.HEIGHT;
    }
}
