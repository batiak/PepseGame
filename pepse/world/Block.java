package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
/**
 * Represents a single block in the game world.
 * Blocks are static objects with fixed size and mass, and only collide with the avatar.
 * @author: Batia
 */
public class Block extends GameObject {

    /**
     * Constructs a new Block instance.
     * @param topLeftCorner The position of the block in window coordinates (pixels).
     * @param renderable The renderable representing the block's appearance.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(Constants.BLOCK_SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Determines whether the block should collide with another object.
     * Blocks only collide with objects tagged as the avatar.
     * @param other The other GameObject.
     * @return True if the other object's tag is AVATAR_TAG, otherwise false.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (other.getTag().equals(Constants.AVATAR_TAG));
    }
}
