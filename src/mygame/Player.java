/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bralts & Hulsman
 */
public class Player extends LivingThing{

    

    public Player() {

        capsuleShape = new CapsuleCollisionShape(1f, 3.75f, 1);
        pawn = new CharacterControl(capsuleShape, 0.05f);
        pawn.setJumpSpeed(15f);
        pawn.setFallSpeed(30f);
        pawn.setGravity(30f);
        pawn.setPhysicsLocation(new Vector3f(0, 15f, 0));

        health = 100f;
        maxHealth = 100f;
    }




}
