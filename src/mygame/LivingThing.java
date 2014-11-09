package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Living thing class, super of player and enemy class
 *
 * @author Bralts & Hulsman
 */
public class LivingThing extends Node {

    protected int health;
    protected float knockBackJumpSpeed, knockBackWeakness;
    protected CharacterControl pawnControl;
    protected CapsuleCollisionShape capsuleShape;
    protected float knockBackTimer = 1;

    public LivingThing() {
    }

    /**
     * Returns health of this living thing
     *
     * @return health
     */
    public float getHealth() {
        return health;
    }

    /**
     * Moves the living thing in given direction
     *
     * @param direction
     */
    public void movePawn(Vector3f direction) {
        try {
            pawnControl.setWalkDirection(direction);
            this.setLocalTranslation(pawnControl.getPhysicsLocation());
        } catch (Exception e) {
            System.out.println("UNABLE TO MOVE! " + e.toString());
        }
    }

    /**
     * Moves living thing in given knockback direction
     *
     * @param direction
     */
    public void knockPawnBack(Vector3f direction) {
        Vector3f knockDirection = new Vector3f(direction);
        knockDirection = knockDirection.mult(knockBackWeakness);
        movePawn(knockDirection);
    }

    /**
     * Trigger a small leap backwards for this living thing
     */
    public void knockBackJump() {
        float jumpSpeed = pawnControl.getJumpSpeed();
        pawnControl.setJumpSpeed(knockBackJumpSpeed);
        pawnControl.jump();
        pawnControl.setJumpSpeed(jumpSpeed);
    }

    /**
     * Triggers a jump for this living thing
     *
     * @return onGround
     */
    public boolean jump() {
        if (pawnControl.onGround()) {
            pawnControl.jump();
            return true;
        }

        return false;
    }

    /**
     * Substract damage from health and return whether this living thing is dead
     * or not
     *
     * @param damage
     * @return killed
     */
    public boolean gotKilled(float damage) {
        if (health >= 0.1f) {
            knockBackTimer = 0;
            knockBackJump();

            health -= damage;
            if (health < 0.1f) {
                return true;
            }
        }
        return false;
    }
}
