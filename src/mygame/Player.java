/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Bralts & Hulsman
 */
public class Player {

    private CharacterControl player;
    private float playerHealth, maxPlayerHealth;

    public Player() {

        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 3.75f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(15f);
        player.setFallSpeed(30f);
        player.setGravity(30f);
        player.setPhysicsLocation(new Vector3f(0, 15f, 0));

        playerHealth = 100f;
        maxPlayerHealth = 100f;
    }

    public CharacterControl getCharacterControl() {
        return player;
    }

    public void Move(Vector3f walkdirection) {
        player.setWalkDirection(walkdirection);
    }

    public void Jump() {
        player.jump();
    }

    public float getHealth() {
        return playerHealth;
    }

    public float getMaxHealth() {
        return maxPlayerHealth;
    }

    public void updatePlayer() {
        //TODO Player stuff
    }
}
