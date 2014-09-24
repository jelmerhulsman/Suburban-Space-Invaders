package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Bralts & Hulsman
 */
public class Main extends SimpleApplication {
    private BulletAppState bulletAppState;
    
    private Spatial town;
    private RigidBodyControl landscape;
    
    private CharacterControl player;
    private Vector3f walkDirection;
    private boolean left, right, up, down;
    private Vector3f camDir;
    private Vector3f camLeft;
    private Weapon rayGun;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        cam.setFrustumFar(100f);
        
        initPhysics(false);
        initScene();
        initCollision();
        initLight();
        initShadow();
        initPlayer();
        initHUD();
        initKeys();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        updatePlayer();
        updateWeapon();
    }
    
    private void initPhysics(boolean debug) {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        if (debug)
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    } 
    
    private void initScene() {
        //assetManager.registerLocator("town.zip", ZipLocator.class);
        //town = assetManager.loadModel("main.scene");
        
        town = assetManager.loadModel("Models/Suburbs/Suburbs.j3o");
        town.scale(5f);
        rootNode.attachChild(town);
    }
    
    private void initCollision() {
        CollisionShape townShape = CollisionShapeFactory.createMeshShape((Node) town);
        landscape = new RigidBodyControl(townShape, 0);
        town.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
    }
    
    private void initLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    public void initShadow()
    {
        
    }
    
    private void initPlayer () {
        walkDirection = new Vector3f();
        left = false;
        right = false;
        up = false;
        down = false;
        camDir = new Vector3f();
        camLeft = new Vector3f();
        
        flyCam.setMoveSpeed(0);
        flyCam.setZoomSpeed(0);
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 3.75f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(15f);
        player.setFallSpeed(30f);
        player.setGravity(30f);
        player.setPhysicsLocation(new Vector3f(0, 15f, 0));
        bulletAppState.getPhysicsSpace().add(player);
        
        rayGun = new Weapon(assetManager, bulletAppState, viewPort, timer);
        rootNode.attachChild(rayGun);
    }
    
    private void initHUD() {
        HUD hud = new HUD(assetManager, guiNode, settings);
        hud.initCrossHair(40);
    }
    
    private void initKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "Jump");

        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(analogListener, "Shoot");
    }
    
    public void updatePlayer() {
        camDir.set(cam.getDirection()).multLocal(0.5f);
        camLeft.set(cam.getLeft()).multLocal(0.5f);
        walkDirection.set(0, 0, 0);

        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir.x, 0 ,camDir.z);
        }
        if (down) {
            walkDirection.addLocal(camDir.x * -1, 0, camDir.z * -1);
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
    
    public void updateWeapon() {
        rayGun.setLocalTranslation(cam.getLocation().add(cam.getDirection().mult(3)));
        rayGun.setLocalRotation(cam.getRotation());
        rayGun.restoreEnergy();
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean keyPressed, float tpf) {
            if (binding.equals("Left")) {
                if (keyPressed) {
                    left = true;
                } else {
                    left = false;
                }
            } else if (binding.equals("Right")) {
                if (keyPressed) {
                    right = true;
                } else {
                    right = false;
                }
            } else if (binding.equals("Up")) {
                if (keyPressed) {
                    up = true;
                } else {
                    up = false;
                }
            } else if (binding.equals("Down")) {
                if (keyPressed) {
                    down = true;
                } else {
                    down = false;
                }
            } else if (binding.equals("Jump")) {
                player.jump();
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            if (binding.equals("Shoot")) {
                rayGun.shoot(cam.getLocation().add(cam.getDirection().mult(4)), cam.getRotation(), cam.getDirection());
            }
        }
    };
}
