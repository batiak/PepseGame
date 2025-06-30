package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Constants;

import java.awt.*;

/**
 * A utility class for creating a sun object in the game.
 * The sun moves along a circular path to simulate the apparent motion of the sun across the sky.
 * This class uses a Transition component to update the sun's position over a specified cycle length.
 *
 * @author: Batia
 */
public class Sun {
    private static final int SUN_DIAMETER = 100;
    private static final float CYCLE_LEN = 30;
    private static final float START_ANGLE = 0f;
    private static final float END_ANGLE = 360f;

    /**
     * static method that creates a GameObject representing the sun.
     * The sun follows a circular path centered around the middle of the game window.
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration of a full circular path cycle, in seconds.
     * @return A GameObject representing the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){
            float sunTopLeftX = (windowDimensions.x() / Constants.HALF) - (SUN_DIAMETER/Constants.HALF);
            float sunTopLeftY = (windowDimensions.y() / Constants.THIRD) - (SUN_DIAMETER/Constants.HALF);
            Renderable sunRenderable = new OvalRenderable(Color.YELLOW);
            GameObject sun = new GameObject(new Vector2(sunTopLeftX, sunTopLeftY),
                    new Vector2(SUN_DIAMETER,SUN_DIAMETER), sunRenderable);
            sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
            sun.setTag(Constants.SUN_TAG);
            float cycleCenterX = windowDimensions.x() / Constants.HALF;
            float cycleCenterY = windowDimensions.y() / Constants.HALF;
            Vector2 initialSunCenter = new Vector2(sunTopLeftX, sunTopLeftY);
            Vector2 cycleCenter = new Vector2(cycleCenterX, cycleCenterY);
            new Transition<Float>(sun,
                    (Float angle)->
                    sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                    START_ANGLE, END_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, CYCLE_LEN,
                     Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
            return sun;
    }
}
