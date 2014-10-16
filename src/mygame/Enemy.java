package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Bralts & Hulsman
 */
public class Enemy extends LivingThing {

    private Spatial model;
    private RigidBodyControl deathControl;
    private Material beam_mat;
    private Geometry beam_geometry;

    public Enemy(AssetManager assetManager, BulletAppState bulletAppState, Vector3f spawnLocation) {
        super();
        
        this.setLocalTranslation(spawnLocation);
        
        this.setName("Enemy");
        initModel(assetManager);
        initCharacterControl(bulletAppState, spawnLocation);

        initMaterial(assetManager);
        initGeometry();
        initPhysicsControl();

        health = 100f;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 1f;
    }

    private void initModel(AssetManager assetManager) {
        // Model extents -> x:5, y:2.5, z:4.5
        model = assetManager.loadModel("Models/Alien/Alien.j3o");
        this.attachChild(model);
    }

    private void initCharacterControl(BulletAppState bulletAppState, Vector3f spawnLocation) {
        CylinderCollisionShape collisionShape = new CylinderCollisionShape(new Vector3f(1.5f, 2.5f, 1f), 1);
        pawnControl = new CharacterControl(collisionShape, 0.05f);
        pawnControl.setJumpSpeed(7.5f);
        pawnControl.setUseViewDirection(false);
        pawnControl.setPhysicsLocation(model.center().getWorldTranslation());

        this.addControl(pawnControl);
        bulletAppState.getPhysicsSpace().add(pawnControl);
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

    public void killEffect(BulletAppState bulletAppState) {
        model.removeFromParent();
        
        pawnControl.setEnabled(false);
        pawnControl.destroy();
        this.removeControl(pawnControl);

        this.attachChild(beam_geometry);
        this.addControl(deathControl);

        deathControl.setPhysicsLocation(this.getWorldTranslation());
        deathControl.setLinearVelocity(new Vector3f(0, 250f, 0));

        bulletAppState.getPhysicsSpace().add(deathControl);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
    }
}
