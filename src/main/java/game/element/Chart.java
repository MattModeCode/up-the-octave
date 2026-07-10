package game.element;

import game.Constants;
import game.Constants.kChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** A popup window holding the music puzzle itself, with the clef and notes */
public class Chart extends Pane {// chart things
    /** A holding class for just the notes that belong to the staff */
    private class Note extends ImageView {
        private Image noteImage;
        private Image restImage;

        /**
         * Constructs a Note
         * @param noteLetter Just the letter identifier of the note
         */
        private Note(char noteLetter) {
            super();

            try (
                InputStream fN = getClass().getResourceAsStream("/sprites/chart/note_" + noteLetter + ".png");
                InputStream fR = getClass().getResourceAsStream("/sprites/chart/rest.png")
            ) {
                noteImage = new Image(fN);
                restImage = new Image(fR);
            } catch (Exception ignored) {}

            setImage(noteImage); // set to note image by default
        }

        /** Set sprite to note */
        private void showNote() {
            setImage(noteImage);
        }

        /** Set sprite to rest */
        private void showRest() {
            setImage(restImage);
        }
    }

    // chart data
    private final ArrayList<Note> noteNodes = new ArrayList<>();
    private final Rectangle playhead;

    private final ArrayList<Double> durations = new ArrayList<>(); // just note durations in order
    private final ArrayList<String> noteStrings = new ArrayList<>(); // just fully-qualified notes in order

    private final double pVx; // velocity of playhead

    private int currNoteInd = 0; // index of current note node during progression

    private boolean completed = false; // flag whether chart was finished

    /**
     * Constructs a Chart, is initially set to be invisible
     * @param clef The clef to use
     * @param melody The melody in fully-qualified syntax (DURATION-FQNOTE)
     */
    public Chart(Clef clef, String[] melody) {
        super();
        setPrefSize(kChart.WIDTH, kChart.HEIGHT);
        setVisible(false);

        // background
        Rectangle bg = new Rectangle(kChart.WIDTH, kChart.HEIGHT, Color.web(kChart.COLOR_FILL));
        bg.setArcWidth(kChart.BG_CORNER_RADIUS * 2);
        bg.setArcHeight(kChart.BG_CORNER_RADIUS * 2);
        getChildren().add(bg);

        // clef
        ImageView clefImg = new ImageView();
        try (InputStream f = getClass().getResourceAsStream(
            "/sprites/chart/chart_clef_" + clef.name().toLowerCase() + ".png"
        )) {
            clefImg.setImage(new Image(f));
        } catch (Exception ignored) {}
        clefImg.setX(kChart.MARGIN_X);
        switch (clef) {
            case Clef.TREBLE -> clefImg.setY(kChart.OFFSET_TREBLE_Y);
            case Clef.ALTO -> clefImg.setY(kChart.OFFSET_ALTO_Y);
            case Clef.BASS -> clefImg.setY(kChart.OFFSET_BASS_Y);
        }
        getChildren().add(clefImg);

        // staff
        double staffWidth = kChart.WIDTH - kChart.STAFF_POS[0] - kChart.MARGIN_X;
        double staffHeight = kChart.HEIGHT - 2 * kChart.MARGIN_Y;

        Pane staff = new Pane(); // inner region just for notes
        staff.setPrefSize(staffWidth, staffHeight);
        staff.setLayoutX(kChart.STAFF_POS[0]);
        staff.setLayoutY(kChart.MARGIN_Y);
        getChildren().add(staff);

        // notes, belong to staff
        for (String mNote : melody) { // populate durations and notes arrays
            durations.add(Double.parseDouble(mNote.substring(0, mNote.length() - 3)));
            noteStrings.add(mNote.substring(mNote.length() - 2));
        }

        double totalDur = 0;
        for (double dur : durations) totalDur += dur;

        List<String> orderedNotes = new ArrayList<>(); // correct list of ordered notes
        switch (clef) {
            case Clef.TREBLE -> orderedNotes = Constants.ORDERED_NOTES_TREBLE;
            case Clef.ALTO -> orderedNotes = Constants.ORDERED_NOTES_ALTO;
            case Clef.BASS -> orderedNotes = Constants.ORDERED_NOTES_BASS;
        }

        int nRange; // last-note exclusive range of melody
        int nLowestInd = orderedNotes.size();
        int nHighestInd = 0;
        for (String note : noteStrings) { // find range iteratively
            int currInd = orderedNotes.indexOf(note);
            if (currInd < nLowestInd) nLowestInd = currInd;
            else if (currInd > nHighestInd) nHighestInd = currInd;
        }
        nRange = nHighestInd - nLowestInd;

        double nRegionX = staffWidth - kChart.NOTE_WIDTH; // don't count note image dimensions
        double nRegionY = staffHeight - kChart.NOTE_HEIGHT;

        double nBeatWidth = nRegionX / totalDur;
        double nUnitHeight = nRegionY / nRange;

        double nWidthCovered = 0;
        for (int i = 0; i < melody.length; i++) { // place each note onto staff
            Note note = new Note(noteStrings.get(i).charAt(0));

            note.setX(nWidthCovered); // all previous widths
            note.setY(nUnitHeight * (nHighestInd - orderedNotes.indexOf(noteStrings.get(i)))); // unit differences

            // add
            noteNodes.add(note);
            staff.getChildren().add(note);

            // increment total width by current note duration
            nWidthCovered += nBeatWidth * durations.get(i);
        }

        // playhead, belongs to staff
        playhead = new Rectangle(kChart.PLAYHEAD_WIDTH, kChart.PLAYHEAD_HEIGHT, Color.web(kChart.COLOR_PLAYHEAD));
        playhead.setArcWidth(kChart.PLAYHEAD_WIDTH); // entirely rounded caps
        playhead.setArcHeight(kChart.PLAYHEAD_WIDTH);
        playhead.setVisible(false);
        staff.getChildren().add(playhead);

        pVx = (kChart.BPM / 60) * nBeatWidth; // convert beats/min to pixels/s
    }

