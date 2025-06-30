package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Constants;

import java.awt.*;

/**
 * A utility class for creating a night effect in the game.
 * The class manages a night overlay that changes its opacity cyclically to simulate day-night transitions.
 * It uses a Transition component to smoothly vary the opacity of a black rectangle.
 * @see danogl.GameObject
 * @see danogl.components.Transition
 * @author: Batia
 */
public class Night {
    private static final String NIGHT_TAG = "night";
    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final float DAY_OPACITY = 0f;

    /**
     * Creates a GameObject representing the night overlay.
     * The overlay smoothly changes its opacity between fully transparent (day) and semi-transparent (night)
     * over the specified cycle length.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration of a full day-night cycle, in seconds.
     * @return A GameObject representing the night overlay.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        Renderable nightRend = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRend);
        night.setTag(NIGHT_TAG);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        new Transition<Float>(night, night.renderer()::setOpaqueness, DAY_OPACITY, MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength / Constants.HALF,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }

}
