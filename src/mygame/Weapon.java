package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Bralts & Hulsman
 */
public class Weapon extends Node {

    private AssetManager assetManager;
    private AudioNode bullet_snd;
    private AudioNode empty_snd;
    private float damage;
    private float fireRate;
    private float currentEnergy;
    private float maxEnergy;
    private float rechargeRate;
    private float spread;
    private float fireTimer;
    private float energyTimer;
    public boolean isShooting;

    public Weapon(AssetManager assetManager) {
        super();
        this.assetManager = assetManager;

        energyTimer = 0;
        fireTimer = 0;

        currentEnergy = 50f; // also know as ammo
        rechargeRate = 1 / 2f; // recharge 2 shots per second

        damage = 5f; // 5 damage per shot
        fireRate = 1 / 5f; // 5 shots per second
        spread = 0.3f;



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

    public float getEnergy() {
        return currentEnergy;
    }

    public float getSpread() {
        return spread;
    }

    public void increaseTimer(float tpf) {
        energyTimer += tpf;
        fireTimer += tpf;
    }

    public void restoreEnergy() {
        if (energyTimer >= rechargeRate && currentEnergy < 50f && isShooting == false) {
            currentEnergy++;
            energyTimer = 0;
        }
    }

    public boolean shoot() {
        if (fireTimer >= fireRate && currentEnergy > 0f && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            bullet_snd.stop();
            bullet_snd.play();

            currentEnergy--;

            fireTimer = 0;
            return true;
        } else if (fireTimer >= fireRate && currentEnergy == 0f && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            empty_snd.play();

            fireTimer = 0;
        }

        return false;
    }
}
