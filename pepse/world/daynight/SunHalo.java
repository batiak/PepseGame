package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Constants;

import java.awt.*;

/**
 * A utility class for creating a halo effect around the sun in the game.
 * The sun halo is a translucent, larger circular overlay that follows the sun's position.
 * @author: Batia
 */
public class SunHalo {

    /**
     * static method that creates a GameObject representing the sun halo.
     * The halo is a semi-transparent yellow circle that dynamically follows the sun's position.
     * @param sun The GameObject representing the sun, whose position the halo will follow.
     * @return A GameObject representing the sun halo.
     */
    public static GameObject create(GameObject sun){
        Color haloColor = new Color(255, 255, 0, 20);
        Renderable haloRenderable = new OvalRenderable(haloColor);
        Vector2 haloDimensions = new Vector2(sun.getDimensions().x()*2, sun.getDimensions().y()*2);
        GameObject sunHalo = new GameObject(Vector2.ZERO, haloDimensions, haloRenderable);
        sunHalo.setCenter(sun.getCenter());
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(Constants.HALO_TAG);
        // update sunHalo position to be sun position
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        return sunHalo;
    }
}
