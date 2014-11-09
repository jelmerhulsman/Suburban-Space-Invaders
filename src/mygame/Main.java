package mygame;

import com.jme3.app.SimpleApplication;

/**
 * Little game about aliens invading the suburbs
 * Your goal is to survive waves of aliens
 * @author Bralts & Hulsman
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(true);
        app.setDisplayStatView(false);
        app.start();
    }

    @Override
    /**
     * First, boot up the menuState...
     */
    public void simpleInitApp() {
        stateManager.attach(new MenuState(this));
    }
    
    @Override
    /**
     * Make sure the audioListener is properly moving with the player's location
     */
    public void simpleUpdate(float tpf){
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }
}
