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

    private AudioNode bullet_snd;
    private AudioNode empty_snd;
    private float currentEnergy;
    private float fireTimer;
    private float energyTimer;
    //finals
    final float FIRE_RATE = 0.2f;
    final float RECHARGE_RATE = 0.6f;
    final float SPREAD = 0.3f;

    public Weapon(AssetManager assetManager) {
        super();
        
        this.setName("Weapon");
        initModel(assetManager);
        initAudio(assetManager);
        
        energyTimer = 0;
        fireTimer = 0;
        currentEnergy = 50f;
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
        return currentEnergy;
    }

    public float getSpread() {
        return SPREAD;
    }

    public void increaseTimer(float tpf) {
        energyTimer += tpf;
        fireTimer += tpf;
    }

    public void restoreEnergy(boolean isMoving) {
        if (energyTimer >= RECHARGE_RATE && currentEnergy < 50) {
            if (isMoving) {
                currentEnergy++;
            } else {
                currentEnergy += 0.5f;
            }
            energyTimer = 0;
        }
    }

    public boolean shoot() {
        if (fireTimer >= FIRE_RATE && currentEnergy > 0f && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            bullet_snd.stop();
            bullet_snd.play();

            currentEnergy--;

            fireTimer = 0;
            return true;
        } else if (fireTimer >= FIRE_RATE && currentEnergy == 0f && empty_snd.getStatus() == AudioSource.Status.Stopped) {
            empty_snd.play();

            fireTimer = 0;
        }

        return false;
    }
}
