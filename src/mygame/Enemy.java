/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

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
        this.setName("Enemy");
        initModel();
        initCharacterControl();

        health = 10f;
        maxHealth = 10f;

        pawnControl.setUseViewDirection(false);
        pawnControl.setPhysicsLocation(new Vector3f(-100, 100f, -100f));
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

    private void initGhostCollision() {
        // Alien -> collision shape combined by child collision shapes
        CompoundCollisionShape alienCollisionShape = new CompoundCollisionShape();

        // Legs and tail -> collision boxes
        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(5f, 0.25f, 0.25f));
        alienCollisionShape.addChildShape(box, new Vector3f(0, 0.125f, 1.2f));
        alienCollisionShape.addChildShape(box, new Vector3f(0, 0.125f, -1.2f));

        box = new BoxCollisionShape(new Vector3f(3f, 0.25f, 0.25f));
        alienCollisionShape.addChildShape(box, new Vector3f(0, 0.125f, 0.7f));
        alienCollisionShape.addChildShape(box, new Vector3f(0, 0.125f, -0.7f));

        box = new BoxCollisionShape(new Vector3f(0.9f, 0.25f, 2f));
        alienCollisionShape.addChildShape(box, new Vector3f(0, 0.125f, -3.6f));

        // Body -> collision cone
        ConeCollisionShape cone = new ConeCollisionShape(1.5f, 2.7f, 1);
        alienCollisionShape.addChildShape(cone, new Vector3f(0, 1.35f, -0.2f));

        // Neck -> collision cyllinders and sphere
        CylinderCollisionShape cylinder = new CylinderCollisionShape(new Vector3f(0.3f, 0.6f, 0.3f), 1);
        alienCollisionShape.addChildShape(cylinder, new Vector3f(0, 2.4f, 0.15f));

        SphereCollisionShape sphere = new SphereCollisionShape(0.25f);
        alienCollisionShape.addChildShape(sphere, new Vector3f(0, 3f, 0.4f));

        cylinder = new CylinderCollisionShape(new Vector3f(0.2f, 0.2f, 0.3f), 2);
        alienCollisionShape.addChildShape(cylinder, new Vector3f(0, 3.3f, 0.8f));

        // Eye (head) -> collision sphere
        sphere = new SphereCollisionShape(1.3f);
        alienCollisionShape.addChildShape(sphere, new Vector3f(0, 3.35f, 2.25f));

        ghostControl = new GhostControl(alienCollisionShape);
        model.addControl(ghostControl);

        bulletAppState.getPhysicsSpace().add(model);
    }

    public void checkHP() {
        if (this.health <= 0f) {
            /* A colored lit cube. Needs light source! */
            Box boxMesh = new Box(0.5f, 100f, 0.5f);
        }
    }

    public void gotHit() {
        this.health--;
        if (this.health == 0f) {
            /* A colored lit cube. Needs light source! */
            Box boxMesh = new Box(0.5f, 100f, 0.5f);
            Geometry boxGeo = new Geometry("Colored Box", boxMesh);
            Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            boxMat.setBoolean("UseMaterialColors", true);
            boxMat.setColor("Ambient", ColorRGBA.Cyan);
            boxMat.setColor("Diffuse", ColorRGBA.Cyan);
            boxGeo.setMaterial(boxMat);
            this.attachChild(boxGeo);
        }
    }
}
