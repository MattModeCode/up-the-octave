package game;

import java.util.List;

/**
 * A class containing values for global use.
 * MODIFY THESE VALUES SPARINGLY.
 */
public class Constants {
    /** List of all possible treble notes in increasing order */
    public static final List<String> ORDERED_NOTES_TREBLE = List.of(
        "c5", "d5", "e5", "f5", "g5", "a5", "b5",
        "c6"
    );

    /** List of all possible alto notes in increasing order */
    public static final List<String> ORDERED_NOTES_ALTO = List.of(
        "g3", "a3", "b3",
        "c4", "d4", "e4", "f4", "g4"
    );

    /** List of all possible bass notes in increasing order */
    public static final List<String> ORDERED_NOTES_BASS = List.of(
        "b1",
        "c2", "d2", "e2", "f2", "g2", "a2", "b2"
    );

    /**
     * Values for the game window itself.
     */
    public static final class kWindow {
        // dimensions
        public static final int WIDTH = 900;
        public static final int HEIGHT = 600;
    }

    /**
     * Values for the player.
     */
    public static final class kPlayer {
        public static final int WIDTH = 30;
        public static final int HEIGHT = 46;

        public static final double VEL_MOVE = 300.0;
        public static final double VEL_JUMP = -600.0;

        public static final double GRAVITY = 1500.0;

        public static final double COYOTE_TIME = 0.12; // in seconds

        public static final double COLLISION_MARGIN = 4.0;
    }

    /**
     * Values for the level.
     */
    public static final class kLevel {
        public static final int BOTTOM_BORDER = 2000; // bottom
        public static final int[] SPAWNPOINT = {0, 0};

        public static final String[] KEYS = {"BASS", "ALTO", "TREBLE"}; // the 3 keys needed to complete game
    }

    /**
     * Values for the home screen.
     */
    public static final class kHomeScreen {
        // positions
        public static final int[] LOGO_POS = {232, 103};
        public static final int[] BUTTON_PLAY_POS = {300, 266};
        public static final int[] BUTTON_SETTINGS_POS = {300, 387};
        public static final int[] BUTTON_GUIDE_POS = {300, 486};
        public static final int[] VERSION_TEXT_POS = {30, 580};

        public static final String VERSION_TEXT = "v1.0";

        public static final int VERSION_TEXT_SIZE = 18; // font pt
    }

    /** Values for the settings screen */
    public static final class kSettingsScreen {
        public static final double[] BUTTON_BACK_POS = {100, 69};

        public static final double[] SLIDER_MUSIC_POS = {503, 242};
        public static final double[] SLIDER_SFX_POS = {503, 340};

        public static final double SLIDER_WIDTH = 294;
    }

    /** Values for the guide screen */
    public static final class kGuideScreen {
        public static final double[] BUTTON_BACK_POS = {100, 69};
    }

    /** Values for the game screen */
    public static final class kGameScreen {
        public static final double[] TEXT_DEATHS_POS = {700, 30};
        public static final double[] TEXT_TIMER_POS = {800, 30};
        public static final double[] KEY_BRACKET_POS = {860, 195};
        public static final double[] KEY_TREBLE_POS = {830, 168};
        public static final double[] KEY_ALTO_POS = {830, 285};
        public static final double[] KEY_BASS_POS = {830, 390};

        public static final double CHART_POS_Y = 90;

        public static final double OFFSET_PLAYER_Y = 30;

        public static final int HUD_TEXT_SIZE = 21;

        public static final int CHART_THRESHOLD = 300;

        public static final String[] MELODY_TREBLE = {
            "1-e5", "1-e5", "2-e5", // jingle bells
            "1-e5", "1-e5", "2-e5",
            "1-e5", "1-g5", "1.5-c5", "0.5-d5", "1-e5"
        };
        public static final String[] MELODY_ALTO = {
            "2.5-e4", "1-a3", "4-e4", // from the start - laufey
            "1-e4", "1-d4", "1-c4", "1-b3",
            "1-d4", "1-c4", "1-b3", "1.5-a3"
        };
        public static final String[] MELODY_BASS = {
            "1.5-e2", "0.5-e2", "0.7-g2", "0.7-e2", "0.6-d2", "2-c2", "2-b1" // seven nation army
        };
    }

