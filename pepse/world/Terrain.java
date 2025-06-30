package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the terrain generation, including ground height calculations and block placement.
 * @author Batia
 * @see pepse.world.Block
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int BLOCK_SIZE = Constants.BLOCK_SIZE;
    private static final int TERRAIN_DEPTH = 20;
    private static final int NOISE_FACTOR = 7;
    private static final float BASIC_HEIGHT_PARAMETER = 2/3f;
    private float windowX;
    private float windowY;
    private float groundHeightAtX0;
    private NoiseGenerator noiseGenerator;

    /**
     * Constructs a Terrain instance for managing ground generation.
     * @param windowDimensions The dimensions of the game window.
     * @param seed A seed for noise generation to ensure deterministic terrain.
     */
    public Terrain(Vector2 windowDimensions, int seed){
        this.windowX = windowDimensions.x();
        this.windowY = windowDimensions.y();
        this.groundHeightAtX0 = windowY * BASIC_HEIGHT_PARAMETER;
        this.noiseGenerator = new NoiseGenerator((double) seed, (int) groundHeightAtX0);
    }

    /**
     * Calculates the ground height at a specific x-coordinate.
     * @param x The x-coordinate.
     * @return The y-coordinate of the ground at the given x.
     */
    public float groundHeightAt(float x) {
//        System.out.printf("groundHeightAtX0: %f\n", groundHeightAtX0);
        float noise = (float) noiseGenerator.noise(x, Constants.BLOCK_SIZE * NOISE_FACTOR);
//        System.out.println(groundHeightAtX0 + noise);
        return groundHeightAtX0 + noise;
    }


    /**
     * Creates and returns a list of ground blocks within a given range.
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of Block objects representing the ground in the specified range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        // ensure that we place the blocks in coordinates that are divisible by BlockSize
        int start = (minX / BLOCK_SIZE) * BLOCK_SIZE;
        int end = (int) (Math.ceil((double) maxX / BLOCK_SIZE)) * BLOCK_SIZE;
        List<Block> allBlocks = new ArrayList<>();
        for (int x = start; x < end ; x += BLOCK_SIZE) {
            float height = windowY - groundHeightAt(x);
            int heightInBlocks = (int) Math.ceil(height / BLOCK_SIZE) + 1;
            allBlocks.addAll(fillBlockColumn(x, heightInBlocks));
        }
        return allBlocks;
    }

//    private float getBlocksHeightForX(float x) {
//        if ((Math.abs(x - (windowX / 2)) < Constants.BLOCK_SIZE)){
//            return windowY * 2f/3f;
//        } else {
//            return windowY - groundHeightAt(x);
//        }
//    }

    /**
     * Fills a vertical column of blocks at a specified x-coordinate.
     * @param xCoordinate The x-coordinate of the column.
     * @param colBlockNum The number of blocks in the column.
     * @return A list of Block objects representing the column.
     */
    private List<Block> fillBlockColumn(int xCoordinate, int colBlockNum) {
        List<Block> blocksCol = new ArrayList<>();
        for (int l = 0; l < colBlockNum; l ++) {
            Vector2 topLeftCorner = new Vector2(xCoordinate, windowY - (l * BLOCK_SIZE));
            Renderable blockRenderable = new RectangleRenderable(
                    ColorSupplier.approximateColor(BASE_GROUND_COLOR));
            Block curBlock = new Block(topLeftCorner, blockRenderable);
            curBlock.setTag(Constants.GROUND_TAG);
            blocksCol.add(curBlock);
        }
        return blocksCol;
    }
}
