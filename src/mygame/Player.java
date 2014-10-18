package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bralts & Hulsman
 */
public class Player extends LivingThing {

    private int maxHealth;
    private int killCounter;
    private int waveCounter;
    private boolean moving;
    private AudioNode jump_snd;
    private AudioNode hit1_snd, hit2_snd, hit3_snd, hit4_snd;
    private AudioNode walk1_snd, walk2_snd;

    public Player(AssetManager assetManager, BulletAppState bulletAppState, int maxHealth, Vector3f spawnLocation) {
        super();

        this.setName("Player");
        initCharacterControl(bulletAppState, spawnLocation);
        initAudio(assetManager);

        this.maxHealth = maxHealth;
        health = maxHealth;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 3f;

        killCounter = 0;
        waveCounter = 0;
        moving = false;
    }

    private void initCharacterControl(BulletAppState bulletAppState, Vector3f spawnLocation) {
        capsuleShape = new CapsuleCollisionShape(1f, 3.75f, 1);
        pawnControl = new CharacterControl(capsuleShape, 0.05f);
        pawnControl.setJumpSpeed(15f);

        this.addControl(pawnControl);
        bulletAppState.getPhysicsSpace().add(pawnControl);

        pawnControl.setPhysicsLocation(spawnLocation);
    }

    private void initAudio(AssetManager assetManager) {
        jump_snd = new AudioNode(assetManager, "Sounds/Player/Speler_spring.wav", false);
        jump_snd.setPositional(false);
        jump_snd.setLooping(false);
        jump_snd.setVolume(1f);
        this.attachChild(jump_snd);
        
        walk1_snd = new AudioNode(assetManager, "Sounds/Player/Lopen1.wav", false);
        walk1_snd.setPositional(false);
        walk1_snd.setLooping(false);
        walk1_snd.setVolume(1f);
        this.attachChild(walk1_snd);
        
        walk2_snd = new AudioNode(assetManager, "Sounds/Player/Lopen2.wav", false);
        walk2_snd.setPositional(false);
        walk2_snd.setLooping(false);
        walk2_snd.setVolume(1f);
        this.attachChild(walk2_snd);

        hit1_snd = new AudioNode(assetManager, "Sounds/Player/Speler_auw1.wav", false);
        hit1_snd.setPositional(false);
        hit1_snd.setLooping(false);
        hit1_snd.setVolume(1f);
        this.attachChild(hit1_snd);

        hit2_snd = new AudioNode(assetManager, "Sounds/Player/Speler_auw2.wav", false);
        hit2_snd.setPositional(false);
        hit2_snd.setLooping(false);
        hit2_snd.setVolume(1f);
        this.attachChild(hit2_snd);

        hit3_snd = new AudioNode(assetManager, "Sounds/Player/Speler_auw3.wav", false);
        hit3_snd.setPositional(false);
        hit3_snd.setLooping(false);
        hit3_snd.setVolume(1f);
        this.attachChild(hit3_snd);

        hit4_snd = new AudioNode(assetManager, "Sounds/Player/Speler_auw4.wav", false);
        hit4_snd.setPositional(false);
        hit4_snd.setLooping(false);
        hit4_snd.setVolume(1f);
        this.attachChild(hit4_snd);

    }

    public void restoreHealth() {
        health = maxHealth;
    }

    public void groan() {
        jump_snd.stop();
        jump_snd.play();
    }

    public int getKills() {
        return killCounter;
    }

    public void addKill() {
        killCounter++;
    }

    public int getSurvivedWaves() {
        return waveCounter;
    }

    public void addSurvivedWave() {
        waveCounter++;
    }

    public boolean isMoving() {
        return moving;
    }

    public void isMoving(boolean isMoving) {
        moving = isMoving;
    }

    public void playWalkSound() {
        float randomSound = FastMath.rand.nextFloat();
        if (randomSound < 0.5f) {
            walk1_snd.setLocalTranslation(this.getLocalTranslation());
            walk1_snd.play();
        } else {
            walk2_snd.setLocalTranslation(this.getLocalTranslation());
            walk2_snd.play();
        }
    }

    @Override
    public boolean gotKilled(float damage) {
        float randomSound = FastMath.rand.nextFloat();
        if (randomSound < 0.25f) {
            hit1_snd.setLocalTranslation(this.getLocalTranslation());
            hit1_snd.play();
        } else if (randomSound < 0.5f) {
            hit2_snd.setLocalTranslation(this.getLocalTranslation());
            hit2_snd.play();
        } else if (randomSound < 0.75f) {
            hit3_snd.setLocalTranslation(this.getLocalTranslation());
            hit3_snd.play();
        } else {
            hit4_snd.setLocalTranslation(this.getLocalTranslation());
            hit4_snd.play();
        }
        return super.gotKilled(damage);
    }
}
