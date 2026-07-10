package game.screen;

import com.adonax.audiocue.AudioCue;
import game.Constants.kHomeScreen;
import game.Constants.kUI;
import game.GameManager;
import game.ScreenManager;
import game.element.SpriteButton;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.InputStream;

// Home screen UI
public class HomeScreen implements Screen {
    private final Pane root = new Pane();

    private AudioCue music;
    private int musicInst;

    public HomeScreen(ScreenManager manager, double width, double height) {
        root.setPrefSize(width, height);

        // images
        try (InputStream fontFile = getClass().getResourceAsStream("/fonts/Nepomuk-Italic.otf")) {
            root.getChildren().add(new ImageView( // display backdrop
                new Image(getClass().getResourceAsStream("/sprites/bg/bg_blank.png"))
            ));

            // logo
            ImageView logo = new ImageView( // get logo image first
                new Image(getClass().getResourceAsStream("/sprites/misc/logo.png"))
            );
            logo.relocate(kHomeScreen.LOGO_POS[0], kHomeScreen.LOGO_POS[1]);
            root.getChildren().add(logo);

            // buttons
            root.getChildren().add(new SpriteButton( // play
                kHomeScreen.BUTTON_PLAY_POS[0],
                kHomeScreen.BUTTON_PLAY_POS[1],
                "play",
                () -> manager.switchTo(ScreenType.GAME),
                false
            ));
            root.getChildren().add(new SpriteButton( // settings
                kHomeScreen.BUTTON_SETTINGS_POS[0],
                kHomeScreen.BUTTON_SETTINGS_POS[1],
                "settings",
                () -> manager.switchTo(ScreenType.SETTINGS),
                false
            ));
            root.getChildren().add(new SpriteButton( // guide
                kHomeScreen.BUTTON_GUIDE_POS[0],
                kHomeScreen.BUTTON_GUIDE_POS[1],
                "guide",
                () -> manager.switchTo(ScreenType.GUIDE),
                false
            ));

            // version text
            Text versionText = new Text(
                kHomeScreen.VERSION_TEXT_POS[0],
                kHomeScreen.VERSION_TEXT_POS[1],
                kHomeScreen.VERSION_TEXT
            );
            versionText.setFont(Font.loadFont(fontFile, kHomeScreen.VERSION_TEXT_SIZE));
            versionText.setFill(Color.web(kUI.CUSTOM_BLACK));
            root.getChildren().add(versionText);
        } catch (Exception ignored) {}

        // music
        try {
            music = AudioCue.makeStereoCue(
                getClass().getResource("/music/homescreen.wav"),
                1
            );
        } catch (Exception ignored) {}
    }

    @Override
    public Parent getRoot() { return root; }

    @Override
    public void onEnter() {
        root.requestFocus();

        try {
            music.open();
        } catch (Exception ignored) {}
        musicInst = music.play(GameManager.getMusicVolume());
        music.setLooping(musicInst, -1); // set indefinite looping
    }

    @Override
    public void onExit() {
        music.stop(musicInst);
        music.releaseInstance(musicInst);
        music.close();
    }
}
