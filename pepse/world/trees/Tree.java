package pepse.world.trees;

import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Constants;

import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a tree in the game world. A tree consists of a trunk, leaves, and fruits,
 * all of which are generated
 * procedurally based on a seed and position. The tree trunk height and tree top size are determined
 * by constants.
 * @author: Batia
 * @see pepse.world.trees.Leaf
 * @see pepse.world.trees.Fruit
 * @see pepse.world.trees.TreeTrunk
 */
public class Tree {
    private static final Color TRUNK_BASIC_COLOR = new Color(100, 50, 20);
    private static final Color BASIC_LEAF_COLOR = new Color(50, 200, 30);
    private Color FRUIT_COLOR = new Color(163, 13, 209);
    private static final String LEAF = "leaf";
    private static final String FRUIT = "fruit";
    private Vector2 bottomLeftCornerTrunk;
    private Vector2 topLeftCornerTrunk;
    private int seed;
    private HashSet<Leaf> leaves;
    private TreeTrunk trunk;
    private HashSet<Fruit> fruits;

// GETTERS //
    /**
     * Returns the set of leaves generated for this tree.
     * @return A HashSet of Leaf objects.
     */
    public HashSet<Leaf> getLeaves() {
        return leaves;
    }

    /**
     * Returns the tree trunk.
     * @return A TreeTrunk object representing the tree's trunk.
     */
    public TreeTrunk getTrunk() {
        return trunk;
    }

    /**
     * Returns the set of fruits generated for this tree.
     * @return A HashSet of Fruit objects.
     */
    public HashSet<Fruit> getFruits() {
        return fruits;
    }

// CONSTRUCTOR
    /**
     * Constructs a Tree object at the specified bottom-left corner & initializes it using the provided seed
     * @param bottomLeftCorner The bottom-left corner position of the tree trunk.
     * @param seed The seed for generating random elements of the tree.
     */
    public Tree(Vector2 bottomLeftCorner, int seed) {
        this.bottomLeftCornerTrunk = bottomLeftCorner;
//        this.treeTopSize = Constans.TREE_TRUNK_HEIGHT / 2f;
        this.seed = seed;
        this.leaves = new HashSet<>();
        this.fruits = new HashSet<>();
        createTreeTrunk();
        createTreeTop();
//        System.out.println("\nend of tree\n");
    }

// HELPERS

    /**
     * Creates the tree trunk and initializes the trunk field.
     */
    private void createTreeTrunk(){
        float topLeftCornerY = bottomLeftCornerTrunk.y() - Constants.TREE_TRUNK_HEIGHT;
        Vector2 topLeftCornerTrunk = new Vector2(bottomLeftCornerTrunk.x(), topLeftCornerY);
        this.trunk = new TreeTrunk(topLeftCornerTrunk, Constants.TREE_TRUNK_HEIGHT);
    }

    /**
     * Creates the tree top consisting of leaves and fruits placed randomly around the top of the trunk.
     */
    private void createTreeTop(){
//        Vector2 topLeftCornerHead = new Vector2(trunk.getTopLeftCorner())
        int treeTopHalfSize = Constants.TREE_TOP_SIZE / 2;
//        Vector2 topLeftCornerHead = trunk.getTopLeftCorner().add(Vector2.ONES.mult((-1)*treeTopSize));
        for (int i = -treeTopHalfSize; i < treeTopHalfSize ; i+= Constants.BLOCK_SIZE) {
            for (int j = -treeTopHalfSize; j < treeTopHalfSize; j+= Constants.BLOCK_SIZE) {
                String toCreate = whatToCreate( i + j);
                if (toCreate == null){
                    break;
                }
                Vector2 curPosition = trunk.getTopLeftCorner().add(Vector2.of(i, j));
                switch (toCreate) {
                    case LEAF:
                        Leaf newLeaf = createLeaf(curPosition);
//                        System.out.printf("%d, %d - leaf\n", i, j);
                        leaves.add(newLeaf);
                        break;
                    case FRUIT:
                        Fruit newFruit = createFruit(curPosition);
                        fruits.add(newFruit);
//                        System.out.printf("%d, %d - fruit\n", i, j);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Determines whether to create a leaf or a fruit at a given position based on randomness.
     * @param x The x-coordinate used to determine randomness.
     * @return A string indicating whether to create a "leaf", "fruit", or null if nothing should be created.
     */
    private String whatToCreate(int x) {
        Random random = new Random(Objects.hash(x, seed));
        int answer = random.nextInt(Constants.PORTABILITY_BOUND);
//        System.out.println(randomFloat);
        if (answer < 8) {
            return LEAF;
        } else if (answer > 9){
            return FRUIT;
        } else {
            return null;
        }
    }

    /**
     * Creates a Leaf object at the specified position.
     * @param position The position where the leaf should be created.
     * @return A new Leaf object.
     */
    private Leaf createLeaf(Vector2 position) {
        return new Leaf(position, new RectangleRenderable(ColorSupplier.approximateColor(BASIC_LEAF_COLOR)));
    }

    /**
     * Creates a Fruit object at the specified position.
     * @param position The position where the fruit should be created.
     * @return A new Fruit object.
     */
    private Fruit createFruit(Vector2 position) {
        return new Fruit(position, new OvalRenderable(ColorSupplier.approximateColor(FRUIT_COLOR)));
    }
}
