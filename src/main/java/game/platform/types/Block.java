package game.platform.types;

import game.platform.SpritePlatform;
import game.Constants.kBlock;
import javafx.scene.image.Image;

import java.io.InputStream;

/** A music-based pulsing sprite platform */
public class Block extends SpritePlatform {
    /** Enum of block types */
    public enum BlockType {
        STUB, SHORT, LONG, VERTICAL
    }

    // sprites
    private Image spriteActive;
    private Image spriteInactive;

    // dimensions
    private double w;
    private double h;

    // whether the block is active
    private boolean active;

    public Block(
        double x, double y,
        BlockType type,
        boolean inverted // if inactive on start
    ) {
        super(x, y, null); // start with null image

        // automatic block type processing
        switch (type) {
            case STUB -> {
                try (
                    InputStream fActive = Block.class.getResourceAsStream("/sprites/blocks/block_active_20x12.png");
                    InputStream fInactive = Block.class.getResourceAsStream("/sprites/blocks/block_inactive_20x12.png")
                ) {
                    spriteActive = new Image(fActive);
                    spriteInactive = new Image(fInactive);
                    w = kBlock.STUB_WIDTH;
                    h = kBlock.STUB_HEIGHT;
                } catch (Exception ignored) {}
            }
            case SHORT -> {
                try (
                    InputStream fActive = Block.class.getResourceAsStream("/sprites/blocks/block_active_80x20.png");
                    InputStream fInactive = Block.class.getResourceAsStream("/sprites/blocks/block_inactive_80x20.png")
                ) {
                    spriteActive = new Image(fActive);
                    spriteInactive = new Image(fInactive);
                    w = kBlock.SHORT_WIDTH;
                    h = kBlock.SHORT_HEIGHT;
                } catch (Exception ignored) {}
            }
            case LONG -> {
                try (
                    InputStream fActive = Block.class.getResourceAsStream("/sprites/blocks/block_active_160x20.png");
                    InputStream fInactive = Block.class.getResourceAsStream("/sprites/blocks/block_inactive_160x20.png")
                ) {
                    spriteActive = new Image(fActive);
                    spriteInactive = new Image(fInactive);
                    w = kBlock.LONG_WIDTH;
                    h = kBlock.LONG_HEIGHT;
                } catch (Exception ignored) {}
            }
            case VERTICAL -> {
                try (
                    InputStream fActive = Block.class.getResourceAsStream("/sprites/blocks/block_active_20x200.png");
                    InputStream fInactive = Block.class.getResourceAsStream("/sprites/blocks/block_inactive_20x200.png")
                ) {
                    spriteActive = new Image(fActive);
                    spriteInactive = new Image(fInactive);
                    w = kBlock.VERTICAL_WIDTH;
                    h = kBlock.VERTICAL_HEIGHT;
                } catch (Exception ignored) {}
            }
        }
        setImage(
            inverted ? spriteInactive : spriteActive
        );

        active = !inverted;
    }

    @Override
    public double getWidth() {
        return w;
    }

    @Override
    public double getHeight() {
        return h;
    }

    /** Toggle block state */
    public void toggle() {
        active = !active;

        setImage( // swap sprite
            active ? spriteActive : spriteInactive
        );
    }

    /** Get current state */
    public boolean isActive() {
        return active;
    }
}