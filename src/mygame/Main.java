package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import java.util.ArrayList;

/**
 *
 * @author Bralts & Hulsman
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener {

    private BulletAppState bulletAppState;
    private Spatial suburbs;
    private RigidBodyControl suburbsControl;
    private Player player;
    private Weapon rayGun;
    private HUD hud;
    private Enemy enemy;
    private BoundingBox suburbsBox;
    private PointLight sun;
    private Vector3f camDir;
    private Vector3f camLeft;
    private Vector3f playerWalkDirection;
    private Vector3f enemyWalkDirection;
    final int ShadowSize = 1024;
    private float playerKnockBackTimer = 1;
    private float enemyKnockBackTimer = 1;
    final float ENEMY_SPEED = 0.2f;
    private boolean left, right, up, down;
    private boolean bDebugMode;
    final boolean bEnableFog = false;
    final boolean bEnableShadows = false;
    ArrayList bullets;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        initPhysics();
        initScene();
        initSceneCollision();

        initLight();
        if (bEnableShadows) {
            initShadow();
        }

        initPlayer();
        if (bEnableFog) {
            initFog();
        }
        initFilter();
        initHUD();
        initKeys();

        bullets = new ArrayList();

        enemyWalkDirection = new Vector3f();

        enemy = new Enemy(assetManager, bulletAppState);
        rootNode.attachChild(enemy);

    }

    private void initPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void initScene() {
        //assetManager.registerLocator("town.zip", ZipLocator.class);
        //town = assetManager.loadModel("main.scene");

        suburbs = assetManager.loadModel("Models/Suburbs/Suburbs.j3o");
        suburbs.scale(5f);
        rootNode.attachChild(suburbs);
        suburbsBox = (BoundingBox) suburbs.getWorldBound();
    }

    private void initSceneCollision() {
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
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
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
        playerWalkDirection = new Vector3f();
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

        //fpp.setNumSamples(4);
        int numSamples = getContext().getSettings().getSamples();
        if (numSamples > 0) {
            fpp.setNumSamples(numSamples);
        }

        FogFilter fog = new FogFilter();
        fog.setFogColor(ColorRGBA.Gray);
        fog.setFogDensity(1f);
        fog.setFogDistance(155f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }

    private void initFilter() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        CartoonEdgeFilter cartoony = new CartoonEdgeFilter();
        cartoony.setEdgeWidth(1f);
        cartoony.setEdgeIntensity(0.5f);
        fpp.addFilter(cartoony);
        viewPort.addProcessor(fpp);
    }

    private void initHUD() {
        hud = new HUD(assetManager, guiNode, settings, guiFont);
        hud.initCrossHair(40);
        hud.initBars();
        hud.initScore();
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

    @Override
    public void simpleUpdate(float tpf) {
        playerKnockBackTimer += tpf;
        enemyKnockBackTimer += tpf;

        updatePlayerWalk();
        updateEnemyWalk();
        updateWeapon(tpf);
        updateHUD();

        //fpsText.setText(/*FastMath.floor(cam.getLocation().x) + ", " + FastMath.floor(cam.getLocation().y) + ", " + FastMath.floor(cam.getLocation().z)*/"Player distance vs monster : " + playerDist);
        fpsText.setText(FastMath.floor(enemy.pawnControl.getPhysicsLocation().x) + ", " + FastMath.floor(enemy.pawnControl.getPhysicsLocation().y) + ", " + FastMath.floor(enemy.pawnControl.getPhysicsLocation().z));
        if (bDebugMode) {
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        } else {
            bulletAppState.getPhysicsSpace().disableDebug();
        }

    }

    public void updatePlayerWalk() {
        if (playerKnockBackTimer > 1f) {
            camDir.set(cam.getDirection()).multLocal(0.5f);
            camLeft.set(cam.getLeft()).multLocal(0.5f);
            playerWalkDirection.set(0, 0, 0);

            if (left) {
                playerWalkDirection.addLocal(camLeft);
            }
            if (right) {
                playerWalkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                playerWalkDirection.addLocal(camDir.x, 0, camDir.z);
            }
            if (down) {
                playerWalkDirection.addLocal(camDir.x * -1, 0, camDir.z * -1);
            }
            player.Move(playerWalkDirection);
        }
        cam.setLocation(player.getCharacterControl().getPhysicsLocation());
    }

    public void updateEnemyWalk() {
        Vector3f enemyLoc = enemy.getWorldTranslation();
        Vector3f playerLoc = player.getWorldTranslation();
        enemyWalkDirection.set(0, 0, 0);

        if (enemyLoc.x < playerLoc.x) {
            float diffX = playerLoc.x - enemyLoc.x;
            if (diffX < ENEMY_SPEED) {
                enemyWalkDirection.addLocal(diffX, 0, 0);
            } else {
                enemyWalkDirection.addLocal(ENEMY_SPEED, 0, 0);
            }
        }
        if (enemyLoc.x > playerLoc.x) {
            float diffX = playerLoc.x - enemyLoc.x;
            if (diffX > -ENEMY_SPEED) {
                enemyWalkDirection.addLocal(diffX, 0, 0);
            } else {
                enemyWalkDirection.addLocal(-ENEMY_SPEED, 0, 0);
            }
        }
        if (enemyLoc.z < playerLoc.z) {
            float diffZ = playerLoc.z - enemyLoc.z;
            if (diffZ < ENEMY_SPEED) {
                enemyWalkDirection.addLocal(0, 0, diffZ);
            } else {
                enemyWalkDirection.addLocal(0, 0, ENEMY_SPEED);
            }
        }
        if (enemyLoc.z > playerLoc.z) {
            float diffZ = playerLoc.z - enemyLoc.z;
            if (diffZ > -ENEMY_SPEED) {
                enemyWalkDirection.addLocal(0, 0, diffZ);
            } else {
                enemyWalkDirection.addLocal(0, 0, -ENEMY_SPEED);
            }
        }


        if (playerKnockBackTimer < 1f) {
            player.Knockback(enemyWalkDirection.mult(1.3f));
            enemyWalkDirection.set(0, 0, 0);
        } else if (enemyLoc.distance(playerLoc) < 8) {
            player.Jump();
            playerKnockBackTimer = 0;
        }

        if (enemyLoc.distance(playerLoc) > 5) {
            enemy.Move(enemyWalkDirection);
        } else {
            enemy.move(0, 0, 0);
        }

        Vector3f newloc = new Vector3f(playerLoc.x, 0, playerLoc.z);
        enemy.lookAt(newloc, new Vector3f(0, 1, 0));
    }

    public void updateWeapon(float tpf) {
        Vector3f gunLoc = cam.getLocation().add(cam.getDirection().mult(3));
        rayGun.setLocalTranslation(gunLoc);
        rayGun.setLocalRotation(cam.getRotation());

        rayGun.increaseTimer(tpf);
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

                if (rayGun.shoot()) {
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

                    if (bullets.size() > 20) {
                        removeBullet((Bullet) bullets.get(20));
                    }
                }
            }
        }
    };

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() instanceof Enemy && event.getNodeB() instanceof Bullet) {
            Enemy e = (Enemy) event.getNodeA();
            Bullet b = (Bullet) event.getNodeB();

            fpsText.setText("Hit enemy with bullet!");


            e.gotHit();
            removeBullet(b);
        }
    }

    public void removeBullet(Bullet removeBullet) {
        removeBullet.control.destroy();
        removeBullet.removeFromParent();
        bullets.remove(removeBullet);
    }
}
