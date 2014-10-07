package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.Timer;

/**
 *
 * @author Hulsman
 */
public class Weapon extends Node {

    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private ViewPort viewPort;
    private AudioNode bullet_snd;
    private AudioNode empty_snd;
    private float damage;
    private float fireRate;
    private float currentEnergy;
    private float maxEnergy;
    private float rechargeRate;
    private float spread;
    private Timer fireTimer;
    private Timer energyTimer;
    public boolean isShooting;

    public Weapon(AssetManager assetManager, BulletAppState bulletAppState, ViewPort viewPort, Timer timer) {
        super();
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.viewPort = viewPort;

        damage = 5f; // 5 damage per shot
        fireRate = 1 / 5f; // 5 shots per second
        currentEnergy = 50f; // also know as ammo
        maxEnergy = 50f; // maximum ammo
        rechargeRate = 1 / 2f; // recharge 2 shots per second
        spread = 0.3f;

        fireTimer = timer;
        fireTimer.reset();
        energyTimer = timer;
        energyTimer.reset();

        initModel();
        initAudio();
    }

    private void initModel() {
        Spatial model = assetManager.loadModel("Models/GrenadeLauncher/GrenadeLauncher.j3o");
        this.attachChild(model);
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

    public float getSpread() {
        return spread;
    }

    public void restoreEnergy() {
        if (energyTimer.getTimeInSeconds() >= rechargeRate && currentEnergy < maxEnergy && isShooting == false) {
            currentEnergy++;
            energyTimer.reset();
        }
    }

    public void recoil() {
    }

    public boolean shoot() {
        if (fireTimer.getTimeInSeconds() >= fireRate && currentEnergy > 0f && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            bullet_snd.stop();
            bullet_snd.play();

            currentEnergy--;
            fireTimer.reset();
            recoil();

            return true;
        } else if (fireTimer.getTimeInSeconds() >= fireRate && currentEnergy == 0f && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            empty_snd.play();
            fireTimer.reset();
        }

        return false;
    }

    public float getEnergy() {
        return currentEnergy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }
}
