package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bralts & Hulsman
 */
public class Player extends LivingThing {

    public int killCounter;
    public int waveCounter;
    public boolean isMoving;
    public AudioNode jump_snd;

    public Player(AssetManager assetManager, BulletAppState bulletAppState, Vector3f spawnLocation) {
        super();

        this.setName("Player");
        initCharacterControl(bulletAppState, spawnLocation);
        initAudio(assetManager);

        health = 100f;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 3f;

        killCounter = 0;
        waveCounter = 0;

        isMoving = false;
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
        jump_snd = new AudioNode(assetManager, "Sounds/hiccup.wav", false);
        jump_snd.setPositional(false);
        jump_snd.setLooping(false);
        jump_snd.setVolume(0.75f);
        this.attachChild(jump_snd);
    }
    
    public void restoreHealth() {
        health = 100f;
    }
    
    public void groan() {
        jump_snd.stop();
        jump_snd.play();
    }
}
