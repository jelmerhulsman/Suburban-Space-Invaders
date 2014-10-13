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

    AssetManager assetManager;
    BulletAppState bulletAppState;
    Spatial model;
    GhostControl ghostControl;
    Vector3f location;
    CylinderCollisionShape ccs;

    public Enemy(AssetManager assetManager, BulletAppState bulletAppState, Vector3f location) {
        super();

        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.location = location;
        
        initModel();
        initCharacterControl();

        health = 10f;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 1f;
        
        pawnControl.setJumpSpeed(2.5f);
        pawnControl.setUseViewDirection(false);
        pawnControl.setPhysicsLocation(location);

        this.setName("Enemy");
    }

    private void initModel() {
        // Model bounds -> x:5, y:2.5, z:4.5
        model = assetManager.loadModel("Models/Alien/Alien.j3o");
        this.attachChild(model);
    }

    private void initCharacterControl() {
        ccs = new CylinderCollisionShape(new Vector3f(1.5f, 2.5f, 1f), 1);
        pawnControl = new CharacterControl(ccs, 0.05f);
        Vector3f loc = model.center().getWorldTranslation();
        pawnControl.setPhysicsLocation(loc);

        this.addControl(pawnControl);
        bulletAppState.getPhysicsSpace().add(pawnControl);
    }
}
