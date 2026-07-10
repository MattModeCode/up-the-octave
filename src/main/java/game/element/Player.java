package game.element;

import game.Constants.kLevel;
import game.Constants.kPlayer;
import game.InputHandler;
import game.platform.Platform;
import game.platform.types.Block;
import game.platform.types.Exit;
import game.platform.types.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends ImageView {
    private Image imgL;
    private Image imgR;

    private final InputHandler input;

    // player data
    private double vx = 0;
    private double vy = 0;
    private double coyoteTimer = 0.0;

    private int deaths = 0;

    private String currentTileNote; // note of current tile, empty if none

    private final Map<Clef, Boolean> keyStatus = new HashMap<>();

    private boolean died = false;
    private boolean atExit = false;

    /**
     * forget the name... constructor or smth
     * @param x start x
     * @param y start y
     * @param input input handler
     */
    public Player(int x, int y, InputHandler input) {
        super();

        try ( // define images
            InputStream fL = getClass().getResourceAsStream("/sprites/misc/player_l.png");
            InputStream fR = getClass().getResourceAsStream("/sprites/misc/player_r.png")
        ) {
            imgL = new Image(fL);
            imgR = new Image(fR);
        } catch (Exception ignored) {}

        setImage(imgR); // start facing right
        setX(x);
        setY(y);

        this.input = input;

        for (Clef c : Clef.values()) {
            keyStatus.put(c, false);
        }
    }

    /** Get number of deaths */
    public int getDeaths() {
        return deaths;
    }

    /** Set number of deaths */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Get note of the currently-collided-with tile
     * @return The fully-qualified note, or empty string if not at tile
     */
    public String getCurrentTileNote() {
        return currentTileNote;
    }

    /** Call when player obtains a key */
    public void obtainKey(Clef key) {
        keyStatus.put(key, true);
    }

    /** Return true when all required keys have been obtained. */
    public boolean hasAllKeys() {
        for (Boolean v : keyStatus.values()) {
            if (v == null || !v) return false; // if at any point
        }
        return true;
    }

    /** Has the player died */
    public boolean hasDied() {
        return died;
    }

    /** Is the player at an exit */
    public boolean isAtExit() {
        return atExit;
    }

    /**
     * Update player physics, death counter, and sprite image
     * @param dt delta time in seconds
     * @param platforms all platforms
     */
    public void update(double dt, List<Platform> platforms) {
        // init
        died = atExit = false;
        currentTileNote = "";

        boolean onGround = false;

        // velocities, sprite changes
        if (input.isLeft() && !input.isRight()) { // x vel left
            if (!getImage().equals(imgL)) setImage(imgL);
            vx = -kPlayer.VEL_MOVE;
        }
        else if (input.isRight() && !input.isLeft()) { // x vel right
            if (!getImage().equals(imgR)) setImage(imgR);
            vx = kPlayer.VEL_MOVE;
        }
        else vx = 0;
        vy += kPlayer.GRAVITY * dt; // y vel via gravity

        // previous values (current)
        double prevX = getX();
        double prevY = getY(); // also means previous top coordinate
        double prevBottom = prevY + kPlayer.HEIGHT;

        // tentatively move player to next positions, new positions to be processed below
        setX(prevX + vx * dt);
        setY(prevY + vy * dt);

        // x-axis collision processing
        for (Platform p : platforms) {
            if (p instanceof Block b && !b.isActive()) continue; // skip inactive blocks

            // if intersecting
            if (getBoundsInParent().intersects(p.getBoundsInParent())) {
                if (p instanceof Exit) { // check if intersecting with exit
                    atExit = true;
                    continue; // exits don't collide
                }

                // ORIGINAL X-AXIS COLLISION BY Crowvic; y-axis if statements merged
                // coordinates
                double pLeft = p.getX();
                double pRight = pLeft + p.getWidth();
                double pTop = p.getY();
                double pBottom = pTop + p.getHeight();

                if ( // either y-collision case already to be solved
                    prevBottom <= pTop + kPlayer.COLLISION_MARGIN ||
                    prevY >= pBottom - kPlayer.COLLISION_MARGIN
                ) continue;

                if (vx > 0) setX(pLeft - kPlayer.WIDTH); // player right + platform left
                else if (vx < 0) setX(pRight); // player left + platform right
                else setX(prevX);
                vx = 0;
            }
        }

        // y-axis collision processing
        for (Platform p : platforms) {
            if (p instanceof Block b && !b.isActive()) continue; // skip inactive blocks

            // if intersecting
            if (getBoundsInParent().intersects(p.getBoundsInParent())) {
                if (p instanceof Exit) { // check if intersecting with exit
                    atExit = true;
                    continue; // exits dont collide
                }

                // y-coordinates of current platform
                double pTop = p.getY();
                double pBottom = pTop + p.getHeight();

                // detection
                if (prevBottom <= pTop + kPlayer.COLLISION_MARGIN) { // player bottom + platform top
                    setY(pTop - kPlayer.HEIGHT);

                    vy = 0;
                    onGround = true;

                    if (p instanceof Tile t && !t.isTriggered()) { // check if colliding a Tile to flag ONLY on first trigger
                        t.trigger();
                        currentTileNote = t.getNote(); // store tile's note
                    }
                } else if (prevY >= pBottom - kPlayer.COLLISION_MARGIN) { // player top + platform bottom
                    setY(pBottom);

                    vy = 0;
                }
            } else { // if non-intersecting
                if (p instanceof Tile t && t.isTriggered()) { // not intersecting a just-triggered Tile
                    t.release();
                }
            }
        }

        // coyote timer processing
        if (onGround) coyoteTimer = kPlayer.COYOTE_TIME; // reset on ground
        else coyoteTimer = Math.max(0.0, coyoteTimer - dt); // count down until min of 0

        // process jump
        if (
            input.isJump() &&
            (onGround || coyoteTimer > 0.0) // allow when on ground or within coyote window
        ) {
            vy = kPlayer.VEL_JUMP; // override previous y vel
            coyoteTimer = 0.0; // consume coyote time

            onGround = false; // override previous onGround
        }

        // death processing
        if (getY() > kLevel.BOTTOM_BORDER) {
            // respawn
            setX(kLevel.SPAWNPOINT[0]);
            setY(kLevel.SPAWNPOINT[1]);

            // reset speed
            vx = vy = 0;

            // counting
            died = true;
            deaths++;
        }
    }
}