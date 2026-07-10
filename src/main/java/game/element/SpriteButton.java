package game.element;

import game.GameManager;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;

/** A button with dynamic sprites for mouse hover and click states */
public class SpriteButton extends Button {
    // graphics
    private Image nor;
    private Image sel;
    private Image act;

    private ImageView graphic;

    // sfx
    private AudioClip sfxClick;

    /** Construct a Button
     * @param x x position
     * @param y y position
     * @param name The button name, used to determine the sprite file
     * @param action On-click action
     * @param light Whether the button is light
     */
    public SpriteButton(
        double x, double y,
        String name, Runnable action,
        boolean light
    ) {
        super();

        // create graphics
        graphic = new ImageView();

        nor = new Image(getClass().getResourceAsStream(
            light ? "/sprites/buttons/" + name + "_LIGHT_n.png" : "/sprites/buttons/" + name + "_n.png"
        ));
        sel = new Image(getClass().getResourceAsStream("/sprites/buttons/" + name + "_s.png"));
        act = new Image(getClass().getResourceAsStream("/sprites/buttons/" + name + "_a.png"));

        graphic.setImage(nor); // start with normal state

        setGraphic(graphic);
        setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        relocate(x, y);

        // sfx
        sfxClick = new AudioClip(
            getClass().getResource("/sfx/click.wav").toString()
        );

        // event handlers
        addEventHandler(MouseEvent.MOUSE_ENTERED, event -> graphic.setImage(sel));
        addEventHandler(MouseEvent.MOUSE_EXITED, event -> graphic.setImage(nor));
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> graphic.setImage(act));
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (contains(event.getX(), event.getY())) { // is mouse inside button
                graphic.setImage(sel);
                sfxClick.play(GameManager.getSfxVolume());

                action.run();
            } else {
                graphic.setImage(nor);
            }
        });
    }
}
