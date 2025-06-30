package pepse.world.trees;

import danogl.util.Vector2;
import pepse.world.Constants;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * A utility class for creating and managing flora (trees) in the game world.
 * This class generates trees at random positions within a specified range based on a given seed and ground
 * height.
\ * @author: Batia
 * @see pepse.world.trees.Tree
 */
public class Flora {
    private final Function<Float, Float> groundHeightCallback;
    private final HashSet<Tree> trees;
    private final Random random;
    private final int seed;

    /**
     * Constructs a Flora instance.
     * @param groundHeightCallback A callback function to get the ground height at a given x-coordinate.
     * @param seed The seed for generating random positions of trees.
     */
    public Flora(Function<Float, Float> groundHeightCallback, int seed) {
        this.groundHeightCallback = groundHeightCallback;
        this.random = new Random();
        trees = new HashSet<>();
        this.seed = seed;
    }

    /**
     * Creates trees within the specified range.
     * Trees are placed at random x-coordinates within the range based on the provided seed.
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A HashSet of Tree objects created within the specified range.
     * @see pepse.world.trees.Tree
     */
    public HashSet<Tree> createInRange(int minX, int maxX){
        for (int i = minX; i < maxX; i+= Constants.BLOCK_SIZE) {
            if (shouldPlantTree(i)){
//                float curGroundHeight = groundHeightCallback.apply((float) i);
//                float bottomLeftY =
//                        (float) Math.floor(groundHeightCallback.apply((float) i));
                float groundHeight = groundHeightCallback.apply((float)i);
//                System.out.printf("tree: %f", groundHeight);
                float bottomLeftY = (int) (Math.floor(groundHeightCallback.apply((float) i)
                        / Constants.BLOCK_SIZE) + 1) * Constants.BLOCK_SIZE;
                Vector2 bottomLeftCorner = new Vector2(i, groundHeight);
                Tree curTree = new Tree(bottomLeftCorner, seed);
                trees.add(curTree);
            }
        }
        return trees;
    }

    /**
     * Determines whether a tree should be planted at the given x-coordinate.
     * The decision is based on a random value generated using the seed.
     * @param i The x-coordinate.
     * @return True if a tree should be planted, false otherwise.
     */
    private boolean shouldPlantTree(int i){
        Random random = new Random(Objects.hash(i, seed));
        int answer = random.nextInt(Constants.PORTABILITY_BOUND);
        return (answer == 1);
    }
}
