package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Bralts & Hulsman
 */
public class Enemy extends LivingThing {

    Spatial model;
    
    public Enemy(AssetManager assetManager, BulletAppState bulletAppState, Vector3f location) {
        super();
        
        this.setName("Enemy");
        initModel(assetManager);
        initCharacterControl(bulletAppState);
        
        health = 10f;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 1f;
        
        pawnControl.setJumpSpeed(10f);
        pawnControl.setUseViewDirection(false);
        pawnControl.setPhysicsLocation(location);
    }

    private void initModel(AssetManager assetManager) {
        // Model bounds -> x:5, y:2.5, z:4.5
        model = assetManager.loadModel("Models/Alien/Alien.j3o");
        this.attachChild(model);
    }

    private void initCharacterControl(BulletAppState bulletAppState) {
        CylinderCollisionShape collisionShape = new CylinderCollisionShape(new Vector3f(1.5f, 2.5f, 1f), 1);
        pawnControl = new CharacterControl(collisionShape, 0.05f);
        Vector3f loc = model.center().getWorldTranslation();
        pawnControl.setPhysicsLocation(loc);

        this.addControl(pawnControl);
        bulletAppState.getPhysicsSpace().add(pawnControl);
    }
}
