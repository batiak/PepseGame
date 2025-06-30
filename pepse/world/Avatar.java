package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.AvatarJumpedObserver;

import java.awt.event.KeyEvent;

/**
 * Represents the avatar controlled by the player in the Pepse game world.
 * The avatar can walk, jump, and consume energy during movement. Energy replenishes during idle states.
 * This class manages the avatar's animations, movement logic, and energy mechanics. It interacts
 * with various game objects through collision handling and observer notifications for jumps.
 * @see pepse.AvatarJumpedObserver
 * @author: Batia
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float MAX_ENERGY = 100;
    private static final String RIGHT = "right";
    private static final String LEFT = "left";
    private static final float WALKING_ENERGY = 0.5f;
    private static final float JUMPING_ENERGY = 10f;
    private static final float IDLE_ENERGY = 1f;
    private float curEnergy;
    private static Renderable idleAnimation;
    private static Renderable runAnimation;
    private static Renderable jumpAnimation;
    private String curDirection = RIGHT;
    private AvatarJumpedObserver jumpedObserver;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;

    /**
     * Construct a new GameObject instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param inputListener
     * @param imageReader
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, Vector2.of(Constants.AVATAR_WIDTH, Constants.AVATAR_HEIGHT),
                imageReader.readImage(Constants.IDLE_0_PATH, true));
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.curEnergy = MAX_ENERGY;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(Constants.GRAVITY);
        idleAnimation = createIdleAnimation();
        runAnimation = createWalkingAnimation();
        jumpAnimation = createJumpAnimation();
        renderer().setRenderable(idleAnimation);
        setTag(Constants.AVATAR_TAG);
    }

    /**
     * Creates the walking animation for the avatar.
     * @return A Renderable object containing the walking animation frames.
     */
    private Renderable createWalkingAnimation() {
        String[] clipPaths = new String[]{Constants.RUN_0_PATH, Constants.RUN_1_PATH, Constants.RUN_2_PATH,
                Constants.RUN_3_PATH, Constants.RUN_4_PATH, Constants.RUN_5_PATH};
        Renderable[] renderables = new Renderable[clipPaths.length];
        for (int i = 0; i < renderables.length; i++) {
            renderables[i] = imageReader.readImage(clipPaths[i], true);
        }
        return new AnimationRenderable(renderables, 0.5);
    }

    /**
     * Creates the idle animation for the avatar.
     * @return A Renderable object containing the idle animation frames.
     */
    private Renderable createIdleAnimation() {
        String[] clipPaths = new String[]
                {Constants.IDLE_0_PATH, Constants.IDLE_1_PATH, Constants.IDLE_2_PATH, Constants.IDLE_3_PATH};
        Renderable[] clipImages = new Renderable[clipPaths.length];
        for (int i = 0; i < clipImages.length; i++) {
            clipImages[i] = imageReader.readImage(clipPaths[i], true);
        }
        return new AnimationRenderable(clipImages, 0.5);
    }

    /**
     * Handles the avatar's walking logic and animation based on the specified direction.
     * @param direction The direction of movement ("right" or "left").
     * @param curYVelocity The current vertical velocity of the avatar.
    */
    private void handleWalking(String direction, float curYVelocity) {
        if (curEnergy < WALKING_ENERGY) {
            return; // Skip walking if there's not enough energy
        }
        switch (direction) {
            case RIGHT:
                transform().setVelocityX(VELOCITY_X);
                if (curDirection.equals(LEFT)) {
                    renderer().setIsFlippedHorizontally(false);
                    curDirection = RIGHT;
                }
                renderer().setRenderable(runAnimation);
                break;
            case LEFT:
                transform().setVelocityX(-VELOCITY_X);
                if (curDirection.equals(RIGHT)) {
                    renderer().setIsFlippedHorizontally(true);
                    curDirection = LEFT;
                }
                renderer().setRenderable(runAnimation);
                renderer().setIsFlippedHorizontally(true);
                break;
            default:
                break;
        }
        if (getVelocity().y() == 0) {
            curEnergy -= WALKING_ENERGY; // Reduce energy for walking
        }
    }

    /**
     * Creates the jumping animation for the avatar.
     * @return A Renderable object containing the jump animation frames.
     */
    private Renderable createJumpAnimation() {
        String[] clipPaths = new String[]{Constants.JUMP_0_PATH, Constants.JUMP_1_PATH,
                Constants.JUMP_2_PATH, Constants.JUMP_3_PATH};
        Renderable[] renderables = new Renderable[clipPaths.length];
        for (int i = 0; i < renderables.length; i++) {
            renderables[i] = imageReader.readImage(clipPaths[i], true);
        }
        return new AnimationRenderable(renderables, 0.5);
    }

    /**
     * Handles the avatar's jumping logic and animation, including energy deduction.
     */
    private void handleJumping() {
        notifyJumpObservers();
        if (curEnergy >= JUMPING_ENERGY && getVelocity().y() == 0) {
            transform().setVelocityY(VELOCITY_Y);
//            System.out.println("got here");
            curEnergy -= JUMPING_ENERGY;
            renderer().setRenderable(jumpAnimation);
//            System.out.println("after jumping" + curEnergy);
        }
    }

    /**
     * Updates the avatar's energy by adding the specified amount.
     * @param toAdd The amount of energy to add.
     */
    public void updateEnergy(float toAdd) {
        if (curEnergy < MAX_ENERGY) {
            curEnergy += toAdd;
        }
    }

    /**
     * Updates the avatar's state, handling movement, animations, and energy management.
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Reset horizontal velocity to zero
        transform().setVelocityX(0);

        // Handle walking
        boolean isLeftPressed = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean isRightPressed = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean isSpacePressed = inputListener.isKeyPressed(KeyEvent.VK_SPACE);

        if (isLeftPressed && !isRightPressed) {
            handleWalking(LEFT, getVelocity().y());
        } else if (isRightPressed && !isLeftPressed) {
            handleWalking(RIGHT, getVelocity().y());
        }

        // Handle jumping
        if (isSpacePressed && getVelocity().y() == 0 && curEnergy >= JUMPING_ENERGY) {
            handleJumping();
        }

        if (!isRightPressed && !isLeftPressed && !isSpacePressed && getVelocity().equals(Vector2.ZERO)) {
            updateEnergy(IDLE_ENERGY);
        }
//        System.out.println(curEnergy); // for debug
        EnergyDisplay.update();
    }

    /**
     * Returns the current energy of the avatar.
     * @return The current energy as a Float.
     */
    public Float getCurEnergy() {
        return this.curEnergy;
    }

    /**
     * Determines if the avatar should collide with the specified object.
     * @param other The other GameObject to check collision against.
     * @return True if a collision should occur, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        String tag = other.getTag();
        if (tag.equals(Constants.GROUND_TAG) || tag.equals(Constants.TREE_TRUNK_TAG) ||
                tag.equals(Constants.FRUIT_TAG)){
            return true;
        }
        return false;
    }

    /**
     * Handles the logic when a collision with another object begins.
     * @param other The other GameObject involved in the collision.
     * @param collision The collision details.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Constants.GROUND_TAG) || other.getTag().equals(Constants.TREE_TRUNK_TAG)) {
            renderer().setRenderable(idleAnimation);
        }
    }


    /**
     * Registers an observer to be notified when the avatar jumps.
     * @param jumpedObserver The observer to notify of jump events.
     */
    public void registerObserver(AvatarJumpedObserver jumpedObserver) {
        this.jumpedObserver = jumpedObserver;
    }


    /**
     * Notifies registered observers that the avatar has jumped.
     */
    private void notifyJumpObservers() {
        jumpedObserver.onAvatarJump();
    }
}
