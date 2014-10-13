package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
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
    public AudioNode jump_snd;
    public boolean isMoving;

    public Player(AssetManager assetManager) {
        super();
        
        capsuleShape = new CapsuleCollisionShape(1f, 3.75f, 1);
        pawnControl = new CharacterControl(capsuleShape, 0.05f);
        pawnControl.setJumpSpeed(15f);
        pawnControl.setFallSpeed(30f);
        pawnControl.setGravity(30f);
        pawnControl.setPhysicsLocation(new Vector3f(0, 15f, 0));

        health = 100f;
        knockBackJumpSpeed = 10f;
        knockBackWeakness = 3f;
        
        killCounter = 0;
        waveCounter = 0;
        
        isMoving = false;
        
        this.setName("Player");
        initAudio(assetManager);
    }
    
    private void initAudio(AssetManager assetManager) {
        jump_snd = new AudioNode(assetManager, "Sounds/hiccup.wav", false);
        jump_snd.setPositional(false);
        jump_snd.setLooping(false);
        jump_snd.setVolume(0.75f);
        this.attachChild(jump_snd);
    }
    
    public void groan() {
        jump_snd.stop();
        jump_snd.play();
    }
}
