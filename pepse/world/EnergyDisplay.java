package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.util.function.Supplier;

/**
 * A utility class for creating and managing an energy display for the avatar.
 * The energy display shows the current energy level as a percentage and updates dynamically.
 * @author: Batia
 */
public class EnergyDisplay{
    private static final int DISPLAY_SIZE = 30;
    private static final String FULL_ENERGY_START = "100%";
    private static final String PERCENT = "%";
    private static Supplier<Float> energyCallback; // The callback to get the avatar's energy
    private static GameObject theEnergyDisplay;

    /**
     * Creates a GameObject representing the energy display.
     * The display shows the avatar's energy level as a percentage, starting at 100%.
     * @param energyCallback A callback function that provides the current energy level of the avatar.
     * @return A GameObject representing the energy display.
     */
    public static GameObject create(Supplier<Float> energyCallback){
        EnergyDisplay.energyCallback = energyCallback;
        TextRenderable textRenderable = new TextRenderable(FULL_ENERGY_START);
        theEnergyDisplay = new GameObject(Vector2.ZERO, Vector2.ONES.mult(DISPLAY_SIZE), textRenderable);
        theEnergyDisplay.setTag(Constants.ENERGY_DISPLAY_TAG);
        theEnergyDisplay.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return theEnergyDisplay;
    }

    /**
     * Updates the energy display to reflect the current energy level.
     * The displayed value is retrieved from the provided energy callback function.
     */
    public static void update(){
        TextRenderable curEnergyRend = new TextRenderable(energyCallback.get().toString() + PERCENT);
        theEnergyDisplay.renderer().setRenderable(curEnergyRend);
    }

}
