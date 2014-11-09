package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * Enemy class
 * @author Bralts & Hulsman
 */
public class Enemy extends LivingThing {

    private Spatial model;
    private RigidBodyControl deathControl;
    private Material beam_mat;
    private Geometry beam_geometry;
    private AudioNode hit1_snd, hit2_snd;
    private AudioNode aliendeath_snd;
    private AudioNode alienjump_snd;

    public Enemy(AssetManager assetManager, BulletAppState bulletAppState, int maxHealth, Vector3f spawnLocation) {
        super();

        this.setLocalTranslation(spawnLocation);

        this.setName("Enemy");
        initModel(assetManager);
        initCharacterControl(bulletAppState, spawnLocation);

        initAudio(assetManager);
        initMaterial(assetManager);
        initGeometry();
        initPhysicsControl();

        health = maxHealth;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 1f;
    }
    
     //Initializes the model for the class
    private void initModel(AssetManager assetManager) {
        // Model extents -> x:5, y:2.5, z:4.5
        model = assetManager.loadModel("Models/Alien/Alien.j3o");
        this.attachChild(model);
    }
    
     //Initializes the sound for the class
    public void initAudio(AssetManager assetManager) {
        hit1_snd = new AudioNode(assetManager, "Sounds/Alien/Alien_Hit1.wav", false);
        hit1_snd.setPositional(true);
        hit1_snd.setLooping(false);
        hit1_snd.setReverbEnabled(false);
        hit1_snd.setRefDistance(30f);
        hit1_snd.setMaxDistance(1000f);
        hit1_snd.setVolume(4f);
        this.attachChild(hit1_snd);

        hit2_snd = new AudioNode(assetManager, "Sounds/Alien/Alien_Hit2.wav", false);
        hit2_snd.setPositional(true);
        hit2_snd.setLooping(false);
        hit2_snd.setReverbEnabled(false);
        hit2_snd.setRefDistance(30f);
        hit2_snd.setMaxDistance(1000f);
        hit2_snd.setVolume(4f);
        this.attachChild(hit2_snd);

        aliendeath_snd = new AudioNode(assetManager, "Sounds/Alien/Alien_Dead.wav", false);
        aliendeath_snd.setPositional(true);
        aliendeath_snd.setLooping(false);
        aliendeath_snd.setReverbEnabled(false);
        aliendeath_snd.setRefDistance(30f);
        aliendeath_snd.setMaxDistance(1000f);
        aliendeath_snd.setVolume(8f);
        this.attachChild(aliendeath_snd);

        alienjump_snd = new AudioNode(assetManager, "Sounds/Alien/Alien_Spring.wav", false);
        alienjump_snd.setPositional(true);
        alienjump_snd.setLooping(false);
        alienjump_snd.setReverbEnabled(false);
        alienjump_snd.setRefDistance(30f);
        alienjump_snd.setMaxDistance(1000f);
        alienjump_snd.setVolume(0.6f);
        this.attachChild(alienjump_snd);
    }
    
     //Initializes character control for the class
    private void initCharacterControl(BulletAppState bulletAppState, Vector3f spawnLocation) {
        CylinderCollisionShape collisionShape = new CylinderCollisionShape(new Vector3f(1.5f, 2.5f, 1f), 1);
        pawnControl = new CharacterControl(collisionShape, 0.05f);
        pawnControl.setJumpSpeed(7.5f);
        pawnControl.setUseViewDirection(false);
        pawnControl.setPhysicsLocation(model.center().getWorldTranslation());

        this.addControl(pawnControl);
        bulletAppState.getPhysicsSpace().add(pawnControl);
    }
    
     //Initializes material for the class
    private void initMaterial(AssetManager assetManager) {
        beam_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        beam_mat.setColor("Color", ColorRGBA.Cyan);
        beam_mat.setColor("GlowColor", ColorRGBA.Cyan);
    }
    
    //Initializes geomtery for the class
    private void initGeometry() {
        beam_geometry = new Geometry();
        Sphere s = new Sphere(25, 8, 8f);

        beam_geometry.setMesh(s);
        beam_geometry.setMaterial(beam_mat);
    }
    
     //Initializes physics control for the class
    private void initPhysicsControl() {
        SphereCollisionShape scs = new SphereCollisionShape(0.075f);
        deathControl = new RigidBodyControl(scs, 200f);
    }
    
    /**
     * Spawns a huge beam on location of this enemy (removes enemy)
     * @param bulletAppState 
     */
    public void killEffect(BulletAppState bulletAppState) {
        aliendeath_snd.setLocalTranslation(this.getLocalTranslation());
        aliendeath_snd.play();

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

    /**
     * Plays random enemy hit sound and checks whether enemy dies or not
     * @param damage
     * @return killed
     */
    @Override
    public boolean gotKilled(float damage) {
        float randomSound = FastMath.rand.nextFloat();
        if (randomSound < 0.5) {
            hit1_snd.setLocalTranslation(this.getLocalTranslation());
            hit1_snd.play();
        } else {
            hit2_snd.setLocalTranslation(this.getLocalTranslation());
            hit2_snd.play();
        }
        return super.gotKilled(damage);
    }
    
    /**
     * Triggers jump for this enemy
     * @return onGround
     */
    @Override
    public boolean jump() {
        alienjump_snd.play();
        return super.jump();
    }
}
