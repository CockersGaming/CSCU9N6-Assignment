package game2D.entities.Player;

import game2D.Animation;
import game2D.Sprite;

public class Player extends Sprite {

    private final float PLAYER_SPEED = 0.2f;

    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim
     */
    public Player(Animation anim) {
        super(anim);
    }

    public void moveLeft() {
        this.setVelocityX(-PLAYER_SPEED);
        this.setScaleX(-1);
    }

    public void moveRight() {
        this.setVelocityX(PLAYER_SPEED);
        this.setScaleX(1);
    }

    public void stopMoving() {
        this.setVelocityX(0);
        this.setScaleX(1);
    }
}
