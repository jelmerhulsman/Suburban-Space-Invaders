package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author Bralts & Hulsman
 */
public class GameHUD {

    BitmapText energyText, healthText, scoreText, waveText;
    Picture healthbarInline, energybarInline;
    
    final int posBarsY = 20;
    final int posBarsX = 1;
    

    public GameHUD(AssetManager assetManager, Node guiNode, AppSettings settings, BitmapFont guiFont, int crossHairSize) {
        initBars(assetManager, guiNode);
        initScore(assetManager, guiNode, settings, guiFont);
        initCrossHair(assetManager, guiNode, settings, crossHairSize);
    }
    
    private void initBars(AssetManager assetManager, Node guiNode) {
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

        healthbarOutline.setPosition(posBarsX, posBarsY + 30);
        healthbarInline.setPosition(posBarsX, posBarsY + 30);
        energybarOutline.setPosition(posBarsX, posBarsY);
        energybarInline.setPosition(posBarsX, posBarsY);

        guiNode.attachChild(healthbarOutline);
        guiNode.attachChild(healthbarInline);
        guiNode.attachChild(energybarOutline);
        guiNode.attachChild(energybarInline);
    }
    
    private void initScore(AssetManager assetManager, Node guiNode, AppSettings settings, BitmapFont guiFont)
    {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setText("Score : 0");
        scoreText.setLocalTranslation(300, scoreText.getLineHeight(), 0);
        guiNode.attachChild(scoreText);
        
        waveText = new BitmapText(guiFont, false);
        waveText.setSize(guiFont.getCharSet().getRenderedSize());
        waveText.setText("Waves survived : 0");
        waveText.setLocalTranslation(settings.getWidth() - 300, waveText.getLineHeight(), 0);
        guiNode.attachChild(waveText);
    }

    public void initCrossHair(AssetManager assetManager, Node guiNode, AppSettings settings, int size) {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/neon_crosshair.png", true);

        pic.setWidth(size);
        pic.setHeight(size);

        float width = settings.getWidth() / 2 - size / 2;
        float height = settings.getHeight() / 2 - size / 2;
        pic.setPosition(width, height);

        guiNode.attachChild(pic);
    }
    
    public void updateScore(int score, int waves)
    {
        scoreText.setText("Score : " + score);
        waveText.setText("Waves survived : " + waves);
    }

    public void updateHUD(float percentageEnergy, float percentageHealth) {
        energybarInline.setWidth(120 * percentageEnergy);
        energybarInline.setPosition(10 + ((1 - percentageEnergy * 10)), posBarsY);

        healthbarInline.setWidth(120 * percentageHealth);
        healthbarInline.setPosition(10 + ((1 - percentageHealth * 10)), posBarsY + 30);
    }
}
