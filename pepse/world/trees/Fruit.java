package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Constants;
import java.util.function.Consumer;

/**
 * Represents a fruit in the game world. Fruits can be collected by the avatar to restore energy.
 * The fruit reappears after a certain time when collected.
 * @author: Batia
 * @see pepse.world.trees.Tree
 */
public class Fruit extends GameObject {
    private final float ENERGY_TO_ADD_WHEN_EATEN = 10f;
    private Consumer<Float> updateEnergyCallback;
    private boolean ateMe;

    /**
     * Construct a new GameObject instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Fruit(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(Constants.BLOCK_SIZE), renderable);
        setTag(Constants.FRUIT_TAG);
//        this.updateEnergyCallback = updateEnergyCallback;
    }

    /**
     * Registers a callback to update the avatar's energy when the fruit is collected.
     * @param updateEnergyCallback Callback to update energy.
     */
    public void addToEnergy(Consumer<Float> updateEnergyCallback) {
        this.updateEnergyCallback = updateEnergyCallback;
    }

    /**
     * Resets the fruit's state, making it visible and collectible again.
     */
    private void resetFruit(){
        ateMe = false;
        renderer().setOpaqueness(1f);
    }

    /**
     * Handles collision events with other game objects. If the colliding object is the avatar and the fruit
     * has not been collected yet, it updates the avatar's energy, makes the fruit invisible, and schedules
     * it to reappear after a certain time.
     * @param other The other GameObject involved in the collision.
     * @param collision Details about the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Constants.AVATAR_TAG) && !ateMe){
            ateMe = true;
            updateEnergyCallback.accept(ENERGY_TO_ADD_WHEN_EATEN);
            renderer().setOpaqueness(0f);
            new ScheduledTask(this, Constants.CYCLE_LENGTH,
                    false, this::resetFruit);
        }
    }
}
