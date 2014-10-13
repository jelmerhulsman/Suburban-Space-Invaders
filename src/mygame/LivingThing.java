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
    public float knockBackTimer = 1;

    public LivingThing() {
    }

    public float getHealth() {
        return this.health;
    }

    public CharacterControl getCharacterControl() {
        return this.pawnControl;
    }

    public void movePawn(Vector3f walkdirection) {
        if (this.pawnControl != null || walkdirection != new Vector3f(0, 0, 0)) {
            try {
                this.pawnControl.setWalkDirection(walkdirection);

                this.setLocalTranslation(this.pawnControl.getPhysicsLocation());
            } catch (Exception e) {
                System.out.println("UNABLE TO MOVE! " + e.toString());
            }
        }
    }

    public void knockBack(Vector3f direction) {
        Vector3f knockDirection = direction;
        knockDirection.multLocal(this.knockBackWeakness);
        this.movePawn(knockDirection);
    }

    public void knockBackJump() {
        float jumpSpeed = this.pawnControl.getJumpSpeed();
        this.pawnControl.setJumpSpeed(knockBackJumpSpeed);
        this.pawnControl.jump();
        this.pawnControl.setJumpSpeed(jumpSpeed);
    }

    public void jump() {
        if (this.pawnControl.onGround()) {
            this.pawnControl.jump();
        }
    }
    
    public void jump(float jumpSpeed) {
        float jumpSpeedOrg = pawnControl.getJumpSpeed();
        pawnControl.setJumpSpeed(jumpSpeed);
        if (pawnControl.onGround())
        pawnControl.jump();
        pawnControl.setJumpSpeed(jumpSpeedOrg);
    }

    public boolean gotKilled(float damage) {
        if (this.health >= 0.1f) {
            this.knockBackTimer = 0;
            this.knockBackJump();

            this.health -= damage;
            if (this.health < 0.1f) {
                //Explode here
                this.pawnControl.setPhysicsLocation(new Vector3f(0, -10000f, 0));
                this.removeFromParent();

                return true;
            }
        }

        return false;
    }
}
