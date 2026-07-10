package game.platform.types;

import game.Constants.kTile;
import game.GameManager;
import game.element.Clef;
import game.platform.SpritePlatform;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

/** A special sprite platform that plays a note upon collision */
public class Tile extends SpritePlatform {
    // tile data
    private final Clef clef;
    private final String note;
    private final AudioClip sound;

    private boolean on = false; // whether the Tile is triggered

    /**
     * Construct a Tile
     * @param x x position
     * @param y y position
     * @param clef The clef of note to use
     * @param note Lowercase fully-qualified two-character note identifier, i.e. a3
     */
    public Tile(
        double x, double y,
        Clef clef, String note
    ) {
        super(
            x, y,
            new Image(Tile.class.getResourceAsStream("/sprites/tiles/tile_" + note.charAt(0) + ".png"))
        );

        this.clef = clef;
        this.note = note;

        sound = new AudioClip(
            getClass().getResource(
                "/sfx/notes/" + clef.name().toLowerCase() + "_" + note + ".wav"
            ).toString()
        );
    }

    /** Get tile's clef */
    public Clef getClef() {
        return clef;
    }

    /** Get tile's note string */
    public String getNote() {
        return note;
    }

    /** Call when Tile should be triggered */
    public void trigger() { // RESTORED; BY SpaceCat321
        if (!on) { // no dupe check
            on = true;
            sound.play(GameManager.getSfxVolume());
        }
    }

    /** Call when Tile should end being triggered */
    public void release() { // RESTORED; BY SpaceCat321
        on = false;
    }

    /** Whether the Tile is triggered */
    public boolean isTriggered() {
        return on;
    }

    @Override
    public double getWidth() {
        return kTile.WIDTH;
    }

    @Override
    public double getHeight() {
        return kTile.HEIGHT;
    }
}