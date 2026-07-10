package game.screen;

import game.GameManager;
import game.ScreenManager;
import game.Constants.kWindow;
import game.Constants.kEndScreen;
import game.Constants.kUI;
import game.element.SpriteButton;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/** Simple end-game screen shown when player reaches a door. */
public class EndScreen implements Screen {
    private final Pane root = new Pane();

    private ImageView bg;
    private Text values; // numbers next to labels

    private AudioClip sfxComplete;

    public EndScreen(ScreenManager manager, double width, double height) {
        root.setPrefSize(width, height);

        // bg
        bg = new ImageView();
        root.getChildren().add(bg);
        root.getChildren().add(new Rectangle( // shaded overlay
            kWindow.WIDTH, kWindow.HEIGHT,
            Color.web(kUI.CUSTOM_BLACK + kEndScreen.BG_OVERLAY_OPACITY)
        ));

        // fine
        ImageView fine = new ImageView(new Image(
            getClass().getResourceAsStream("/sprites/misc/fine.png")
        ));
        fine.setX(kEndScreen.FINE_POS[0]);
        fine.setY(kEndScreen.FINE_POS[1]);
        root.getChildren().add(fine);

        // labels
        Text labels = new Text(
            kEndScreen.TEXT_LABEL_POS[0],
            kEndScreen.TEXT_LABEL_POS[1],
            "    deaths\n        time\n\nbest time"
        );
        labels.setFont(Font.loadFont(
            getClass().getResourceAsStream("/fonts/Nepomuk-Regular.otf"),
            kEndScreen.TEXT_SIZE
        ));
        labels.setFill(Color.web(kUI.LIGHT));
        root.getChildren().add(labels);

        values = new Text();
        values.setX(kEndScreen.TEXT_VALUES_POS[0]);
        values.setY(kEndScreen.TEXT_VALUES_POS[1]);
        values.setFont(Font.loadFont(
            getClass().getResourceAsStream("/fonts/Nepomuk-Regular.otf"),
            kEndScreen.TEXT_SIZE
        ));
        values.setFill(Color.web(kUI.ACCENT_LIGHT));
        root.getChildren().add(values);

        // buttons
        root.getChildren().add(new SpriteButton( // play again
            kEndScreen.BUTTON_PLAYAGAIN_POS[0],
            kEndScreen.BUTTON_PLAYAGAIN_POS[1],
            "playagain",
            () -> manager.switchTo(ScreenType.GAME),
            true
        ));
        root.getChildren().add(new SpriteButton( // menu
            kEndScreen.BUTTON_MENU_POS[0],
            kEndScreen.BUTTON_MENU_POS[1],
            "menu",
            () -> manager.switchTo(ScreenType.HOME),
            true
        ));

        // sfx
        sfxComplete = new AudioClip(
            getClass().getResource("/sfx/complete.wav").toString()
        );
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    @Override
    public void onEnter() {
        // update text values
        values.setText(
            String.format(
                "%d\n%d:%02d\n\n%d:%02d",
                GameManager.getDeaths(),
                GameManager.getCurrentTime() / 60, GameManager.getCurrentTime() % 60,
                GameManager.getBestTime() / 60, GameManager.getBestTime() % 60
            )
        );

        // play sound
        sfxComplete.play(GameManager.getSfxVolume());

        root.requestFocus();
    }

    @Override
    public void supplyBackground(Image image) {
        bg.setImage(image);
    }
}
