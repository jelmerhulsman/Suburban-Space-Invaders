package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Hulsman
 */
public class Bullet extends Node{
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private ViewPort viewPort;
    
    private Material bullet_mat;
    private Vector3f loc;
    private Quaternion rot;
    private Vector3f dir;
    
    public Bullet(AssetManager assetManager, BulletAppState bulletAppState, Vector3f loc, Quaternion rot, Vector3f dir) {
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        
        this.loc = loc;
        this.rot = rot;
        this.dir = dir;
                
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
        Geometry bullet = new Geometry("Bullet", c);
        bullet.setMaterial(bullet_mat);
        
        bullet.setLocalTranslation(loc);
        bullet.rotate(rot);
        this.attachChild(bullet);
    }
    
    private void initPhysicsControl() {
        RigidBodyControl bullet_phy = new RigidBodyControl();
        this.addControl(bullet_phy);
        
        bullet_phy.setLinearVelocity(dir.mult(250f));

        bulletAppState.getPhysicsSpace().add(bullet_phy);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
    }
}
