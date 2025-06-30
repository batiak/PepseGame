package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.trees.Fruit;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Manages the Pepse game, including initialization, game updates, and the creation of game objects.
 * Handles dynamic terrain generation, cloud and flora management, and avatar interactions.
 * @see Terrain
 * @see Avatar
 * @see Cloud
 * @see Sun
 * @see Night
 * @see SunHalo
 * @see Flora
 * @author: Batia
 */
public class PepseGameManager extends GameManager {
    private int extraWorld;
    private final int LEAVES_LAYER = Layer.STATIC_OBJECTS + 50;
    private final int CLOUD_LAYER = Layer.STATIC_OBJECTS - 10;
    WindowController windowController;
    private float windowHeight;
    private float windowWidth;
    private Vector2 windowDimensions;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private Terrain terrain;
    private Avatar gameAvatar;
    private GameObject gameEnergyDisplay;
    private Flora gameFlora;
    private Cloud gameCloudInstance;
    private float leftWorldEdge;
    private float rightWorldEdge;
    private int seed;

    /**
     * Initializes the game by setting up the environment, objects, and camera.
     * @param imageReader Used to read images for rendering game objects.
     * @param soundReader Used to read sounds for the game.
     * @param inputListener Used to receive user inputs.
     * @param windowController Controls the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                    UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.windowController = windowController;
        this.seed = new Random().nextInt();
        initializeWindowDimensions(windowController);
        updateInfinityWorldEdges();
        updateLayerCollision();
        createSky();
        createTerrain();
        createTerrainRange(leftWorldEdge, rightWorldEdge);
        GameObject curSun = createSunAndHalo();
        addYellowRibbon(curSun);
        createNight();
        createCloud();
        gameAvatar = createAvatar();
        createFlora(0f, windowWidth);
        this.gameEnergyDisplay = createEnergyDisplay();

        setCamera(new Camera(gameAvatar, Vector2.of(0, -80),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    /**
     * Updates the boundaries of the infinite world for the game.
     */
    private void updateInfinityWorldEdges() {
        this.leftWorldEdge = -0.5f * windowController.getWindowDimensions().x();
        this.rightWorldEdge = windowController.getWindowDimensions().x() +
                (0.5f * windowController.getWindowDimensions().x());
    }

