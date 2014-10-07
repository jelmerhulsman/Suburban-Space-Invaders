package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.Timer;

/**
 *
 * @author Moreno
 */
public class LivingThing extends Node {

    protected float health, maxHealth;
    protected CharacterControl pawnControl;
    protected CapsuleCollisionShape capsuleShape;
    protected Timer timer;

    public LivingThing() {
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public CharacterControl getCharacterControl() {
        return pawnControl;
    }
    
    public void initTimer(Timer timer)
    {
        this.timer = timer;
    }

    public void Move(Vector3f walkdirection) {
        if (pawnControl != null || walkdirection != new Vector3f(0, 0, 0)) {
            try {
                pawnControl.setWalkDirection(walkdirection);

                this.setLocalTranslation(pawnControl.getPhysicsLocation());
            } catch (Exception e) {
                System.out.println("UNABLE TO MOVE! " + e.toString());
            }
        } else {
            return;
        }


    }
    
    public void Knockback(Vector3f direction)
    {
        Move(direction);
    }

    public void Jump() {
        pawnControl.jump();
    }

    public void updatePawn() {
    }
}
