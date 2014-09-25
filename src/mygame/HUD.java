package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author Hulsman
 */
public class HUD{
    AssetManager assetManager;
    Node guiNode;
    AppSettings settings;
    BitmapText EnergyText,HealthText;
    BitmapFont GuiFont;
    Picture healthbarInline,energybarInline;

    public HUD(AssetManager assetManager, Node guiNode, AppSettings settings, BitmapFont guifont) {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.settings = settings;
        this.GuiFont = guifont;
    }
    
    public void initBars()
    {
        Picture healthbarOutline = new Picture("HUD bar_outline");
        healthbarOutline.setImage(assetManager, "Textures/bar_outline.png", true);
        healthbarInline = new Picture("HUD bar_inline");
        healthbarInline.setImage(assetManager, "Textures/healthbar_inline.png", true);
        Picture energybarOutline = new Picture("HUD bar_outline");
        energybarOutline.setImage(assetManager, "Textures/bar_outline.png", true);
        energybarInline = new Picture("HUD bar_inline");
        energybarInline.setImage(assetManager, "Textures/energybar_inline.png", true);
        
        healthbarOutline.setWidth(120);
        healthbarOutline.setHeight(20);
        healthbarInline.setWidth(120);
        healthbarInline.setHeight(20);
        energybarOutline.setWidth(120);
        energybarOutline.setHeight(20);
        energybarInline.setWidth(120);
        energybarInline.setHeight(20);
        
        healthbarOutline.setPosition(1, 50);
        healthbarInline.setPosition(1, 50);
        energybarOutline.setPosition(1, 20);
        energybarInline.setPosition(1, 20);
        
        guiNode.attachChild(healthbarOutline);
        guiNode.attachChild(healthbarInline);
        guiNode.attachChild(energybarOutline);
        guiNode.attachChild(energybarInline);
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
    
public void updateHUD(float RayGunEnergy, float playerHealth)
{
    energybarInline.setWidth(120 * RayGunEnergy);
    // TODO : Texture fix
    //energybarInline.setPosition(1 * RayGunEnergy, 20);
}
}
