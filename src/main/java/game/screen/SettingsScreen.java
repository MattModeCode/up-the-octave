package game.screen;

import game.GameManager;
import game.ScreenManager;
import game.element.SpriteButton;
import game.Constants.kSettingsScreen;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/** A screen to change settings */
public class SettingsScreen implements Screen {
    private final Pane root = new Pane();

    public SettingsScreen(ScreenManager manager, double width, double height) {
        root.setPrefSize(width, height);

        // bg
        root.getChildren().add(new ImageView(new Image(
            getClass().getResourceAsStream("/sprites/bg/bg_settings.png")
        )));

        // buttons
        root.getChildren().add(new SpriteButton(
            kSettingsScreen.BUTTON_BACK_POS[0],
            kSettingsScreen.BUTTON_BACK_POS[1],
            "back",
            () -> manager.switchTo(ScreenType.HOME),
            false
        ));

        // sliders
        Slider sliderMusic = new Slider(0.0, 1.0, GameManager.getMusicVolume());
        sliderMusic.getStylesheets().add(
            getClass().getResource("/slider.css").toExternalForm()
        );
        sliderMusic.setLayoutX(kSettingsScreen.SLIDER_MUSIC_POS[0]);
        sliderMusic.setLayoutY(kSettingsScreen.SLIDER_MUSIC_POS[1]);
        sliderMusic.setPrefWidth(kSettingsScreen.SLIDER_WIDTH);
        sliderMusic.valueProperty().addListener(
            new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    GameManager.setMusicVolume(t1.doubleValue());
                }
            }
        );
        root.getChildren().add(sliderMusic);

        Slider sliderSfx = new Slider(0.0, 1.0, GameManager.getSfxVolume());
        sliderSfx.getStylesheets().add(
            getClass().getResource("/slider.css").toExternalForm()
        );
        sliderSfx.setLayoutX(kSettingsScreen.SLIDER_SFX_POS[0]);
        sliderSfx.setLayoutY(kSettingsScreen.SLIDER_SFX_POS[1]);
        sliderSfx.setPrefWidth(kSettingsScreen.SLIDER_WIDTH);
        sliderSfx.valueProperty().addListener(
            new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    GameManager.setSfxVolume(t1.doubleValue());
                }
            }
        );
        root.getChildren().add(sliderSfx);
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
