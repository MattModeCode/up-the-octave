package game;

import java.util.HashMap;
import java.util.Map;

import game.screen.ScreenType;
import game.screen.Screen;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 * Screen manager to handle switching between screens
 */
public class ScreenManager {
    private final StackPane root;

    // screen management
    private final Map<ScreenType, Screen> screens = new HashMap<>();
    private ScreenType currScreen = null;

    public ScreenManager(double width, double height) {
        root = new StackPane();
        root.setPrefSize(width, height);
    }

    /** Get root */
    public StackPane getRoot() {
        return root;
    }

    /** Add a screen to the manager */
    public void addScreen(ScreenType id, Screen screen) {
        screens.put(id, screen);
    }

    /** Return the Screen registered with the given ID, or null if none */
    public Screen getScreen(ScreenType id) {
        return screens.get(id);
    }

    /** Replace whole root with the requested screen */
    public void switchTo(ScreenType id) {
        Screen next = screens.get(id); // get next

        if (next == null) return; // check if next is invalid
        if (currScreen != null) {
            Screen current = screens.get(currScreen);
            if (current != null) current.onExit();
        }

        root.getChildren().clear();
        Parent p = next.getRoot();
        if (p != null) root.getChildren().add(p); // add next screen's root to manager's root
        next.onEnter();
        currScreen = id;
    }

    /** Replace whole root with the requested screen and send previous screen snapshot to next */
    public void switchTo(ScreenType id, Image previousScreen) {
        Screen next = screens.get(id); // get next

        if (next == null) return; // check if next is invalid
        if (currScreen != null) {
            Screen current = screens.get(currScreen);
            if (current != null) current.onExit();
        }

        root.getChildren().clear();
        Parent p = next.getRoot();
        if (p != null) root.getChildren().add(p); // add next screen's root to manager's root
        next.onEnter();
        next.supplyBackground(previousScreen); // give previous screen image to next
        currScreen = id;
    }

    /** Manually force an exit call on the current screen */
    public void exitCurrent() {
        screens.get(currScreen).onExit();
    }
}
