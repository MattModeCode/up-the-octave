package game.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.platform.Platform;
import game.platform.types.*;
import game.platform.types.Block.BlockType;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private final List<Block> blocks = new ArrayList<>();

    // compound of all visible platforms
    private final List<Platform> platforms = new ArrayList<>();

    // separate array of clef markers
    private final List<ClefMarker> markers = new ArrayList<>();

    /**
     * Build platforms and add to root.
     * @param root scene root pane
     */
    public void build(Pane root) throws IOException {
        // compound of all visible platforms as Nodes for root
        List<Node> nodes = new ArrayList<>();

        // load json
        String json = Files.readString(Path.of("src/main/resources/map.json"));
        JsonObject branch = JsonParser.parseString(json).getAsJsonObject();

        // concrete
        for (JsonElement elem : branch.getAsJsonArray("concrete")) {
            JsonObject obj = elem.getAsJsonObject();

            Concrete c = new Concrete( // add to list
                obj.get("x").getAsInt(), obj.get("y").getAsInt(),
                obj.get("width").getAsInt(), obj.get("height").getAsInt()
            );

            nodes.add(c);
            platforms.add(c);
        }

        // blocks
        for (JsonElement elem : branch.getAsJsonArray("blocks")) {
            JsonObject obj = elem.getAsJsonObject();

            Block b = new Block(
                obj.get("x").getAsInt(), obj.get("y").getAsInt(),
                BlockType.valueOf(obj.get("type").getAsString()),
                obj.get("inverted").getAsBoolean()
            );

            nodes.add(b);
            blocks.add(b);
            platforms.add(b);
        }

        // tiles
        for (JsonElement elem : branch.getAsJsonArray("tiles")) {
            JsonObject obj = elem.getAsJsonObject();

            Tile t = new Tile(
                obj.get("x").getAsInt(), obj.get("y").getAsInt(),
                Clef.valueOf(obj.get("clef").getAsString()),
                obj.get("note").getAsString()
            );

            nodes.add(t);
            platforms.add(t);
        }

        // clef markers (NOT VISIBLE)
        if (branch.has("rooms") && branch.get("rooms").isJsonArray()) {
            for (JsonElement elem : branch.getAsJsonArray("rooms")) {
                JsonObject obj = elem.getAsJsonObject();

                ClefMarker rb = new ClefMarker(
                    obj.get("x").getAsInt(), obj.get("y").getAsInt(),
                    Clef.valueOf(obj.get("type").getAsString())
                );

                markers.add(rb);
            }
        }

        // exits
        if (branch.has("exits") && branch.get("exits").isJsonArray()) {
            for (JsonElement elem : branch.getAsJsonArray("exits")) {
                JsonObject obj = elem.getAsJsonObject();

                Exit ex = new Exit(
                    obj.get("x").getAsInt(), obj.get("y").getAsInt()
                );

                nodes.add(ex);
                platforms.add(ex);
            }
        }

        // add all to root
        for (Node n : nodes) root.getChildren().add(n);
    }

    /**
     * Get list of all Blocks
     * @return list of blocks
     */
    public List<Block> getBlocks() {
        return blocks;
    }

    /**
     * Get list of all platforms as the low level interface
     * @return list of platforms
     */
    public List<Platform> getAllPlatforms() {
        return platforms;
    }

    /** Get room markers list. */
    public List<ClefMarker> getMarkers() {
        return markers;
    }
}
