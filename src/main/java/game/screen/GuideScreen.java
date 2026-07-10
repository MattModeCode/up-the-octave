package game.screen;

import game.ScreenManager;
import game.element.SpriteButton;
import game.Constants.kGuideScreen;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/** A simple screen displaying the game guide */
public class GuideScreen implements Screen {
    private final Pane root = new Pane();

    public GuideScreen(ScreenManager manager, double width, double height) {
        root.setPrefSize(width, height);

        // bg
        root.getChildren().add(new ImageView(new Image(
            getClass().getResourceAsStream("/sprites/bg/bg_guide.png")
        )));

        // button
        root.getChildren().add(new SpriteButton(
            kGuideScreen.BUTTON_BACK_POS[0],
            kGuideScreen.BUTTON_BACK_POS[1],
            "back",
            () -> manager.switchTo(ScreenType.HOME),
            false
        ));
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
