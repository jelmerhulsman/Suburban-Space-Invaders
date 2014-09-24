package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Hulsman
 */
public class Weapon extends Node {

    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private ViewPort viewPort;
    private Material bullet_mat;
    private AudioNode bullet_snd;
    float damage;
    float rateOfFire;

    public Weapon(AssetManager assetManager, BulletAppState bulletAppState, ViewPort viewPort) {
        super();
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.viewPort = viewPort;
        
        damage = 5f; //5 damage per shot
        rateOfFire = 1/5f; //5 shots per second
        
        initModel();
        initMaterial();
        initAudio();
    }

    private void initModel() {
        Spatial model = assetManager.loadModel("Models/GranadeLauncher/GranadeLauncher.j3o");
        this.attachChild(model);
    }

    private void initMaterial() {
        bullet_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bullet_mat.setColor("Color", ColorRGBA.Green);
        bullet_mat.setColor("GlowColor", ColorRGBA.Green);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
    }

    private void initAudio() {
        bullet_snd = new AudioNode(assetManager, "Sounds/space_gun.wav", false);
        bullet_snd.setPositional(false);
        bullet_snd.setLooping(false);
        bullet_snd.setVolume(0.75f);
        this.attachChild(bullet_snd);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getRateOfFire() {
        return rateOfFire;
    }

    public void setRateOfFire(float rateOfFire) {
        this.rateOfFire = rateOfFire;
    }

    public void shoot(Vector3f loc, Quaternion rot, Vector3f dir) {
        //All actions to create and fire a projectile
        Cylinder c = new Cylinder(100, 100, 0.075f, 1f, true);
        Geometry geom = new Geometry("Bullet", c);
        geom.setMaterial(bullet_mat);

        geom.setLocalTranslation(loc);
        geom.rotate(rot);

        RigidBodyControl physics = new RigidBodyControl();
        geom.addControl(physics);
        physics.setLinearVelocity(dir.mult(350));
        physics.setGravity(Vector3f.ZERO);
        bulletAppState.getPhysicsSpace().add(physics);

        bullet_snd.stop();
        bullet_snd.play();

        this.attachChild(geom);
    }
}
