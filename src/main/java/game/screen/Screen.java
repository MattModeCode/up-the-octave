package game.screen;

import javafx.scene.Parent;
import javafx.scene.image.Image;

public interface Screen {
    Parent getRoot();

    default void onEnter() {}

    default void onExit() {}

    default void supplyBackground(Image image) {}
}
