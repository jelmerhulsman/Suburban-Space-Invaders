package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bralts & Hulsman
 */
public class LivingThing extends Node {

    protected float health, knockBackJumpSpeed, knockBackWeakness;
    protected CharacterControl pawnControl;
    protected CapsuleCollisionShape capsuleShape;
    protected float knockBackTimer = 1;

    public LivingThing() {
    }

    public float getHealth() {
        return health;
    }

    public CharacterControl getCharacterControl() {
        return pawnControl;
    }

    public void movePawn(Vector3f direction) {
        try {
            pawnControl.setWalkDirection(direction);
            this.setLocalTranslation(pawnControl.getPhysicsLocation());
        } catch (Exception e) {
            System.out.println("UNABLE TO MOVE! " + e.toString());
        }
    }

    public void knockBack(Vector3f direction) {
        Vector3f knockDirection = new Vector3f(direction);
        knockDirection = knockDirection.mult(knockBackWeakness);
        movePawn(knockDirection);
    }

    public void knockBackJump() {
        float jumpSpeed = pawnControl.getJumpSpeed();
        pawnControl.setJumpSpeed(knockBackJumpSpeed);
        pawnControl.jump();
        pawnControl.setJumpSpeed(jumpSpeed);
    }

    public boolean jump() {
        if (pawnControl.onGround()) {
            pawnControl.jump();
            return true;
        }

        return false;
    }

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
