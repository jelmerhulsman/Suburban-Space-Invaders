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
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Hulsman
 */
public class Bullet extends Node{
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    
    private Material bullet_mat;
    private Geometry bullet_geometry;
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
        bullet_geometry = new Geometry();
        Cylinder c = new Cylinder(100, 100, 0.075f, 1f, true);
        
        bullet_geometry.setMesh(c);
        bullet_geometry.setMaterial(bullet_mat);
        
        this.attachChild(bullet_geometry);
    }
    
    private void initPhysicsControl() {
        SphereCollisionShape scs = new SphereCollisionShape(0.075f);
        control = new RigidBodyControl(scs, 200f);
        //control.setCcdMotionThreshold(0.01f);
        //control.setCcdSweptSphereRadius(0.01f);
        this.addControl(control);
        
        control.setPhysicsLocation(loc);
        control.setPhysicsRotation(rot);
        control.setLinearVelocity(dir.mult(250f));

        bulletAppState.getPhysicsSpace().add(control);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
    }
    
    public Vector3f getDirection()
    {
        return this.dir;
    }
    
    public void removeBullet ()
    {
        this.control.setPhysicsLocation(new Vector3f(0, -1500f, 0));
        this.removeFromParent();
    }
}
