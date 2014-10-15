package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

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
        if (pawnControl != null || direction != new Vector3f(0, 0, 0)) {
            try {
                pawnControl.setWalkDirection(direction);
                this.setLocalTranslation(pawnControl.getPhysicsLocation());
            } catch (Exception e) {
                System.out.println("UNABLE TO MOVE! " + e.toString());
            }
        }
    }

    public void knockBack(Vector3f direction) {
        Vector3f knockDirection = new Vector3f(direction);
        knockDirection = knockDirection.multLocal(knockBackWeakness);
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
