package game;

import com.google.gson.*;
import game.Constants.kEditor;
import game.Constants.kWindow;
import game.Constants.kLevel;
import game.element.Clef;
import game.platform.RectPlatform;
import game.platform.types.*;
import game.screen.Screen;
import game.screen.ScreenType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Level editor application for creating and saving game maps.
 */
public class LevelEditor extends Application implements Screen {
    private final BorderPane root = new BorderPane();
    private final Pane world = new Pane();
    private final int gameWidth = kWindow.WIDTH;
    private final int gameHeight = kWindow.HEIGHT;
    private final int uiWidth = kEditor.UI_WIDTH;
    private final int width = gameWidth + uiWidth;
    private final int height = gameHeight;

    private enum Tool {PLACE_CONCRETE, PLACE_BLOCK, PLACE_TILE, PLACE_EXIT, PLACE_ROOM }
    private Tool currentTool = Tool.PLACE_CONCRETE;

    private double placeWidth = kEditor.PLACE_WIDTH;
    private double placeHeight = kEditor.PLACE_HEIGHT;
    private String selectedNote = "c4";
    private Block.BlockType selectedBlockType = Block.BlockType.STUB;
    private Clef selectedClef = Clef.TREBLE;
    private boolean isBlockInverted = false; 

    private final Map<Clef, List<String>> noteOptions = Map.of(
        Clef.TREBLE, Constants.ORDERED_NOTES_TREBLE,
        Clef.ALTO, Constants.ORDERED_NOTES_ALTO,
        Clef.BASS, Constants.ORDERED_NOTES_BASS
    );

    private double cameraX = 0;
    private double cameraY = 0;
    private final double CAMERA_SPEED = kEditor.CAMERA_SPEED;

    private final List<javafx.scene.Node> placedPlatforms = new ArrayList<>();
    private Rectangle boundsRect = null;

    /**
     * A wrapper class to store the initial inversion state of a Block,
     * ensuring the editor saves the placement intent.
     */
    private static class EditorBlock extends Block {
        private final boolean initialInverted;

        public EditorBlock(int x, int y, BlockType type, boolean inverted) {
            super(x, y, type, inverted);
            this.initialInverted = inverted;
        }

        public boolean isInitialInverted() {
            return initialInverted;
        }
    }

    /**
     * Calculates the minimum bounding box encompassing all placed platforms 
     * and updates the {@code boundsRect} to visually represent it.
     */
    private void ensureWorldBounds() {
        double buffer = kEditor.BOUNDS_BUFFER;
        double minX = 0; double minY = 0; double maxX = gameWidth; double maxY = gameHeight;
        
        if (!placedPlatforms.isEmpty()) {
            minX = Double.POSITIVE_INFINITY; minY = Double.POSITIVE_INFINITY;
            maxX = Double.NEGATIVE_INFINITY; maxY = Double.NEGATIVE_INFINITY;
            for (var n : placedPlatforms) {
                if (n instanceof RectPlatform p) {
                    double px = p.getX();
                    double py = p.getY();
                    double pw = p.getWidth();
                    double ph = p.getHeight();
                    minX = Math.min(minX, px);
                    minY = Math.min(minY, py);
                    maxX = Math.max(maxX, px + pw);
                    maxY = Math.max(maxY, py + ph);
                } else {
                    // Unified bound calculation for all other Node types
                    minX = Math.min(minX, n.getBoundsInLocal().getMinX());
                    minY = Math.min(minY, n.getBoundsInLocal().getMinY());
                    maxX = Math.max(maxX, n.getBoundsInLocal().getMaxX());
                    maxY = Math.max(maxY, n.getBoundsInLocal().getMaxY());
                }
            }
        }
        
        if (buffer == 0) buffer = 200.0; 

        double rx = minX - buffer;
        double ry = minY - buffer;
        double rw = (maxX - minX) + buffer * 2.0;
        double rh = (maxY - minY) + buffer * 2.0;
        
        if (boundsRect == null) {
            boundsRect = new Rectangle(rx, ry, Math.max(1, rw), Math.max(1, rh));
            boundsRect.setFill(Color.TRANSPARENT);
            boundsRect.setMouseTransparent(true);
            world.getChildren().add(0, boundsRect);
        } else {
            boundsRect.setX(rx);
            boundsRect.setY(ry);
            boundsRect.setWidth(Math.max(1, rw));
            boundsRect.setHeight(Math.max(1, rh));
        }
    }