    /** Resets the Chart progress and hides the playhead */
    public void reset() {
        for (int i = 0; i < currNoteInd; i++) noteNodes.get(i).showNote();

        playhead.setVisible(false);
        playhead.setX(0);

        currNoteInd = 0;
    }

    /** Make the Chart visible */
    public void show() {
        setVisible(true);
    }

    /** Make the Chart invisible */
    public void hide() {
        setVisible(false);
    }

    /** Whether Chart has been completed */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Update Chart progress and status
     * @param dt Delta time in seconds
     * @param currentTileNote Note of current tile occupied by Player, empty if none
     */
    public void update(double dt, String currentTileNote) {
        if (!isVisible() || completed) return; // extra layer of protection to NOT update if not visible or chart complete

        if (playhead.isVisible()) { // processing when running
            if (playhead.getX() >= noteNodes.get(currNoteInd).getX()) reset(); // note overrun check
            else { // playhead is within tolerable range
                if (currentTileNote.isEmpty()) { // no note selected
                    playhead.setX(playhead.getX() + pVx * dt); // increment playhead according to velocity
                } else if (currentTileNote.equals(noteStrings.get(currNoteInd))) { // if on correct next tile
                    noteNodes.get(currNoteInd).showRest();
                    playhead.setX(noteNodes.get(currNoteInd).getX()); // jump playhead to note

                    currNoteInd += 1;

                    if (currNoteInd == noteNodes.size()) { // reached last note
                        // soft reset, keep notes showing rests
                        playhead.setVisible(false);
                        playhead.setX(0);

                        completed = true; // VERY IMPORTANT set flag
                    }
                } else reset(); // incorrect note
            }
        } else { // logic while not running
            if (currNoteInd != 0) currNoteInd = 0; // extra layer of insurance

            if (currentTileNote.equals(noteStrings.getFirst())) {
                noteNodes.getFirst().showRest();
                playhead.setVisible(true); // begin the chart

                currNoteInd += 1;
            }
        }
    }
}
