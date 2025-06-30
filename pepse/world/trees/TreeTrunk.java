package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Constants;
import java.awt.*;

/**
 * Represents the trunk of a tree in the Pepse game world. This class handles the rendering and physics
 * properties
 * of the tree trunk.
 * The trunk is rendered as a rectangle with a color approximated to a basic trunk color. It is immovable and
 * prevents intersections from any direction.
 * @author: Batia
 */
public class TreeTrunk extends GameObject {
    private static final Color TRUNK_BASIC_COLOR = new Color(100, 50, 20);


    /**
     * Construct a new GameObject instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param heightOfTree  Height in window coordinates.
     */
    public TreeTrunk(Vector2 topLeftCorner, float heightOfTree) {
        super(topLeftCorner, Vector2.of(Constants.BLOCK_SIZE, heightOfTree),
                new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_BASIC_COLOR)));
        setTag(Constants.TREE_TRUNK_TAG);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