    /**
     * Constructs the LevelEditor.
     */
    public LevelEditor() {
        root.setPrefSize(width, height);
        buildUI();
    }

    /**
     * The main entry point for the JavaFX application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Initializes the JavaFX stage, scene, input handling, and camera loop.
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        ScreenManager screenManager = new ScreenManager(width, height);
        screenManager.addScreen(ScreenType.EDITOR, this);
        screenManager.switchTo(ScreenType.EDITOR);

        Scene scene = new Scene(screenManager.getRoot());

        // Camera movement control fields (used in timer, no need for separate booleans)
        final double[] vx = {0};
        final double[] vy = {0};

        scene.setOnKeyPressed(ev -> {
            switch (ev.getCode()) {
                case W, UP -> vy[0] = -CAMERA_SPEED;
                case A, LEFT -> vx[0] = -CAMERA_SPEED;
                case S, DOWN -> vy[0] = CAMERA_SPEED;
                case D, RIGHT -> vx[0] = CAMERA_SPEED;
                default -> {}
            }
        });

        scene.setOnKeyReleased(ev -> {
            switch (ev.getCode()) {
                case W, UP, S, DOWN -> vy[0] = 0;
                case A, LEFT, D, RIGHT -> vx[0] = 0;
                default -> {}
            }
        });

        try { loadMap(); } catch (IOException ex) { ex.printStackTrace(); }

        Rectangle spawnMarker = new Rectangle(
            kLevel.SPAWNPOINT[0], kLevel.SPAWNPOINT[1],
            kEditor.SPAWN_MARKER_LENGTH, kEditor.SPAWN_MARKER_LENGTH
        );
        spawnMarker.setFill(Color.web(kEditor.SPAWN_MARKER_COLOR));
        addPlatform(spawnMarker);

        final var timer = new javafx.animation.AnimationTimer() {
            private long last = 0;
            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                double dt = (now - last) / 1_000_000_000.0;
                last = now;

                cameraX += vx[0] * dt;
                cameraY += vy[0] * dt;

                world.setTranslateX(-cameraX);
                world.setTranslateY(-cameraY);
            }
        };
        timer.start();

        stage.setScene(scene);
        stage.setTitle("Level Editor");
        stage.show();
        stage.setOnCloseRequest(e -> saveMap());
        screenManager.getRoot().requestFocus();
    }

    /**
     * Builds the editor's graphical user interface (UI) and sets up event handlers.
     */
    private void buildUI() {
        VBox toolbar = new VBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #2b2b2b;");
        toolbar.setPrefWidth(uiWidth);

        Label title = new Label("Level Editor");
        title.setStyle("-fx-text-fill: #d9d9d9; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Tool Selection
        ToggleGroup tools = new ToggleGroup();
        RadioButton rbConcrete = new RadioButton("Concrete");
        rbConcrete.setToggleGroup(tools); rbConcrete.setSelected(true); rbConcrete.setStyle("-fx-text-fill: #d9d9d9;");
        RadioButton rbBlock = new RadioButton("Block");
        rbBlock.setToggleGroup(tools); rbBlock.setStyle("-fx-text-fill: #d9d9d9;");
        RadioButton rbTile = new RadioButton("Tile");
        rbTile.setToggleGroup(tools); rbTile.setStyle("-fx-text-fill: #d9d9d9;");
        RadioButton rbExit = new RadioButton("Exit");
        rbExit.setToggleGroup(tools); rbExit.setStyle("-fx-text-fill: #d9d9d9;");
        RadioButton rbRoom = new RadioButton("Room");
        rbRoom.setToggleGroup(tools); rbRoom.setStyle("-fx-text-fill: #d9d9d9;");

        rbConcrete.setOnAction(e -> currentTool = Tool.PLACE_CONCRETE);
        rbBlock.setOnAction(e -> currentTool = Tool.PLACE_BLOCK);
        rbTile.setOnAction(e -> currentTool = Tool.PLACE_TILE);
        rbExit.setOnAction(e -> currentTool = Tool.PLACE_EXIT);
        rbRoom.setOnAction(e -> currentTool = Tool.PLACE_ROOM);

        // Block Type Selection
        HBox blockRow = new HBox(6);
        Label blockLabel = new Label("Type:");
        blockLabel.setStyle("-fx-text-fill: #d9d9d9;");
        ComboBox<Block.BlockType> blockCombo = new ComboBox<>();
        blockCombo.getItems().addAll(Block.BlockType.values());
        blockCombo.setValue(selectedBlockType);
        blockCombo.setOnAction(e -> {
            selectedBlockType = blockCombo.getValue();
            switch (selectedBlockType) {
                case STUB -> { placeWidth = 20; placeHeight = 12; }
                case SHORT -> { placeWidth = 80; placeHeight = 20; }
                case LONG -> { placeWidth = 160; placeHeight = 20; }
                case VERTICAL -> { placeWidth = 20; placeHeight = 200; }
            }
        });
        blockRow.getChildren().addAll(blockLabel, blockCombo);
        
        // Inverted Block Toggle Button
        Button btnToggleInverted = new Button("Inverted: OFF");
        btnToggleInverted.setOnAction(e -> {
            isBlockInverted = !isBlockInverted;
            btnToggleInverted.setText("Inverted: " + (isBlockInverted ? "ON" : "OFF"));
        });

        // Tile/Music Note Selection
        HBox tileRow = new HBox(6);
        Label clefLabel = new Label("Clef:");
        clefLabel.setStyle("-fx-text-fill: #d9d9d9;");
        ComboBox<Clef> clefCombo = new ComboBox<>();
        clefCombo.getItems().addAll(Clef.values());
        clefCombo.setValue(selectedClef);
        ComboBox<String> noteCombo = new ComboBox<>();
        noteCombo.getItems().addAll(noteOptions.get(selectedClef));
        noteCombo.setValue(selectedNote);
        clefCombo.setOnAction(e -> {
            selectedClef = clefCombo.getValue();
            noteCombo.getItems().setAll(noteOptions.get(selectedClef));
            selectedNote = noteOptions.get(selectedClef).get(0);
            noteCombo.setValue(selectedNote);
        });
        noteCombo.setOnAction(e -> selectedNote = noteCombo.getValue());
        tileRow.getChildren().addAll(clefLabel, clefCombo, noteCombo);

        // Map Control Buttons
        Button btnSave = new Button("Save");
        Button btnClear = new Button("Clear All");

        btnSave.setOnAction(e -> saveMap());
        btnClear.setOnAction(e -> clearWorld());

        Label instructions = new Label("Left click: place\nDouble click/Right click: delete\nWASD/Arrows: move camera");
        instructions.setStyle("-fx-text-fill: #bfbfbf; -fx-font-size: 12px;");

        toolbar.getChildren().addAll(title, rbConcrete, rbBlock, btnToggleInverted, rbTile, rbExit, rbRoom, blockRow, tileRow, btnSave, btnClear, instructions);

        world.setStyle("-fx-background-color: transparent;");
        world.setPrefSize(gameWidth, gameHeight);

        // World click handler
        world.setOnMouseClicked(ev -> {
            Point2D wp = world.sceneToLocal(ev.getSceneX(), ev.getSceneY());
            double wx = wp.getX();
            double wy = wp.getY();
            
            // Delete element on right-click or double-click
            if (ev.getButton() == MouseButton.SECONDARY || ev.getClickCount() == 2) {
                for (int i = world.getChildren().size()-1; i>=0; i--) {
                    var n = world.getChildren().get(i);
                    if (n != boundsRect && n.getBoundsInParent().contains(wx, wy)) {
                        world.getChildren().remove(n);
                        placedPlatforms.remove(n);
                        ensureWorldBounds();
                        return;
                    }
                }
            } 
            // Place element on primary click
            else if (ev.getButton() == MouseButton.PRIMARY) {
                double px = wx;
                double py = wy;
                
                if (currentTool == Tool.PLACE_CONCRETE) {
                    Concrete p = new Concrete((int)px, (int)py, (int)placeWidth, (int)placeHeight);
                    addPlatform(p);
                } else if (currentTool == Tool.PLACE_BLOCK) {
                    // *** USING THE NEW WRAPPER CLASS ***
                    EditorBlock b = new EditorBlock((int)px, (int)py, selectedBlockType, isBlockInverted);
                    addPlatform(b);
                } else if (currentTool == Tool.PLACE_TILE) {
                    String noteStr = selectedNote;
                    Tile t = new Tile((int)px, (int)py, selectedClef, noteStr);
                    addPlatform(t);
                } else if (currentTool == Tool.PLACE_EXIT) {
                    Exit e = new Exit((int)px, (int)py);
                    addPlatform(e);
                } else if (currentTool == Tool.PLACE_ROOM) {
                    ClefMarker cmp = new ClefMarker((int)px, (int)py, selectedClef);
                    addPlatform(cmp);
                }
            }
        });

        root.setLeft(world);
        root.setRight(toolbar);
    }

    /**
     * Adds a platform node to the world and updates the world bounds.
     * @param n The platform node to add.
     */
    private void addPlatform(javafx.scene.Node n) {
        placedPlatforms.add(n);
        world.getChildren().add(n);
        ensureWorldBounds(); 
    }
    
    /**
     * Clears all placed platforms from the world and resets the bounds.
     */
    private void clearWorld() {
        world.getChildren().clear();
        placedPlatforms.clear();
        if (boundsRect != null) {
            boundsRect = null;
        }
        ensureWorldBounds();
    }

    /**
     * Saves all placed platforms to the {@code map.json} file.
     */
    private void saveMap() {
        JsonObject rootObj = new JsonObject();
        JsonArray concreteArr = new JsonArray();
        JsonArray blocksArr = new JsonArray();
        JsonArray tilesArr = new JsonArray();
        JsonArray roomsArr = new JsonArray();
        JsonArray exitsArr = new JsonArray();

        for (var n : placedPlatforms) {
            if (n instanceof EditorBlock eb) { // *** CHECKING FOR EDITORBLOCK ***
                JsonObject o = new JsonObject();
                o.addProperty("x", (int)eb.getX());
                o.addProperty("y", (int)eb.getY());
                
                // Determine block type based on image size (assuming Block handles dimensions)
                int w = (int)eb.getImage().getWidth();
                int h = (int)eb.getImage().getHeight();
                Block.BlockType type;
                if (w == 20 && h == 12) type = Block.BlockType.STUB;
                else if (w == 80 && h == 20) type = Block.BlockType.SHORT;
                else if (w == 160 && h == 20) type = Block.BlockType.LONG;
                else if (w == 20 && h == 200) type = Block.BlockType.VERTICAL;
                else type = Block.BlockType.STUB;
                
                o.addProperty("type", type.toString());
                // *** DIRECTLY SAVING THE EDITOR'S INTENDED STATE ***
                o.addProperty("inverted", eb.isInitialInverted()); 
                blocksArr.add(o);
            } else if (n instanceof Tile mp) {
                JsonObject o = new JsonObject();
                o.addProperty("x", (int)mp.getX());
                o.addProperty("y", (int)mp.getY());
                String clef = mp.getClef().name();
                String note = mp.getNote();
                o.addProperty("clef", clef);
                o.addProperty("note", note);
                tilesArr.add(o);
            } else if (n instanceof ClefMarker cmp) {
                JsonObject o = new JsonObject();
                o.addProperty("x", (int)cmp.getX());
                o.addProperty("y", (int)cmp.getY());
                o.addProperty("type", cmp.getRoomType().name());
                roomsArr.add(o);
            } else if (n instanceof Exit d) {
                JsonObject o = new JsonObject();
                o.addProperty("x", (int)d.getX());
                o.addProperty("y", (int)d.getY());
                exitsArr.add(o);
            } else if (n instanceof RectPlatform p) {
                JsonObject o = new JsonObject();
                o.addProperty("x", (int)p.getX());
                o.addProperty("y", (int)p.getY());
                o.addProperty("width", (int)p.getWidth());
                o.addProperty("height", (int)p.getHeight());
                concreteArr.add(o);
            }
        }

        rootObj.add("concrete", concreteArr);
        rootObj.add("blocks", blocksArr);
        rootObj.add("tiles", tilesArr);
        rootObj.add("rooms", roomsArr);
        rootObj.add("exits", exitsArr);

        try {
            Path out = Path.of("src/main/resources/map.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.writeString(out, gson.toJson(rootObj));
            System.out.println("Saved map.json to " + out.toAbsolutePath());
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    /**
     * Loads platforms from the {@code map.json} file into the editor.
     * @throws IOException If an error occurs reading the map file.
     */
    private void loadMap() throws IOException {
        Path p = Path.of("src/main/resources/map.json");
        if (!Files.exists(p)) return;
        String json = Files.readString(p);
        JsonObject branch = JsonParser.parseString(json).getAsJsonObject();

        clearWorld();

        JsonArray concreteArr = branch.has("concrete") ? branch.getAsJsonArray("concrete") : new JsonArray();
        for (var el : concreteArr) {
            var obj = el.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            int w = obj.get("width").getAsInt();
            int h = obj.get("height").getAsInt();
            Concrete p2 = new Concrete(x, y, w, h);
            addPlatform(p2);
        }

        JsonArray blocksArr = branch.has("blocks") ? branch.getAsJsonArray("blocks") : new JsonArray();
        for (var el : blocksArr) {
            var obj = el.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            String typeStr = obj.get("type").getAsString();
            Block.BlockType type = Block.BlockType.valueOf(typeStr);
            // Load the inverted state
            boolean inverted = obj.has("inverted") && obj.get("inverted").getAsBoolean();
            // *** LOADING BACK INTO THE WRAPPER CLASS ***
            EditorBlock eb = new EditorBlock(x, y, type, inverted); 
            addPlatform(eb);
        }

        JsonArray tilesArr = branch.has("tiles") ? branch.getAsJsonArray("tiles") : new JsonArray();
        for (var el : tilesArr) {
            var obj = el.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            String clef = obj.get("clef").getAsString();
            String note = obj.get("note").getAsString();
            Clef clefEnum = Clef.valueOf(clef.toUpperCase());
            Tile mp = new Tile(x, y, clefEnum, note);
            addPlatform(mp);
        }

        JsonArray exitsArr = branch.has("exits") ? branch.getAsJsonArray("exits") : new JsonArray();
        for (var el : exitsArr) {
            var obj = el.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            Exit d = new Exit(x, y);
            addPlatform(d);
        }

        JsonArray roomsArr = branch.has("rooms") ? branch.getAsJsonArray("rooms") : new JsonArray();
        for (var el : roomsArr) {
            var obj = el.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            String typeStr = obj.get("type").getAsString();
            Clef clef = Clef.valueOf(typeStr.toUpperCase());
            ClefMarker cmp = new ClefMarker(x, y, clef);
            addPlatform(cmp);
        }
    }

    /**
     * Gets the root node of the scene.
     * @return The root {@code Parent} node.
     */
    @Override
    public Parent getRoot() { return root; }

    /**
     * Called when the editor screen becomes active. Requests focus for the root node.
     */
    @Override
    public void onEnter() { root.requestFocus(); }

    /**
     * Called when the editor screen becomes inactive.
     */
    @Override
    public void onExit() {}
}