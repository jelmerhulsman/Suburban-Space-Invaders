package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author Hulsman
 */
public class HUD {
    AssetManager assetManager;
    Node guiNode;
    AppSettings settings;

    public HUD(AssetManager assetManager, Node guiNode, AppSettings settings) {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.settings = settings;
    }
    
    void initCrossHair(int size){
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/neon_crosshair.png", true);
        
        pic.setWidth(size);
        pic.setHeight(size);
        
        float width = settings.getWidth()/2 - size/2;
        float height = settings.getHeight()/2 - size/2;
        pic.setPosition(width, height);
        
        guiNode.attachChild(pic);
    }
}
