package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Constants;
import java.awt.*;
import java.util.Random;

/**
 * Represents a leaf in the game world. The leaf can move and rotate slightly to simulate a natural effect.
 * @author: Batia
 * @see pepse.world.trees.Tree
 */
public class Leaf extends GameObject {
    private static final Color BASIC_LEAF_COLOR = new Color(50, 200, 30);
    /**
     * Construct a new GameObject instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable The renderable representing the leaf.
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(Constants.BLOCK_SIZE), renderable);
        float time = getRandomTime();
        new ScheduledTask(this, time, false, this::moveLeaf);
        setTag(Constants.LEAF_TAG);
    }

    /**
     * Returns a random time interval for scheduling the leaf's movement.
     * @return A random float representing time in seconds.
     */
    private float getRandomTime(){
        Random rand = new Random();
        return rand.nextFloat(4f);
    }

    /**
     * Schedules the leaf's movement and rotation transitions to simulate a natural swaying effect.
     */
    private void moveLeaf(){
        Vector2 dim = Vector2.ONES.mult(Constants.BLOCK_SIZE);
        new Transition<Float>(this,
                (Float f) -> this.setDimensions(dim.mult(f)),
                1f, 0.9f, Transition.LINEAR_INTERPOLATOR_FLOAT, 2f,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        new Transition<Float>(this,
                (Float f) -> this.renderer().setRenderableAngle(f),
                -15f, 15f, Transition.LINEAR_INTERPOLATOR_FLOAT, 1f,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }
}
