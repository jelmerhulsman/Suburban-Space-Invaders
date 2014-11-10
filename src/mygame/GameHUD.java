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
    Picture inlineHealthBar, inlineEnergyBar, keymapping;
    BitmapFont guiFont;
    final int HEALTH_BAR_WIDTH = 300;
    final int ENERGY_BAR_WIDTH = 200;

    public GameHUD(AssetManager assetManager, Node guiNode, int crossHairSize) {
        guiFont = assetManager.loadFont("Interface/Fonts/PokemonSolid.fnt");
        
        initBars(assetManager, guiNode);
        initScore(assetManager, guiNode);
        
        keymapping = new Picture("HUD Picture");
        keymapping.setImage(assetManager, "Textures/keymapping.png", true);
        float width = Display.getWidth() * 0.9f;
        float height = Display.getHeight() * 0.9f;
        keymapping.setWidth(width);
        keymapping.setHeight(height);
        width = Display.getWidth() / 2f - width / 2f;
        height = Display.getHeight() / 2f - height / 2f;
        keymapping.setPosition(width, height);
        guiNode.attachChild(keymapping);
    }

    //Initializes bars for the HUD
    private void initBars(AssetManager assetManager, Node guiNode) {
        final int HEALTH_BAR_X = 10 + 3;
        final int HEALTH_BAR_Y = (Display.getHeight() - 10) - 27;
        final int ENERGY_BAR_X = 10 + 29;
        final int ENERGY_BAR_Y = (Display.getHeight() - 10) - 40;

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
        final float KILLS_X = 10f;
        final float KILLS_Y = 30f;
        Picture kills = new Picture("Score");
        kills.setImage(assetManager, "Textures/text_kills.png", true);
        kills.setWidth(width);
        kills.setHeight(height);
        kills.scale(SCALE_PICTURE);
        kills.setLocalTranslation(KILLS_X, KILLS_Y, 0);
        guiNode.attachChild(kills);

        killsText = new BitmapText(guiFont, false);
        killsText.setSize(guiFont.getCharSet().getRenderedSize());
        killsText.setText("0");
        killsText.scale(SCALE_FONT);
        killsText.setLocalTranslation(KILLS_X + (width * SCALE_PICTURE) / 2, KILLS_Y - 3, 0);
        guiNode.attachChild(killsText);

        width = 290;
        height = 68;
        final float WAVES_X = Display.getWidth() - (width * SCALE_PICTURE) - 10f;
        final float WAVES_Y = 30f;
        Picture waves = new Picture("Waves");
        waves.setImage(assetManager, "Textures/text_waves.png", true);
        waves.setWidth(width);
        waves.setHeight(height);
        waves.scale(SCALE_PICTURE);
        waves.setLocalTranslation(WAVES_X, WAVES_Y, 0);
        guiNode.attachChild(waves);

        waveText = new BitmapText(guiFont, false);
        waveText.setSize(guiFont.getCharSet().getRenderedSize());
        waveText.setText("0");
        waveText.scale(SCALE_FONT);
        waveText.setLocalTranslation(WAVES_X + (width * SCALE_PICTURE) / 2, WAVES_Y - 3, 0);
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

    /**
     * Trigger stopIntro screen
     */
    public void stopIntro(Node guiNode) {
        guiNode.detachChild(keymapping);
    }

    /**
     * Trigger game over screen
     *
     * @param assetManager
     * @param guiNode
     */
    public void gameOver(AssetManager assetManager, Node guiNode) {
        Picture black = new Picture("HUD Picture");
        black.setImage(assetManager, "Textures/black.jpg", false);
        black.setWidth(Display.getWidth() * 2);
        black.setHeight(Display.getHeight() * 2);
        black.setPosition(0, 0);
        guiNode.attachChild(black);

        BitmapText text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.setText("You lost! \n "
                + "You killed " + killsText.getText() + " enemies. \n "
                + "And you survided " + waveText.getText() + " waves!");
        text.scale(2f);
        text.setLocalTranslation(Display.getWidth() / 3, Display.getHeight() / 2, 0);
        guiNode.attachChild(text);
    }
}
