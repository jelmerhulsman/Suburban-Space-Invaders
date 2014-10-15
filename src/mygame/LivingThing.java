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
    protected RigidBodyControl deathControl;
    protected Material beam_mat;
    protected Geometry beam_geometry;
    protected CapsuleCollisionShape capsuleShape;
    protected float knockBackTimer = 1;

    public LivingThing(AssetManager assetManager) {
        initMaterial(assetManager);
        initGeometry();
        initPhysicsControl();

    }

    private void initMaterial(AssetManager assetManager) {
        beam_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        beam_mat.setColor("Color", ColorRGBA.Cyan);
        beam_mat.setColor("GlowColor", ColorRGBA.Cyan);
    }

    private void initGeometry() {
        beam_geometry = new Geometry();
        Sphere s = new Sphere(25, 8, 8f);

        beam_geometry.setMesh(s);
        beam_geometry.setMaterial(beam_mat);
    }

    private void initPhysicsControl() {
        SphereCollisionShape scs = new SphereCollisionShape(0.075f);
        deathControl = new RigidBodyControl(scs, 200f);
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

    public void knockBack(Vector3f direction) {
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

    public void killEffect(BulletAppState bulletAppState) {
        this.scale(0.5f);
        pawnControl.setEnabled(false);
        pawnControl.destroy();
        this.removeControl(pawnControl);

        this.attachChild(beam_geometry);
        this.addControl(deathControl);

        deathControl.setPhysicsLocation(this.getWorldTranslation());
        deathControl.setLinearVelocity(new Vector3f(0, 250f, 0));

        bulletAppState.getPhysicsSpace().add(deathControl);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
        
        this.removeFromParent();
    }
}
