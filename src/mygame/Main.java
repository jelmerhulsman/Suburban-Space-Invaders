package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;

/**
 *
 * @author Bralts & Hulsman
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener {
    private BulletAppState bulletAppState;
    private Spatial suburbs;
    private RigidBodyControl suburbsControl;
    private CharacterControl player;
    private Vector3f walkDirection;
    private boolean left, right, up, down;
    private boolean bDebugMode;
    private Vector3f camDir;
    private Vector3f camLeft;
    private Weapon rayGun;
    private float playerHealth,maxPlayerHealth;
    private HUD hud;
    private BoundingBox suburbsBox;
    private PointLight sun;
    
    final boolean bEnableShadows = false;
    final int ShadowSize = 1024;
    

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        
        initPhysics();
        initPhysics(false);
        initScene();
        initCollision();
        initLight();
        if(bEnableShadows)
        initShadow();
        initPlayer();
        initFog();
        initHUD();
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) {
        updatePlayer();
        updateWeapon();
        updateHUD();
        
        //fpsText.setText(FastMath.floor(cam.getLocation().x) + ", " + FastMath.floor(cam.getLocation().y) + ", " + FastMath.floor(cam.getLocation().z));
    }

    private void initPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void initPhysics(boolean debug) {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        if (debug) {
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        }
    }

    private void initScene() {
        //assetManager.registerLocator("town.zip", ZipLocator.class);
        //town = assetManager.loadModel("main.scene");

        suburbs = assetManager.loadModel("Models/Suburbs/Suburbs.j3o");
        suburbs.scale(5f);
        rootNode.attachChild(suburbs);
        
        suburbsBox = (BoundingBox)suburbs.getWorldBound();
    }

    private void initCollision() {
        CollisionShape suburbsShape = CollisionShapeFactory.createMeshShape(suburbs);
        suburbsControl = new RigidBodyControl(suburbsShape, 0f);
        suburbs.addControl(suburbsControl);
        bulletAppState.getPhysicsSpace().add(suburbsControl);
    }

    private void initLight() {
        /*AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);*/
       
        sun = new PointLight();
        sun.setColor(ColorRGBA.White);
        sun.setPosition(new Vector3f(suburbsBox.getXExtent() / 2,300,suburbsBox.getZExtent() / 2));
        sun.setRadius(suburbsBox.getXExtent() * suburbsBox.getZExtent());
        rootNode.addLight(sun);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    public void initShadow()
    {
        suburbs.setShadowMode(ShadowMode.CastAndReceive);
        
        
        PointLightShadowRenderer dlsr = new PointLightShadowRenderer(assetManager, ShadowSize);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.5f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        viewPort.addProcessor(dlsr);
    }
    
    private void initPlayer() {
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
        
        playerHealth = 100f;
        maxPlayerHealth = 100f;
        rayGun = new Weapon(assetManager, bulletAppState, viewPort, timer);
        rootNode.attachChild(rayGun);
    }
    
    public void initFog() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(150f);
        fog.setFogDensity(2f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }

    private void initHUD() {
        hud = new HUD(assetManager, guiNode, settings, guiFont);
        hud.initCrossHair(40);
        hud.initBars();
    }
    
    private void initKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Debug", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(actionListener, "Debug");

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
            walkDirection.addLocal(camDir.x, 0, camDir.z);
        }
        if (down) {
            walkDirection.addLocal(camDir.x * -1, 0, camDir.z * -1);
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    public void updateWeapon() {
        Vector3f gunLoc = cam.getLocation().add(cam.getDirection().mult(3));
        rayGun.setLocalTranslation(gunLoc);
        rayGun.setLocalRotation(cam.getRotation());
        rayGun.restoreEnergy();
        rayGun.isShooting = false;
    }
    
    public void updateHUD()
    {
        float percentageEnergy = ((rayGun.getEnergy() / rayGun.getMaxEnergy()));
        float percentageHealth = ((playerHealth / maxPlayerHealth));
        hud.updateHUD(percentageEnergy, percentageHealth);
        
        if (bDebugMode) {
            setDisplayStatView(true);
            setDisplayFps(true);
        } else {
            setDisplayStatView(false);
            setDisplayFps(false);
        }
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
            if (binding.equals("Debug")) {
                if(keyPressed)
                if (bDebugMode) {
                    bDebugMode = false;
                } else {
                    bDebugMode = true;
                }
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            if (binding.equals("Shoot")) {
                Vector3f bulletLoc = cam.getLocation().add(cam.getDirection().mult(3));
                rayGun.shoot(bulletLoc, cam.getRotation(), cam.getDirection());
                rayGun.isShooting = true;
            }
        }
    };

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() != null) {
            if ( event.getNodeA().getName().equals("Bullet") ){
                /*Vector3f xyz = event.getNodeA().getWorldTranslation();
                Box boxMesh = new Box(1f,1f,1f); 
                Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
                Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
                boxMat.setBoolean("UseMaterialColors", true); 
                boxMat.setColor("Ambient", ColorRGBA.Pink); 
                boxMat.setColor("Diffuse", ColorRGBA.Pink); 
                boxGeo.setMaterial(boxMat); 
                boxGeo.setLocalTranslation(xyz);
                rootNode.attachChild(boxGeo);*/
            
                rayGun.detachChild(event.getNodeA());
            }
        }
    }
    
    public float getPlayerHealth() {
        return playerHealth;
    }
}