    /** Values for the end screen */
    public static final class kEndScreen {
        public static final double[] FINE_POS = {385, 80};
        public static final double[] TEXT_LABEL_POS = {300, 210};
        public static final double[] TEXT_VALUES_POS = {465, 210};
        public static final double[] BUTTON_PLAYAGAIN_POS = {300, 380};
        public static final double[] BUTTON_MENU_POS = {300, 480};

        public static final int TEXT_SIZE = 36; // pt

        public static final int BLUR_SIZE = 32; // gauss radius

        public static final String BG_OVERLAY_OPACITY = "bf"; // 75%
    }

    /** Values for the chart */
    public static final class kChart {
        public static final double WIDTH = 600;
        public static final double HEIGHT = 120;

        public static final double NOTE_WIDTH = 20;
        public static final double NOTE_HEIGHT = 16;

        public static final double PLAYHEAD_WIDTH = 2;
        public static final double PLAYHEAD_HEIGHT = 80;

        public static final double BG_CORNER_RADIUS = 20;

        public static final double MARGIN_X = 30;
        public static final double MARGIN_Y = 20;

        public static final double[] STAFF_POS = {80, 20};

        public static final double OFFSET_TREBLE_Y = 20;
        public static final double OFFSET_ALTO_Y = 38;
        public static final double OFFSET_BASS_Y = 40;

        public static final String COLOR_FILL = "#ffffffe5";
        public static final String COLOR_PLAYHEAD = "#5765ff";

        public static final double BPM = 30;
    }

    /** Values for the level editor */
    public static final class kEditor {
        public static final int UI_WIDTH = 300;
        public static final double PLACE_WIDTH = 160.0;
        public static final double PLACE_HEIGHT = 20.0;
        public static final double CAMERA_SPEED = 800.0;
        public static final double BOUNDS_BUFFER = 10000.0;

        public static final double SPAWN_MARKER_LENGTH = 5;
        public static final String SPAWN_MARKER_COLOR = "#ff0000"; // pure red
    }

    /** Miscellaneous values for UI */
    public static final class kUI {
        public static final String CUSTOM_BLACK = "#333333"; // MUST BE USED
        public static final String LIGHT = "#ffffff"; // just white
        public static final String ACCENT_LIGHT = "#ffaa80"; // the bright orange at 50% saturation
    }

    /**
     * Values for concrete platforms.
     */
    public static final class kConcrete {
        public static final String COLOR = "#849799"; // platform color

        public static final double CORNER_RADIUS = 4.0;
    }

    /** Values for clef marker platforms. */
    public static final class kClefMarker {
        public static final double WIDTH = 20;
        public static final double HEIGHT = 20;

        public static final String COLOR_FILL = "#00000000"; // completely transparent
        public static final String COLOR_STROKE = "#0099CC7F";
    }

    /**
     * Values specifically for blinking parkour platforms.
     */
    public static final class kBlock {
        public static final double STUB_WIDTH = 20;
        public static final double STUB_HEIGHT = 12;

        public static final double SHORT_WIDTH = 80;
        public static final double SHORT_HEIGHT = 20;

        public static final double LONG_WIDTH = 160;
        public static final double LONG_HEIGHT = 20;

        public static final double VERTICAL_WIDTH = 20;
        public static final double VERTICAL_HEIGHT = 200;
    }

    /**
     * Values specifically for music platforms.
     */
    public static final class kTile {
        public static final double WIDTH = 80;
        public static final double HEIGHT = 20;
    }

    /** Values specifically for exits. */
    public static final class kExit {
        public static final double WIDTH = 40;
        public static final double HEIGHT = 60;
    }
}
