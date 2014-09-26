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
    protected CharacterControl pawn;
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
        return pawn;
    }

    public void Move(Vector3f walkdirection) {
        pawn.setWalkDirection(walkdirection);
        this.setLocalTranslation(pawn.getPhysicsLocation());
    }

    public void Jump() {
        pawn.jump();
    }

    public void updatePawn() {
    }
}
