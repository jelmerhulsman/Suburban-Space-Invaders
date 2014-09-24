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
import com.jme3.system.Timer;

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
    private AudioNode empty_snd;
    private float damage;
    private float fireRate;
    private float currentEnergy;
    private float maxEnergy;
    private float rechargeRate;
    private Timer fireTimer;
    private Timer energyTimer;

    public Weapon(AssetManager assetManager, BulletAppState bulletAppState, ViewPort viewPort, Timer timer) {
        super();
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.viewPort = viewPort;
        
        damage = 5f; // 5 damage per shot
        fireRate = 1/5f; // 5 shots per second
        currentEnergy = 50f; // also know as ammo
        maxEnergy = 50f; // maximum ammo
        rechargeRate = 1/2f; // recharge 2 shots per second
        
        fireTimer = timer;
        fireTimer.reset();
        energyTimer = timer;
        energyTimer.reset(); 
        
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
        bullet_mat.setColor("Color", ColorRGBA.Yellow);
        bullet_mat.setColor("GlowColor", ColorRGBA.Yellow);
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
        
        empty_snd = new AudioNode(assetManager, "Sounds/no_energy.wav", false);
        empty_snd.setPositional(false);
        empty_snd.setLooping(false);
        empty_snd.setVolume(0.75f);
        this.attachChild(empty_snd);
    }
    
    public void restoreEnergy() {
        if (energyTimer.getTimeInSeconds() >= rechargeRate && currentEnergy < maxEnergy)
        {
            currentEnergy++;
            energyTimer.reset();
        }
    }

    public void shoot(Vector3f loc, Quaternion rot, Vector3f dir) {
        if (fireTimer.getTimeInSeconds() >= fireRate && currentEnergy > 1f)
        {
            Cylinder c = new Cylinder(100, 100, 0.075f, 1f, true);
            Geometry geom = new Geometry("Bullet", c);
            geom.setMaterial(bullet_mat);

            geom.setLocalTranslation(loc);
            geom.rotate(rot);

            RigidBodyControl physics = new RigidBodyControl();
            geom.addControl(physics);
            physics.setLinearVelocity(dir.mult(250f));
            
            bulletAppState.getPhysicsSpace().add(physics);
            bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
            
            this.attachChild(geom);
            
            bullet_snd.stop();
            empty_snd.stop();
            
            bullet_snd.play();
            
            currentEnergy--;
            fireTimer.reset();
        } else if (currentEnergy < 1f) {
            bullet_snd.stop();
            empty_snd.stop();
            
            empty_snd.play();
        }
    }
}
