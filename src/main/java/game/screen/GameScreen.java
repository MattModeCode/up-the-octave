package game.screen;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueInstanceEvent.Type;
import com.adonax.audiocue.AudioCueListener;
import game.Constants.kGameScreen;
import game.Constants.kEndScreen;
import game.Constants.kLevel;
import game.Constants.kPlayer;
import game.Constants.kUI;
import game.Constants.kChart;
import game.GameManager;
import game.InputHandler;
import game.ScreenManager;
import game.element.*;
import game.platform.types.Block;
import game.platform.types.ClefMarker;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Main game loop screen
 */
public class GameScreen implements Screen {
    // root level things
    private final Pane root = new Pane();
    private final InputHandler input;
    private final ScreenManager manager;

    // elements
    private Pane world;
    private Pane hud;

    private Level level;
    private Player player;

    private Map<Clef, Chart> charts = new HashMap<>();
    private Map<Clef, Key> keys = new HashMap<>();

    private Text deathText;
    private Text timerText;

    private AnimationTimer timer; // global delta time counter

    // music and sfx
    private AudioCue musicIntro;
    private AudioCue musicMain;
    private AudioCue blockCue;

    private int musicIntroInst;
    private int musicMainInst;
    private int blockCueInst;

    // values
    private final int width;
    private final int height;

    private double cameraX = 0;
    private double cameraY = 0;

    private double elapsedTime = 0.0; // seconds since level start

    private boolean initialized = false;
    private boolean timerRunning = false;

    /** Create the GameScreen
     * @param manager A ScreenManager
     * @param input An InputHandler
     * @param width The window width
     * @param height The window height
     */
    public GameScreen(ScreenManager manager, InputHandler input, int width, int height) {
        this.manager = manager;
        this.input = input;
        this.width = width;
        this.height = height;

        root.setPrefSize(width, height);
    }

