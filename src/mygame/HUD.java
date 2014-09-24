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

    public HUD(AssetManager assetManager, Node guiNode, AppSettings settings, BitmapFont guifont) {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.settings = settings;
        this.GuiFont = guifont;
    }
    
    public void initText()
    {
        GuiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        EnergyText = new BitmapText(GuiFont, false);
        EnergyText.setSize(GuiFont.getCharSet().getRenderedSize());
        EnergyText.setText("");
        EnergyText.setLocalTranslation(300, EnergyText.getLineHeight(), 0);
        
        HealthText = new BitmapText(GuiFont, false);
        HealthText.setSize(GuiFont.getCharSet().getRenderedSize());
        HealthText.setText("");
        HealthText.setLocalTranslation(300, HealthText.getLineHeight() + 20, 0);
        
        guiNode.attachChild(EnergyText);
        guiNode.attachChild(HealthText);
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
    EnergyText.setText("Energy Level " + (int)RayGunEnergy);
    HealthText.setText("Health " + (int)playerHealth);
}
}
