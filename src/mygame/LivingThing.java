/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Moreno
 */
public class LivingThing extends Node {

    protected float health, maxHealth;
    protected CharacterControl pawnControl;
    protected CapsuleCollisionShape capsuleShape;

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

    public void Move(Vector3f walkdirection) {
        if(walkdirection != null || walkdirection != new Vector3f(0,0,0) )
        {
        pawnControl.setWalkDirection(walkdirection);
        
        this.setLocalTranslation(pawnControl.getPhysicsLocation());
        }

    }

    public void Jump() {
        pawnControl.jump();
    }

    public void updatePawn() {
    }
}
