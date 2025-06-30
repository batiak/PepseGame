package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.AvatarJumpedObserver;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Represents a cloud in the game world that can create rain upon avatar interaction.
 * @author: Batia
 * @see AvatarJumpedObserver
 */
public class Cloud implements AvatarJumpedObserver {
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final int CLOUD_BLOCK_SIZE = 6;
    private static final int CLOUD_HEIGHT = 100;
    private static final int CLOUD_WIDTH = 180;
    private static final int MAX_RAIN = 4;
    private static final int MIN_RAIN = 1;
    private static Vector2 windowDimensions;
    private final List<List<Integer>> cloudList = List.of((
                    List.of(0, 1, 1, 0, 0, 0)),
            List.of(1, 1, 1, 0, 1, 0),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(0, 1, 1, 1, 0, 0),
            List.of(0, 0, 0, 0, 0, 0));
    private static final Vector2 cloudStartPosition = new Vector2(-180, 100);
    private GameObject cloudObject;
    private Consumer<GameObject> addRainCallback;
    private Consumer<GameObject> removeRainCallback;
    private ImageReader imageReader;
    private HashSet<GameObject> rainDrops;


    /**
     * Constructs a new Cloud instance.
     * @param addRainCallback Callback function to add rain drops to the game.
     * @param removeRainCallback Callback function to remove rain drops from the game.
     * @param imageReader Utility for reading image files.
     */
    public Cloud(Consumer<GameObject> addRainCallback, Consumer<GameObject> removeRainCallback,
                 ImageReader imageReader) {
        this.addRainCallback = addRainCallback;
        this.removeRainCallback = removeRainCallback;
        this.imageReader = imageReader;
        this.rainDrops = new HashSet<>();
    }

    /**
     * static method that creates a cloud GameObject and initializes its movement.
     * @param windowDimensions Dimensions of the game window.
     * @param cloudInstance The current Cloud instance.
     * @return The created cloud GameObject.
     */
    public GameObject createCloud(Vector2 windowDimensions, Cloud cloudInstance) {
        Cloud.windowDimensions = windowDimensions;
        Renderable readableCloud = this.imageReader.readImage("assets/cloud.png", false);
        GameObject cloud = new GameObject(Vector2.of(0, 90), Vector2.of(CLOUD_WIDTH, CLOUD_HEIGHT),
                readableCloud);
        setCloudMovement(cloud);
        cloud.setTag(Constants.CLOUD_TAG);
        cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        cloudInstance.cloudObject = cloud;
        return cloud;
    }

//    public HashSet<GameObject> createCloud(Vector2 cloudTopLeftCorner, Vector2 windowDimensions){
//        this.windowDimensions = windowDimensions;
//        HashSet<GameObject> cloudSet = new HashSet<>();
//
//        for (int i = 0; i < CLOUD_BLOCK_SIZE; i++){
//            for(int j = 0; j < CLOUD_BLOCK_SIZE; j++){
//                Vector2 pieceTopLeftCorner = new Vector2(cloudTopLeftCorner.add(
//                        Vector2.of(j * Constans.BLOCK_SIZE, i * Constans.BLOCK_SIZE)));
//                GameObject newPiece = createPieceOfCloud(pieceTopLeftCorner);
//                setPieceOpaqueness(newPiece, i, j);
//                setPieceMovement(newPiece);
//                newPiece.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
//                newPiece.setTag(Constans.PIECE_OF_CLOUD);
//                cloudSet.add(newPiece);
//            }
//        }
//        return cloudSet;
//    }


    /**
     * Defines the movement of the cloud using a looping transition.
     * @param newCloud The cloud GameObject to move.
     */
    private static void setCloudMovement(GameObject newCloud) {
        new Transition<Float>(newCloud,
                (Float toMove) -> newCloud.setCenter(cloudStartPosition.add(Vector2.RIGHT.mult(toMove))),
                0f, windowDimensions.x() - cloudStartPosition.x(),
                Transition.LINEAR_INTERPOLATOR_FLOAT, 20,
                Transition.TransitionType.TRANSITION_LOOP, null);
    }
//
//    private void setPieceOpaqueness(GameObject piece, int i, int j){
//        piece.renderer().setOpaqueness(cloudList.get(i).get(j));
//    }

//
//    private GameObject createPieceOfCloud(Vector2 topLeftCorner){
//        Renderable rend = new RectangleRenderable(ColorSupplier.approximateColor(BASE_CLOUD_COLOR));
//        return new GameObject(topLeftCorner, Vector2.ONES.mult(Constans.BLOCK_SIZE), rend);
//    }


    /**
     * Triggers rain generation when the avatar jumps.
     */
    @Override
    public void onAvatarJump() {
        goRain();
    }

    /**
     * Generates rain drops and adds them to the game.
     */
    private void goRain() {
        Renderable rainRenderable = imageReader.readImage("assets/tripleDrop.png", true);
        int numOfDrops = howManyDrops();
        for (int i = 0; i < numOfDrops; i++) {
            Vector2 rainCenter = Vector2.of(cloudObject.getCenter().x() + i*30,
                    (cloudObject.getCenter().y() + 20 * i) + CLOUD_HEIGHT / 2f);
            Vector2 topLeft = cloudObject.getTopLeftCorner().add(Vector2.RIGHT.mult(10));
            GameObject curRainObject = new GameObject(topLeft, Vector2.of(50, 30), rainRenderable);
            curRainObject.setCenter(rainCenter);
            curRainObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
            curRainObject.transform().setAccelerationY(Constants.GRAVITY);
            this.rainDrops.add(curRainObject);
            this.addRainCallback.accept(curRainObject);
//            moveAndFadeRain();
//            this.addRainCallback.accept(curRainObject);
        }
//        addRain();
    }

    /**
     * Determines the number of rain drops to generate.
     * @return The number of rain drops.
     */
        void addRain(){
            for (GameObject d : rainDrops){
                moveAndFadeRain(d);
                this.addRainCallback.accept(d);
            }
        }
//

    /**
     * Determines the number of rain drops to generate.
     * @return The number of rain drops.
     */
    private int howManyDrops() {
        int numDrops = new Random().nextInt(MIN_RAIN, MAX_RAIN);
        System.out.println(numDrops);
        return numDrops;
    }

    /**
     * Fades out and removes rain drops from the game.
     * @param curRainObject The rain GameObject to move and fade.
     */
    private void moveAndFadeRain(GameObject curRainObject) {
//        new Transition<Float>(curRainObject,
//                (Float toMove) -> curRainObject.getTopLeftCorner().add(Vector2.DOWN.mult(toMove)),
//                0f, windowDimensions.y(), Transition.LINEAR_INTERPOLATOR_FLOAT, 30,
//                Transition.TransitionType.TRANSITION_ONCE, null);
        curRainObject.renderer().fadeOut(2f);
        new ScheduledTask(curRainObject,30, false, this::removeRain);
    }

    /**
     * Removes rain drops from the game.
     */
    private void removeRain() {
        for (GameObject d : rainDrops){
            removeRainCallback.accept(d);
        }
    }
}
