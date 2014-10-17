package mygame;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Bralts & Hulsman
 */
public class MenuState extends AbstractAppState implements ScreenController {

    public Node rootNode;
    public Main app;
    public MenuState GUI;
    public AssetManager assetManager;
    private AppStateManager stateManager;
    private BulletAppState physics;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort viewPort;
    private Nifty nifty;
    public Screen screen;
    public Window startMenu;

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
        this.physics = this.stateManager.getState(BulletAppState.class);
        this.flyCam = this.stateManager.getState(FlyCamAppState.class).getCamera();
        this.viewPort = this.app.getViewPort();
        this.inputManager = this.app.getInputManager();
        this.audioRenderer = this.app.getAudioRenderer();
        startMenu();
    }

    public void startGame() {
        stateManager.attach(new GameRunningState());
        System.out.print("Booting up game!");
        nifty.exit();
    }

    public void startMenu() {
        flyCam.setEnabled(false);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        nifty = niftyDisplay.getNifty();
        MenuState ggs = new MenuState(app);
        nifty.fromXml("Interface/MenuNifty.xml", "start", ggs);
        nifty.gotoScreen("start");
        viewPort.addProcessor(niftyDisplay);
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }
}
