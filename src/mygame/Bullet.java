package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Hulsman
 */
public class Bullet extends Geometry{
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    
    private Material bullet_mat;
    public RigidBodyControl control;
    private Vector3f loc;
    private Quaternion rot;
    private Vector3f dir;
    
    public Bullet(AssetManager assetManager, BulletAppState bulletAppState, Vector3f loc, Quaternion rot, Vector3f dir) {
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        
        this.loc = loc;
        this.rot = rot;
        this.dir = dir;
        
        this.setName("Bullet");
        initMaterial();
        initGeometry();
        initPhysicsControl();
    }

    private void initMaterial() {
        bullet_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bullet_mat.setColor("Color", ColorRGBA.Yellow);
        bullet_mat.setColor("GlowColor", ColorRGBA.Yellow);
    }

    private void initGeometry() {
        Cylinder c = new Cylinder(100, 100, 0.075f, 1f, true);
        
        this.setMesh(c);
        this.setMaterial(bullet_mat);
        
        this.setLocalTranslation(loc);
        this.rotate(rot);
    }
    
    private void initPhysicsControl() {
        SphereCollisionShape scs = new SphereCollisionShape(0.075f);
        control = new RigidBodyControl(scs, 1f);
        this.addControl(control);
        
        control.setLinearVelocity(dir.mult(20f));

        bulletAppState.getPhysicsSpace().add(control);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
    }
}
