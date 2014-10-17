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

    //final variables
    final private float FIRE_RATE = 0.2f;
    final private int MAX_ENERGY = 50;
    final private float RECHARGE_RATE = 0.6f;
    final private float SPREAD = 0.3f;
    //variables
    private AudioNode bullet_snd;
    private AudioNode empty_snd;
    private boolean isShooting;
    private int maxEnergy;
    private int energy;
    private float energyTimer;
    private float firingTimer;

    public Weapon(AssetManager assetManager, int maxEnergy) {
        super();

        this.setName("Weapon");
        initModel(assetManager);
        initAudio(assetManager);

        isShooting = false;
        this.maxEnergy = maxEnergy;
        energy = maxEnergy;
        energyTimer = 0;
        firingTimer = 0;
    }

    private void initModel(AssetManager assetManager) {
        Spatial model = assetManager.loadModel("Models/GrenadeLauncher/GrenadeLauncher.j3o");
        this.attachChild(model);
    }

    private void initAudio(AssetManager assetManager) {
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
        return energy;
    }

    public float getSpread() {
        return SPREAD;
    }

    public void increaseTimers(float tpf) {
        energyTimer += tpf;
        firingTimer += tpf;
    }

    public void restoreEnergy(boolean isMoving) {
        if (energyTimer >= RECHARGE_RATE && energy < maxEnergy && !isShooting) {
            if (isMoving) {
                energy++;
            }
            energy++;
            energyTimer = 0;
        }
    }

    public boolean shoot() {
        if (firingTimer >= FIRE_RATE && energy > 0 && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            bullet_snd.stop();
            bullet_snd.play();

            energy--;
            firingTimer = 0;

            isShooting = true;
            return true;
        } else if (firingTimer >= FIRE_RATE && energy == 0 && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            empty_snd.play();

            firingTimer = 0;
        }

        isShooting = false;
        return false;
    }
}
