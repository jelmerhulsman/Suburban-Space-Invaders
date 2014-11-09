package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import org.lwjgl.opengl.Display;

/**
 * HUD class, manages the game HUD
 *
 * @author Bralts & Hulsman
 */
public class GameHUD {

    BitmapText energyText, healthText, killsText, waveText;
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
        initCrosshair(assetManager, guiNode, crossHairSize);
    }

    //Initializes bars for the HUD
    private void initBars(AssetManager assetManager, Node guiNode) {

        inlineHealthBar = new Picture("inHealthbar");
        inlineHealthBar.setImage(assetManager, "Textures/bar_health.png", true);
        inlineHealthBar.setWidth(HEALTH_BAR_WIDTH);
        inlineHealthBar.setHeight(24);
        inlineHealthBar.setPosition(HEALTH_BAR_X, HEALTH_BAR_Y);
        guiNode.attachChild(inlineHealthBar);

        inlineEnergyBar = new Picture("inEnergybar");
        inlineEnergyBar.setImage(assetManager, "Textures/bar_energy.png", true);
        inlineEnergyBar.setWidth(ENERGY_BAR_WIDTH);
        inlineEnergyBar.setHeight(10);
        inlineEnergyBar.setPosition(ENERGY_BAR_X, ENERGY_BAR_Y);
        guiNode.attachChild(inlineEnergyBar);

        Picture outlineBar = new Picture("outBar");
        outlineBar.setImage(assetManager, "Textures/bar.png", true);
        outlineBar.setWidth(308);
        outlineBar.setHeight(43);
        float barX = 10;
        float barY = (Display.getHeight() - 10) - 43;
        outlineBar.setPosition(barX, barY);
        guiNode.attachChild(outlineBar);
    }

    //Initializes score for the HUD
    private void initScore(AssetManager assetManager, Node guiNode) {
        final float SCALE_PICTURE = 0.4f;
        final float SCALE_FONT = 1.5f;

        int width = 228;
        int height = 68;
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/PokemonSolid.fnt");
        Picture kills = new Picture("Score");
        kills.setImage(assetManager, "Textures/text_kills.png", true);
        kills.setWidth(width);
        kills.setHeight(height);
        kills.scale(SCALE_PICTURE);
        kills.setLocalTranslation(10, 30, 0);
        guiNode.attachChild(kills);

        killsText = new BitmapText(guiFont, false);
        killsText.setSize(guiFont.getCharSet().getRenderedSize());
        killsText.setText("0");
        killsText.scale(SCALE_FONT);
        killsText.setLocalTranslation(10, 25, 0);
        guiNode.attachChild(killsText);

        width = 290;
        Picture waves = new Picture("Waves");
        waves.setImage(assetManager, "Textures/text_waves.png", true);
        waves.setWidth(290);
        waves.setHeight(68);
        waves.scale(SCALE_PICTURE);
        waves.setLocalTranslation(Display.getWidth() - width * SCALE_PICTURE - 10, 30, 0);
        guiNode.attachChild(waves);

        waveText = new BitmapText(guiFont, false);
        waveText.setSize(guiFont.getCharSet().getRenderedSize());
        waveText.setText("0");
        waveText.scale(SCALE_FONT);
        waveText.setLocalTranslation(Display.getWidth() - width * SCALE_PICTURE + 75, 25, 0);
        guiNode.attachChild(waveText);
    }

    //Initializes crosshair for the HUD
    private void initCrosshair(AssetManager assetManager, Node guiNode, int size) {
        Picture crosshair = new Picture("HUD Picture");
        crosshair.setImage(assetManager, "Textures/crosshair_neon.png", true);

        crosshair.setWidth(size);
        crosshair.setHeight(size);

        float width = Display.getWidth() / 2 - size / 2;
        float height = Display.getHeight() / 2 - size / 2;
        crosshair.setPosition(width, height);

        guiNode.attachChild(crosshair);
    }

    /**
     * Updates the bars in the hud
     *
     * @param percentageHealth
     * @param percentageEnergy
     */
    public void updateBars(float percentageHealth, float percentageEnergy) {
        inlineHealthBar.setWidth(HEALTH_BAR_WIDTH * percentageHealth);
        inlineEnergyBar.setWidth(ENERGY_BAR_WIDTH * percentageEnergy);
    }

    /**
     * Updates the score in the hud
     *
     * @param kills
     * @param waves
     */
    public void updateScore(int kills, int waves) {
        killsText.setText("" + kills);
        waveText.setText("" + waves);
    }
}
