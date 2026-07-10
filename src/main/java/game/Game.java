package game;

import game.screen.*;
import game.Constants.kWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game extends Application {
    private ScreenManager screenManager;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		if (getClass().getResource("/coconut.png") == null) { Platform.exit(); return; } // thanks matt

		// game.screens.Screen manager root
		screenManager = new ScreenManager(kWindow.WIDTH, kWindow.HEIGHT);

		// Single input handler attached to the scene so all screens can use it
		InputHandler input = new InputHandler();
		Scene scene = new Scene(screenManager.getRoot());
		input.attach(scene);

		// Create screens
        HomeScreen homeScreen = new HomeScreen(screenManager, kWindow.WIDTH, kWindow.HEIGHT);
        SettingsScreen settingsScreen = new SettingsScreen(screenManager, kWindow.WIDTH, kWindow.HEIGHT);
        GuideScreen guideScreen = new GuideScreen(screenManager, kWindow.WIDTH, kWindow.HEIGHT);
		GameScreen gameScreen = new GameScreen(screenManager, input, kWindow.WIDTH, kWindow.HEIGHT);
		EndScreen endScreen = new EndScreen(screenManager, kWindow.WIDTH, kWindow.HEIGHT);

		screenManager.addScreen(ScreenType.HOME, homeScreen);
        screenManager.addScreen(ScreenType.SETTINGS, settingsScreen);
        screenManager.addScreen(ScreenType.GUIDE, guideScreen);
		screenManager.addScreen(ScreenType.GAME, gameScreen);
		screenManager.addScreen(ScreenType.END, endScreen);

		screenManager.switchTo(ScreenType.HOME);

		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle("Up the Octave");
		stage.show();
	}

    @Override
    public void stop() { // clean exit suspending all resource-allocating objects
        screenManager.exitCurrent();
    }
}
