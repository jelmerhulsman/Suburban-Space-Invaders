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
        pawnControl.setPhysicsLocation(new Vector3f(0, 15f, -5f));
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
    
    public void rotateAndMove(Vector3f loc) {
        float playerDist = loc.distance(this.getLocalTranslation());
        this.lookAt(new Vector3f(loc.x, 0, loc.z), new Vector3f(0,1,0));
    }
    
    public void gotHit(Vector3f loc) {
        this.health--;
        //this.Jump();
        Vector3f knockBackDirection = new Vector3f(-loc.x, 0, -loc.z).multLocal(0.5f);
        this.Move(knockBackDirection);
        
        if (this.health == 0f)
        {
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