    /** Construct everything within the GameScreen, separate from constructor */
    private void init() {
        if (getClass().getResource("/coconut.png") == null) { Platform.exit(); return; }

        // add bg
        try(InputStream f = getClass().getResourceAsStream("/sprites/bg/bg_blank.png")) {
            root.getChildren().add(new ImageView(new Image(f)));
        } catch (Exception exception) {}

        // world
        world = new Pane();
        level = new Level();
        
        try {
            level.build(world); // attempt level creation
        } catch (Exception ignored) {}

        player = new Player( // create the player
            kLevel.SPAWNPOINT[0],
            kLevel.SPAWNPOINT[1],
            input
        );
        
        world.getChildren().add(player);
        root.getChildren().add(world); // add world

        // HUD
        hud = new Pane();
        hud.setPrefSize(width, height);

        Chart chartTreble = new Chart(Clef.TREBLE, kGameScreen.MELODY_TREBLE);
        chartTreble.setLayoutX((width - kChart.WIDTH) / 2);
        chartTreble.setLayoutY(kGameScreen.CHART_POS_Y);
        charts.put(Clef.TREBLE, chartTreble);
        hud.getChildren().add(chartTreble);

        Chart chartAlto = new Chart(Clef.ALTO, kGameScreen.MELODY_ALTO);
        chartAlto.setLayoutX((width - kChart.WIDTH) / 2);
        chartAlto.setLayoutY(kGameScreen.CHART_POS_Y);
        charts.put(Clef.ALTO, chartAlto);
        hud.getChildren().add(chartAlto);

        Chart chartBass = new Chart(Clef.BASS, kGameScreen.MELODY_BASS);
        chartBass.setLayoutX((width - kChart.WIDTH) / 2);
        chartBass.setLayoutY(kGameScreen.CHART_POS_Y);
        charts.put(Clef.BASS, chartBass);
        hud.getChildren().add(chartBass);

        ImageView keyBracket = new ImageView();
        try (InputStream f = getClass().getResourceAsStream("/sprites/keys/keybracket.png")) {
            keyBracket.setImage(new Image(f));
        } catch (Exception ignored) {}
        keyBracket.setX(kGameScreen.KEY_BRACKET_POS[0]);
        keyBracket.setY(kGameScreen.KEY_BRACKET_POS[1]);
        hud.getChildren().add(keyBracket);

        Key keyTreble = new Key(Clef.TREBLE);
        keyTreble.setX(kGameScreen.KEY_TREBLE_POS[0]);
        keyTreble.setY(kGameScreen.KEY_TREBLE_POS[1]);
        keys.put(Clef.TREBLE, keyTreble);
        hud.getChildren().add(keyTreble);

        Key keyAlto = new Key(Clef.ALTO);
        keyAlto.setX(kGameScreen.KEY_ALTO_POS[0]);
        keyAlto.setY(kGameScreen.KEY_ALTO_POS[1]);
        keys.put(Clef.ALTO, keyAlto);
        hud.getChildren().add(keyAlto);

        Key keyBass = new Key(Clef.BASS);
        keyBass.setX(kGameScreen.KEY_BASS_POS[0]);
        keyBass.setY(kGameScreen.KEY_BASS_POS[1]);
        keys.put(Clef.BASS, keyBass);
        hud.getChildren().add(keyBass);

        deathText = new Text(
            kGameScreen.TEXT_DEATHS_POS[0],
            kGameScreen.TEXT_DEATHS_POS[1],
            "deaths\n0"
        );
        try (InputStream f = getClass().getResourceAsStream("/fonts/Nepomuk-Italic.otf")) {
            deathText.setFont(Font.loadFont(f, kGameScreen.HUD_TEXT_SIZE));
        } catch (Exception ignored) {}
        deathText.setFill(Color.web(kUI.CUSTOM_BLACK));
        hud.getChildren().add(deathText);

        timerText = new Text(
            kGameScreen.TEXT_TIMER_POS[0],
            kGameScreen.TEXT_TIMER_POS[1],
            "timer\n00:00"
        );
        try (InputStream f = getClass().getResourceAsStream("/fonts/Nepomuk-Italic.otf")) {
            timerText.setFont(Font.loadFont(f, kGameScreen.HUD_TEXT_SIZE));
        } catch (Exception ignored) {}
        timerText.setFill(Color.web(kUI.CUSTOM_BLACK));
        hud.getChildren().add(timerText);

        root.getChildren().add(hud);

        // setup global timer
        timer = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                double delta = (now - last) / 1_000_000_000.0;
                last = now;
                update(delta);
            }
        };

        // music
        try {
            musicIntro = AudioCue.makeStereoCue(
                getClass().getResource("/music/gameloop_intro.wav"),
                1
            );
            musicMain = AudioCue.makeStereoCue(
                getClass().getResource("/music/gameloop_main.wav"),
                1
            );
            blockCue = AudioCue.makeStereoCue(
                getClass().getResource("/music/gameloop_castanet.wav"),
                1
            );
        } catch (Exception ignored) {}

        AudioCueListener introListener = new AudioCueListener() {
            @Override
            public void audioCueOpened(long l, int i, int i1, AudioCue audioCue) {}

            @Override
            public void audioCueClosed(long l, AudioCue audioCue) {}

            @Override
            public void instanceEventOccurred(AudioCueInstanceEvent audioCueInstanceEvent) {
                if (audioCueInstanceEvent.type == Type.STOP_INSTANCE) { // once intro finishes playing
                    musicMainInst = musicMain.play(GameManager.getMusicVolume());
                    musicMain.setLooping(musicMainInst, -1);
                }
            }
        };
        musicIntro.addAudioCueListener(introListener);

        AudioCueListener blockCueListener = new AudioCueListener() {
            @Override
            public void audioCueOpened(long l, int i, int i1, AudioCue audioCue) {}

            @Override
            public void audioCueClosed(long l, AudioCue audioCue) {}

            @Override
            public void instanceEventOccurred(AudioCueInstanceEvent audioCueInstanceEvent) {
                if (audioCueInstanceEvent.type == Type.LOOP) { // on loop
                    Platform.runLater(() -> {
                        for (Block b : level.getBlocks()) b.toggle(); // toggle all Blocks
                    });
                }
            }
        };
        blockCue.addAudioCueListener(blockCueListener);

        // mark initialized so we don't double-init
        initialized = true;
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    @Override
    public void onEnter() {
        if (!initialized) init(); // run initialization

        // reset timer
        elapsedTime = 0.0;

        // set timer
        timerRunning = true;
        if (timer != null) timer.start();

        // begin playing intro music and looping block cue
        try {
            musicIntro.open();
            musicMain.open();
            blockCue.open();
        } catch (Exception ignored) {}

        musicIntroInst = musicIntro.play(GameManager.getMusicVolume());

        blockCueInst = blockCue.play(GameManager.getMusicVolume());
        blockCue.setLooping(blockCueInst, -1);

        root.requestFocus();
    }

    @Override
    public void onExit() {
        timerRunning = false;
        if (timer != null) timer.stop();

        if (musicIntro.getIsPlaying(musicIntroInst)) {
            musicIntro.stop(musicIntroInst);
            musicIntro.releaseInstance(musicIntroInst);
        }
        musicIntro.close();

        if (musicMain.getIsPlaying(musicMainInst)) {
            musicMain.stop(musicMainInst);
            musicMain.releaseInstance(musicMainInst);
        }
        musicMain.close();

        blockCue.stop(blockCueInst);
        blockCue.releaseInstance(blockCueInst);
        blockCue.close();

        // reset game screen (method moved)
        // elements
        world = null;
        hud = null;
        level = null;
        player = null;

        charts = new HashMap<>();
        keys = new HashMap<>();

        deathText = null;
        timerText = null;

        timer = null;

        // values
        cameraX = cameraY = 0;
        elapsedTime = 0.0;

        // make ready for another init
        initialized = false;
    }

    private void update(double dt) {
        if (timerRunning) elapsedTime += dt;
        int totalTime = (int) Math.floor(elapsedTime);

        // call player updater
        player.update(dt, level.getAllPlatforms());

        // THEN call updater for all charts
        for (Chart chart : charts.values()) {
            if (chart.isVisible() && !chart.isCompleted()) chart.update(dt, player.getCurrentTileNote());
        }

        // update HUD text elements
        if (timerText != null) timerText.setText(
            String.format("time\n%d:%02d", totalTime / 60, totalTime % 60) // minutes, seconds
        );
        if (deathText != null) deathText.setText("deaths\n" + player.getDeaths());

        // player death
        if (player.hasDied()) {
            // reset all Charts in the hope that current one is also reset
            for (Chart chart : charts.values()) chart.reset();

            return; // skip all processing
        }

        // camera location processing, follow player
        double playerCenterX = player.getX() + kPlayer.WIDTH / 2.0;
        double playerCenterY = player.getY() + kPlayer.HEIGHT / 2.0;

        cameraX += (playerCenterX - width / 2.0 - cameraX) / 10.0;
        cameraY += (playerCenterY - height / 2.0 - cameraY - kGameScreen.OFFSET_PLAYER_Y) / 10.0;

        world.setTranslateX(-cameraX);
        world.setTranslateY(-cameraY);

        // check exits (only finish if player has collected all keys)
        if (player.isAtExit() && player.hasAllKeys()) {
            // set game values
            GameManager.setDeaths(player.getDeaths());
            GameManager.setCurrentTime(totalTime);
            GameManager.setBestTime(
                GameManager.getBestTime() != 0 // as long as not first game
                    ? Math.min(GameManager.getBestTime(), totalTime) // either existing best time or new current time
                    : totalTime // set to current time no matter what
            );

            // switch screens
            if (manager != null) {
                ImageView temp = new ImageView(root.snapshot(null, null));
                temp.setEffect(new GaussianBlur(kEndScreen.BLUR_SIZE));

                manager.switchTo(ScreenType.END, temp.snapshot(null, null)); // provide current snapshot
            }

            // skip all remaining processing
            return;
        }

        // find nearest ClefMarker from player
        double clefNearestDistance = Double.MAX_VALUE;
        ClefMarker nearestClefMarker = null;

        for (ClefMarker marker: level.getMarkers()) {
            double roomCenterX  = marker.getX() + marker.getWidth() / 2;
            double roomCenterY = marker.getY() + marker.getHeight() / 2;

            double dx = playerCenterX - roomCenterX ; // horizontal difference between player and room block
            double dy = playerCenterY - roomCenterY ;
            double distance = Math.sqrt(dx * dx + dy * dy); // determines how close the player is to the room block

            if (distance < clefNearestDistance) { // find nearest room block
                clefNearestDistance = distance;
                nearestClefMarker = marker;
            }
        }

        // check proximity from nearest ClefMarker and show/hide correct chart
        if (nearestClefMarker != null && clefNearestDistance < kGameScreen.CHART_THRESHOLD) {
            Clef currClef = nearestClefMarker.getRoomType();

            if (!charts.get(currClef).isVisible()) {
                charts.get(currClef).show(); // display the chart if not displayed
            }

            if (charts.get(currClef).isCompleted()) { // on melody complete
                keys.get(currClef).collect(); // change key image
                player.obtainKey(currClef); // change internal player key status
            }
        } else { // player is not in room
            for (Chart chart : charts.values()) {
                if (!chart.isCompleted()) chart.reset(); // reset if incomplete
                chart.hide(); // hide and reset all, also hiding the correct one
            }
        }
    }
}