    /**
     * Updates the collision rules for game object layers.
     */
    private void updateLayerCollision() {
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS, Layer.FOREGROUND, true);
    }

    /**
     * Creates clouds in the game environment.
     */
    private void createCloud(){
            Consumer<GameObject> addCallback = (rain) -> gameObjects().addGameObject(rain, CLOUD_LAYER);
        Consumer<GameObject> removeCallback = (rain) -> gameObjects().removeGameObject(rain, CLOUD_LAYER);
        this.gameCloudInstance = new Cloud(addCallback, removeCallback, imageReader);
        GameObject cloud = gameCloudInstance.createCloud(windowDimensions, gameCloudInstance);
        gameObjects().addGameObject(cloud, CLOUD_LAYER);
    }

    /**
     * Adds a yellow ribbon to the sun (so we shouldn't forget them).
     * @param sun The sun object.
     */
    private void addYellowRibbon(GameObject sun) {
        Renderable renderable = imageReader.readImage("assets/yellow.png", false);
        GameObject yellowRibbon = new GameObject(sun.getCenter(),
                Vector2.ONES.mult(90), renderable);
        yellowRibbon.setCenter(sun.getCenter());
        yellowRibbon.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        yellowRibbon.addComponent(deltaTime -> yellowRibbon.setCenter(sun.getCenter()));
        gameObjects().addGameObject(yellowRibbon, Layer.BACKGROUND);
    }

    /**
     * Initializes window dimensions and calculates related parameters.
     * @param windowController Controls the game window.
     */
    private void initializeWindowDimensions(WindowController windowController){
        this.windowHeight = windowController.getWindowDimensions().y();
        this.windowWidth = windowController.getWindowDimensions().x();
        this.windowDimensions = Vector2.of(windowWidth, windowHeight);
        this.extraWorld = (int) (Math.ceil(windowWidth / 2));
    }

    /**
     * Creates flora within a specified range.
     * @param startRange The starting position of the range.
     * @param endRange The ending position of the range.
     */
    private void createFlora(float startRange , float endRange){
        this.gameFlora = new Flora(terrain::groundHeightAt, seed);
        HashSet<Tree> trees = gameFlora.createInRange((int) startRange, (int) endRange);
        for(Tree curTree: trees){
            gameObjects().addGameObject(curTree.getTrunk(), Layer.STATIC_OBJECTS);
            HashSet<Leaf> leaves = curTree.getLeaves();
            HashSet<Fruit> fruits = curTree.getFruits();
            for(Leaf curLeaf: leaves){
                gameObjects().addGameObject(curLeaf, LEAVES_LAYER);
            }
            for (Fruit curFruit : fruits){
                curFruit.addToEnergy(gameAvatar::updateEnergy);
                gameObjects().addGameObject(curFruit, Layer.STATIC_OBJECTS);
            }
        }
    }

    /**
     * Creates the sky background for the game.
     */
    private void createSky(){
        GameObject skyGameObject = Sky.create(windowDimensions);
        gameObjects().addGameObject(skyGameObject, Layer.BACKGROUND);
    }

    /**
     * Initializes the terrain object for the game.
     */
    private void createTerrain(){
        Terrain terrain = new Terrain(windowDimensions, seed);
        this.terrain = terrain;
    }


    /**
     * Creates terrain blocks within the specified range.
     * @param startRange The starting position of the range.
     * @param endRange The ending position of the range.
     */
    private void createTerrainRange(float startRange , float endRange){
        List<Block> blocks = terrain.createInRange((int) startRange, (int) endRange);
        for (Block block : blocks) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Creates the night overlay for the game.
     */
    private void createNight(){
        GameObject night = Night.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.BACKGROUND);
    }

    /**
     * Creates the sun and its associated halo.
     * @return The sun GameObject.
     */
    private GameObject createSunAndHalo(){
        GameObject sun = Sun.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        return sun;
    }

    /**
     * Creates the game avatar at the appropriate location.
     * @return The avatar GameObject.
     */
    private Avatar createAvatar(){
        // Initialize the avatar in the middle of the screen on a multiple of the block size
        int x = (int) Math.floor((windowDimensions.x() / Constants.HALF) / Constants.BLOCK_SIZE)
                    * Constants.BLOCK_SIZE;
        Vector2 avatarPos = new Vector2(x, terrain.groundHeightAt(x) - 30);
        Avatar avatar = new Avatar(avatarPos, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        avatar.registerObserver(gameCloudInstance);
        return avatar;
    }

    /**
     * Creates the energy display for the game.
     * @return The energy display GameObject.
     */
    private GameObject createEnergyDisplay(){
        GameObject energyDisplay = EnergyDisplay.create(gameAvatar::getCurEnergy);
        gameObjects().addGameObject(energyDisplay, Layer.UI);
        return energyDisplay;
    }

    /**
     * Updates the game state each frame. Monitors the avatar's position to dynamically add or remove
     * terrain and game objects as the avatar moves closer to the world's edges.
     * @param deltaTime The time elapsed since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
//        System.out.printf("right edge: %s\n", rightWorldEdge);
//        System.out.printf("left edge: %s\n", leftWorldEdge);
        super.update(deltaTime);
        float avatarLocation = gameAvatar.getTopLeftCorner().x();
        if (avatarLocation > rightWorldEdge - extraWorld) {
            addToRight();
        } else if (avatarLocation < leftWorldEdge + extraWorld) {
            addToLeft();
        }

    }

    /**
     * Removes game objects that are outside the visible world boundaries.
     */
    private void removeUnseenObjects() {
        for (GameObject go : gameObjects()) {
            if (go.getCenter().x() < leftWorldEdge || go.getCenter().x() > rightWorldEdge) {
                if (go.getTag().equals(Constants.GROUND_TAG) || go.getTag().equals(Constants.TREE_TRUNK_TAG)
                    || go.getTag().equals(Constants.LEAF_TAG) || go.getTag().equals(Constants.FRUIT_TAG)) {
//                    System.out.printf("go of %s was removed\n", go.getTag());
                    gameObjects().removeGameObject(go);
                }
            }
        }
    }

    /**
     * Adds new terrain and flora to the right side of the world when the avatar moves close to the edge.
     */
    private void addToRight() {
        createTerrainRange(rightWorldEdge, rightWorldEdge + extraWorld);
        removeUnseenObjects();
        this.rightWorldEdge += extraWorld;
        this.leftWorldEdge += extraWorld;
    }

    /**
     * Adds new terrain and flora to the left side of the world when the avatar moves close to the edge.
     */
    private void addToLeft(){
        createTerrainRange(leftWorldEdge - extraWorld, leftWorldEdge);
        removeUnseenObjects();
        this.leftWorldEdge -=  extraWorld;
        this.rightWorldEdge -=  extraWorld;
    }

    /**
     * main function to run the Pepse game.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
