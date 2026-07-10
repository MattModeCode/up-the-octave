package game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private boolean left = false;
    private boolean right = false;
    private boolean jump = false;
    private boolean up = false;
    private boolean down = false;

    /**
     * @param scene target scene
     */
    public void attach(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.A || k == KeyCode.LEFT) left = true;
            if (k == KeyCode.D || k == KeyCode.RIGHT) right = true;
            if (k == KeyCode.SPACE || k == KeyCode.W || k == KeyCode.UP) jump = true;
            if (k == KeyCode.W || k == KeyCode.UP) up = true;
            if (k == KeyCode.S || k == KeyCode.DOWN) down = true;
        });

        scene.setOnKeyReleased(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.A || k == KeyCode.LEFT) left = false;
            if (k == KeyCode.D || k == KeyCode.RIGHT) right = false;
            if (k == KeyCode.SPACE || k == KeyCode.W || k == KeyCode.UP) jump = false;
            if (k == KeyCode.W || k == KeyCode.UP) up = false;
            if (k == KeyCode.S || k == KeyCode.DOWN) down = false;
        });
    }

    /** @return true when left pressed */
    public boolean isLeft() { return left; }

    /** @return true when right pressed */
    public boolean isRight() { return right; }

    /** @return true when jump pressed */
    public boolean isJump() { return jump; }

    /** @return true when up pressed */
    public boolean isUp() { return up; }

    /** @return true when down pressed */
    public boolean isDown() { return down; }
}
