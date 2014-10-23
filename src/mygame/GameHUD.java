package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Bralts & Hulsman
 */
public class GameHUD {

    BitmapText energyText, healthText, scoreText, waveText;
    Picture inlineHealthBar, inlineEnergyBar;
    final int HEALTH_BAR_WIDTH = 300;
    final int HEALTH_BAR_X = 10 + 3;
    final int HEALTH_BAR_Y = (Display.getHeight() - 10) - 27;
    final int ENERGY_BAR_WIDTH = 200;
    final int ENERGY_BAR_X = 10 + 29;
    final int ENERGY_BAR_Y = (Display.getHeight() - 10) - 40;

    public GameHUD(AssetManager assetManager, Node guiNode, int crossHairSize) {
        initBars(assetManager, guiNode);
        initScore(assetManager, guiNode);
        initCrossHair(assetManager, guiNode, crossHairSize);
    }

    private void initBars(AssetManager assetManager, Node guiNode) {

        inlineHealthBar = new Picture("Bar");
        inlineHealthBar.setImage(assetManager, "Textures/bar_health.png", true);
        inlineHealthBar.setWidth(HEALTH_BAR_WIDTH);
        inlineHealthBar.setHeight(24);
        inlineHealthBar.setPosition(HEALTH_BAR_X, HEALTH_BAR_Y);
        guiNode.attachChild(inlineHealthBar);

        inlineEnergyBar = new Picture("Bar");
        inlineEnergyBar.setImage(assetManager, "Textures/bar_energy.png", true);
        inlineEnergyBar.setWidth(ENERGY_BAR_WIDTH);
        inlineEnergyBar.setHeight(10);
        inlineEnergyBar.setPosition(ENERGY_BAR_X, ENERGY_BAR_Y);
        guiNode.attachChild(inlineEnergyBar);

        Picture outlineBar = new Picture("Bar");
        outlineBar.setImage(assetManager, "Textures/bar.png", true);
        outlineBar.setWidth(308);
        outlineBar.setHeight(43);
        float barX = 10;
        float barY = (Display.getHeight() - 10) - 43;
        outlineBar.setPosition(barX, barY);
        guiNode.attachChild(outlineBar);
    }

    private void initScore(AssetManager assetManager, Node guiNode) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setText("Score : 0");
        scoreText.setLocalTranslation(10, 100, 0);
        guiNode.attachChild(scoreText);

        waveText = new BitmapText(guiFont, false);
        waveText.setSize(guiFont.getCharSet().getRenderedSize());
        waveText.setText("Waves survived : 0");
        waveText.setLocalTranslation(10, 80, 0);
        guiNode.attachChild(waveText);
    }

    public void initCrossHair(AssetManager assetManager, Node guiNode, int size) {
        Picture crosshair = new Picture("HUD Picture");
        crosshair.setImage(assetManager, "Textures/neon_crosshair.png", true);

        crosshair.setWidth(size);
        crosshair.setHeight(size);

        float width = Display.getWidth() / 2 - size / 2;
        float height = Display.getHeight() / 2 - size / 2;
        crosshair.setPosition(width, height);

        guiNode.attachChild(crosshair);
    }

    public void updateBars(float percentageHealth, float percentageEnergy) {
        inlineHealthBar.setWidth(HEALTH_BAR_WIDTH * percentageHealth);
        inlineEnergyBar.setWidth(ENERGY_BAR_WIDTH * percentageEnergy);
    }

    public void updateScore(int kills, int waves) {
        scoreText.setText("KILLS : " + kills);
        waveText.setText("WAVES SURVIVED : " + waves);
    }
}
