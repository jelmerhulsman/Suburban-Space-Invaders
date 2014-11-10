package mygame;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * The menustate of the game.
 * @author Bralts & Hulsman
 */
public class MenuState extends AbstractAppState implements ScreenController {

    public Node rootNode;
    public Main app;
    public AssetManager assetManager;
    private AppStateManager stateManager;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort viewPort;
    private Nifty nifty;
    public AudioNode menuMusic;

    public MenuState(Main app) {
        this.app = app;
        this.stateManager = this.app.getStateManager();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (Main) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.flyCam = this.stateManager.getState(FlyCamAppState.class).getCamera();
        this.viewPort = this.app.getViewPort();
        this.inputManager = this.app.getInputManager();
        this.audioRenderer = this.app.getAudioRenderer();
        startMenu();
        startMenuMusic();
    }

    /**
     * Starts the gamerunning-state and properly exits the Nifty menu
     */
    public void startGame() {
        stateManager.detach(this);
        stateManager.attach(new GameRunningState());
        nifty.exit();
    }

    /**
     * Initialize the menu using data from the xml file
     */
    public void startMenu() {
        app.setDisplayFps(false);
        flyCam.setEnabled(false);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        nifty = niftyDisplay.getNifty();
        MenuState ggs = new MenuState(app);
        nifty.fromXml("Interface/MenuNifty.xml", "start", ggs);
        nifty.gotoScreen("start");
        viewPort.addProcessor(niftyDisplay);
        niftyDisplay.cleanup();
        ggs.cleanup();
    }

    /**
     * Starts the menu music However unable to stop music due to a nifty bug
     */
    public void startMenuMusic() {
        menuMusic = new AudioNode(assetManager, "Sounds/Menu_FX.wav", false);
        menuMusic.setPositional(false);
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.75f);
        rootNode.attachChild(menuMusic);
        menuMusic.play();
    }

    /**
     * Abstract method
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    /**
     * Abstract method
     */
    public void onStartScreen() {
    }

    /**
     * Abstract method
     */
    public void onEndScreen() {
    }
}
