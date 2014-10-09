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

    public Enemy(AssetManager assetManager, BulletAppState bulletAppState) {
        super();

        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;

        initModel();
        initCharacterControl();

        health = 10f;
        knockBackJumpSpeed = 5f;
        knockBackWeakness = 1f;

        pawnControl.setUseViewDirection(false);
        pawnControl.setPhysicsLocation(new Vector3f(-100, 100f, -100f));

        this.setName("Enemy");
        pawnControl.setCollideWithGroups(1);
    }

    private void initModel() {
        // Model bounds -> x:5, y:2.5, z:4.5
        model = assetManager.loadModel("Models/Alien/Alien.j3o");
        this.attachChild(model);
    }

    private void initCharacterControl() {
        CylinderCollisionShape ccs = new CylinderCollisionShape(new Vector3f(1.5f, 2.5f, 1f), 1);
        pawnControl = new CharacterControl(ccs, 0.05f);
        Vector3f loc = model.center().getWorldTranslation();
        pawnControl.setPhysicsLocation(loc);

        this.addControl(pawnControl);
        bulletAppState.getPhysicsSpace().add(pawnControl);
    }
}
