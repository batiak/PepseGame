package pepse;

/**
 * This interface defines a contract for observing the avatar's jump action in the Pepse game.
 * Implementing classes will be notified whenever the avatar performs a jump.
 * @author: Batia
 */
public interface AvatarJumpedObserver {
    /**
     * Called when the avatar performs a jump.
     */
    void onAvatarJump();
}
