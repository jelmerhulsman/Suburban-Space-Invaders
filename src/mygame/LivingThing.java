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
        return health;
    }

    public CharacterControl getCharacterControl() {
        return pawnControl;
    }

    public void movePawn(Vector3f walkdirection) {
        if (pawnControl != null || walkdirection != new Vector3f(0, 0, 0)) {
            try {
                pawnControl.setWalkDirection(walkdirection);

                this.setLocalTranslation(pawnControl.getPhysicsLocation());
            } catch (Exception e) {
                System.out.println("UNABLE TO MOVE! " + e.toString());
            }
        }
    }
    
    public void knockBack(Vector3f direction)
    {
        Vector3f knockDirection = direction;
        knockDirection.multLocal(knockBackWeakness);
        movePawn(knockDirection);
    }
    
    public void knockBackJump() {
        float jumpSpeed = pawnControl.getJumpSpeed();
        pawnControl.setJumpSpeed(knockBackJumpSpeed);
        pawnControl.jump();
        pawnControl.setJumpSpeed(jumpSpeed);
    }
    
    public void jump() {
        if (pawnControl.onGround())
            pawnControl.jump();
    }

    public void gotHit(int damage) {
        health = health - damage;
        knockBackJump();
        
        if (health < 0.1f) {
            //Explode here
            //Kill counter ++
            pawnControl.setPhysicsLocation(new Vector3f(0, -1000f, 0));
            this.removeFromParent();
        }
    }
}
