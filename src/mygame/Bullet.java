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

    AssetManager assetManager;
    private Material bullet_mat;
    Geometry bullet_geo;
    Vector3f bullet_loc;
    Quaternion bullet_rot;
    Vector3f bullet_dir;
    BulletAppState bulletAppState;
    ViewPort viewPort;

    public Bullet(Vector3f loc, Quaternion rot, Vector3f dir,BulletAppState bulletAppState,AssetManager assetManager,ViewPort viewPort) {
        bullet_loc = loc;
        bullet_rot = rot;
        bullet_dir = dir;
        
        initMaterial();
        initGeometry();
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        RigidBodyControl bullet_phy = new RigidBodyControl();
        this.addControl(bullet_phy);
        bullet_phy.setLinearVelocity(dir.mult(250f));

        bulletAppState.getPhysicsSpace().add(bullet_phy);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);

        
    }

    private void initMaterial() {
        bullet_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bullet_mat.setColor("Color", ColorRGBA.Yellow);
        bullet_mat.setColor("GlowColor", ColorRGBA.Yellow);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
    }

    private void initGeometry() {
        Cylinder c = new Cylinder(100, 100, 0.075f, 1f, true);
        Geometry bullet = new Geometry("Bullet", c);
        bullet.setMaterial(bullet_mat);

        float locX = bullet_loc.x + ((FastMath.rand.nextFloat() - FastMath.rand.nextFloat()) * spread);
        float locY = bullet_loc.y + ((FastMath.rand.nextFloat() - FastMath.rand.nextFloat()) * spread);
        float locZ = bullet_loc.z + ((FastMath.rand.nextFloat() - FastMath.rand.nextFloat()) * spread);

        bullet.setLocalTranslation(locX, locY, locZ);
        bullet.rotate(bullet_rot);
        this.attachChild(bullet);
    }
}
