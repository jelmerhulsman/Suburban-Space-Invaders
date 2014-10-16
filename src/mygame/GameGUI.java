/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.scene.Node;

/**
 *
 * @author Bralts & Hulsman
 */
public class GameGUI extends AbstractAppState {

    public Node rootNode;
    public SimpleApplication app;
    public GameGUI GUI;
    public AssetManager assetManager;
    private AppStateManager stateManager;
    private BulletAppState physics;
    private FlyByCamera flyCam;
    private boolean firstTime;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.physics = this.stateManager.getState(BulletAppState.class);
        this.flyCam = this.stateManager.getState(FlyCamAppState.class).getCamera();
        firstTime = true;
        this.GUI = new GameGUI();
        stateManager.attach(new Game());
    }
}
