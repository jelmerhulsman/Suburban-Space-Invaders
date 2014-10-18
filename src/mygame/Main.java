package mygame;

import com.jme3.app.SimpleApplication;

/**
 *
 * @author Bralts & Hulsman
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        stateManager.attach(new MenuState(this));
    }
    
    
    @Override
    public void simpleUpdate(float tpf){
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }
}
