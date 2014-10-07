package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bralts & Hulsman
 */
public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Spatial suburbs;
    private RigidBodyControl suburbsControl;
    private Player player;
    private Vector3f walkDirection;
    private boolean left, right, up, down;
    private boolean bDebugMode;
    private Vector3f camDir;
    private Vector3f camLeft;
    private Weapon rayGun;
    private HUD hud;
    Enemy enemy;
    private BoundingBox suburbsBox;
    private PointLight sun;
    final boolean bEnableShadows = false;
    final int ShadowSize = 1024;
    
    ArrayList bullets;

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
        
        if (bEnableShadows) {
            initShadow();
        }
        
        initPlayer();
        initFog();
        initFilter();
        initHUD();
        initKeys(); 
        
        bullets = new ArrayList();
        
        enemy = new Enemy(assetManager, bulletAppState);
        rootNode.attachChild(enemy);
        
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    }

    @Override
    public void simpleUpdate(float tpf) {
        updatePlayerWalk();
        updateWeapon();
        updateHUD();

        
        checkGhostCollision();
        //fpsText.setText(/*FastMath.floor(cam.getLocation().x) + ", " + FastMath.floor(cam.getLocation().y) + ", " + FastMath.floor(cam.getLocation().z)*/"Player distance vs monster : " + playerDist);
        fpsText.setText(FastMath.floor(enemy.pawnControl.getPhysicsLocation().x) + ", " + FastMath.floor(enemy.pawnControl.getPhysicsLocation().y) + ", " + FastMath.floor(enemy.pawnControl.getPhysicsLocation().z));
        enemy.rotateAndMove(cam.getLocation());
    }

    private void initPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void initPhysics(boolean debug) {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    private void initScene() {
        //assetManager.registerLocator("town.zip", ZipLocator.class);
        //town = assetManager.loadModel("main.scene");

        suburbs = assetManager.loadModel("Models/Suburbs/Suburbs.j3o");
        suburbs.scale(5f);
        rootNode.attachChild(suburbs);
        suburbsBox = (BoundingBox) suburbs.getWorldBound();
    }

    private void initCollision() {
        CollisionShape suburbsShape = CollisionShapeFactory.createMeshShape(suburbs);
        suburbsControl = new RigidBodyControl(suburbsShape, 0f);
        suburbs.addControl(suburbsControl);
        bulletAppState.getPhysicsSpace().add(suburbsControl);
    }

    private void initLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        sun = new PointLight();
        sun.setColor(ColorRGBA.White);
        sun.setPosition(new Vector3f(suburbsBox.getXExtent() / 2, 300, suburbsBox.getZExtent() / 2));
        sun.setRadius(suburbsBox.getXExtent() * suburbsBox.getZExtent());
        rootNode.addLight(sun);
    }

    public void initShadow() {
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

        player = new Player();

        bulletAppState.getPhysicsSpace().add(player.getCharacterControl());

        rayGun = new Weapon(assetManager, bulletAppState, viewPort, timer);
        rootNode.attachChild(rayGun);
    }

    public void initFog() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(1000f);
        fog.setFogDensity(1f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }
    
    private void initFilter() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
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

    public void updatePlayerWalk() {
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
        player.Move(walkDirection);
        cam.setLocation(player.getCharacterControl().getPhysicsLocation());
    }

    public void updateWeapon() {
        Vector3f gunLoc = cam.getLocation().add(cam.getDirection().mult(3));
        rayGun.setLocalTranslation(gunLoc);
        rayGun.setLocalRotation(cam.getRotation());
        rayGun.restoreEnergy();
        rayGun.isShooting = false;
    }

    public void updateHUD() {
        float percentageEnergy = ((rayGun.getEnergy() / rayGun.getMaxEnergy()));
        float percentageHealth = ((player.getHealth() / player.getMaxHealth()));
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
                player.Jump();
            }
            if (binding.equals("Debug")) {
                if (keyPressed) {
                    if (bDebugMode) {
                        bDebugMode = false;
                    } else {
                        bDebugMode = true;
                    }
                }
            }
        }
    };
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            if (binding.equals("Shoot")) {
                
                if (rayGun.shoot())
                {
                    rayGun.isShooting = true;
                    float spread = rayGun.getSpread();
                    
                    Vector3f bulletLoc = cam.getLocation().add(cam.getDirection().mult(3));
                    float locX = bulletLoc.x + ((FastMath.rand.nextFloat() - FastMath.rand.nextFloat()) * spread);
                    float locY = bulletLoc.y + ((FastMath.rand.nextFloat() - FastMath.rand.nextFloat()) * spread);
                    float locZ = bulletLoc.z + ((FastMath.rand.nextFloat() - FastMath.rand.nextFloat()) * spread);
                    bulletLoc = new Vector3f(locX, locY, locZ);
                    
                    Quaternion bulletRot = cam.getRotation();
                    Vector3f bulletDir = cam.getDirection();
                    
                    Bullet addBullet = new Bullet(assetManager, bulletAppState, bulletLoc, bulletRot, bulletDir);
                    rootNode.attachChild(addBullet);
                    bullets.add(0, addBullet);
                    
                    if (bullets.size() > 10)
                        removeBullet(bullets.size() - 1);
                }
            }
        }
    };
    
    public void removeBullet(int index)
    {
        Bullet removeBullet = (Bullet) bullets.get(index);
        removeBullet.removeFromParent();
        bullets.remove(index);
    }

    public void checkGhostCollision() {
        if (enemy.ghostControl.getOverlappingCount() > 1) {
            List<PhysicsCollisionObject> objList = enemy.ghostControl.getOverlappingObjects();
            for (PhysicsCollisionObject o : objList) {
                if (o.getUserObject() == null) {
                    break;
                }
                
                if (o.getUserObject() instanceof  Bullet) {
                    Bullet b = (Bullet) o.getUserObject();
                    b.removeControl(b.getControl(0));
                    b.removeFromParent();
                    bullets.remove(b);
                    
                    enemy.health--;
                    
                    enemy.checkHP();
                }
            }
        }
    }
}
