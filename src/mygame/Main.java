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
import com.jme3.post.filters.FXAAFilter;
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
    private Vector3f playerWalkDirection;
    private Vector3f enemyWalkDirection;
    private boolean left, right, up, down;
    private boolean debugMode;
    private Vector3f camDir;
    private Vector3f camLeft;
    private Weapon rayGun;
    private Vector3f knockDirection;
    private Vector3f bulletDirection;
    private HUD hud;
    private Enemy enemy;
    private BoundingBox suburbsBox;
    private PointLight sun;
    private float tpf = 0;
    final private float KNOCKBACK_TIME = 0.2f;
    final boolean enableShadows = false;
    final int ShadowSize = 1024;
    int enemiesPerWave = 1;
    int killCounterPerWave = 0;
    final float ENEMY_SPEED = 0.2f;
    final int ENEMY_DAMAGE = 10;
    final int SCORE_PER_KILL = 5;
    final boolean enableFog = false;
    ArrayList<Enemy> enemyList;
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
        if (enableShadows) {
            initShadow();
        }

        initPlayer();

        if (enableFog) {
            initFog();
        }
        initFilter();
        initHUD();
        initKeys();

        bullets = new ArrayList();
        enemyList = new ArrayList<Enemy>();
        enemyWalkDirection = new Vector3f();

        spawnEnemyWave();
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

        player = new Player(assetManager);

        bulletAppState.getPhysicsSpace().add(player.getCharacterControl());

        rayGun = new Weapon(assetManager);
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

        FXAAFilter fxaa = new FXAAFilter();
        fxaa.setReduceMul(0.0f);
        fxaa.setSubPixelShift(0.0f);

        CartoonEdgeFilter cartoony = new CartoonEdgeFilter();
        cartoony.setEdgeWidth(1f);
        cartoony.setEdgeIntensity(0.5f);

        fpp.addFilter(bloom);
        fpp.addFilter(fxaa);
        fpp.addFilter(cartoony);

        viewPort.addProcessor(fpp);
    }

    private void initHUD() {
        int crossHairSize = 40;
        hud = new HUD(assetManager, guiNode, settings, guiFont, crossHairSize);
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
        this.tpf = tpf;

        updatePlayerWalk();
        updateEnemyWalk();
        updateWeapon(tpf);
        updateHUD();
        
        if (killCounterPerWave == enemiesPerWave){
            enemiesPerWave *= 2;
            spawnEnemyWave();
            
            player.waveCounter++;
            killCounterPerWave = 0;
        }

        fpsText.setText("# Nodes in rootnode: " + rootNode.getChildren().size());
        //fpsText.setText(FastMath.floor(enemy.pawnControl.getPhysicsLocation().x) + ", " + FastMath.floor(enemy.pawnControl.getPhysicsLocation().y) + ", " + FastMath.floor(enemy.pawnControl.getPhysicsLocation().z));
    }

    public void updatePlayerWalk() {
        player.knockBackTimer += tpf;

        if (player.knockBackTimer > KNOCKBACK_TIME) {
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
            player.movePawn(playerWalkDirection);
        } else {
            player.knockBack(knockDirection);
        }

        cam.setLocation(player.getCharacterControl().getPhysicsLocation());
    }

    public void updateEnemyWalk() {
        Vector3f playerLoc = player.getWorldTranslation();

        for (Enemy e : enemyList) {
            e.knockBackTimer += tpf;
            Vector3f enemyLoc = e.getWorldTranslation();

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

            if (player.knockBackTimer < KNOCKBACK_TIME) {
                enemyWalkDirection.set(0, 0, 0);
            } else if (enemyLoc.distance(playerLoc) < 8) {
                knockDirection = enemyWalkDirection;
                player.gotKilled(ENEMY_DAMAGE);
            }

            if (e.knockBackTimer > KNOCKBACK_TIME) {
                if (enemyLoc.distance(playerLoc) > 5) {
                    e.jump();
                    e.movePawn(enemyWalkDirection);
                } else {
                    e.move(0, 0, 0);
                }
            } else {
                e.knockBack(bulletDirection);
            }
            
            e.jump();
            e.lookAt(new Vector3f(playerLoc.x, 0, playerLoc.z), new Vector3f(0, 1, 0));
        }
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
        float percentageEnergy = ((rayGun.getEnergy() / 50f));
        float percentageHealth = ((player.getHealth() / 100f));
        hud.updateHUD(percentageEnergy, percentageHealth);
        hud.updateScore(player.killCounter * SCORE_PER_KILL, player.waveCounter);
        if (debugMode) {
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
                if (player.jump())
                    player.groan();
            }
            if (binding.equals("Debug")) {
                if (keyPressed) {
                    if (debugMode) {
                        debugMode = false;
                        bulletAppState.getPhysicsSpace().disableDebug();
                    } else {
                        debugMode = true;
                        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
                    }
                }
            }
        }
    };

    public void spawnEnemyWave() {
        for (int i = 0; i < enemiesPerWave; i++) {
            float locX = FastMath.nextRandomInt(-188, 448);
            float locY = 100;
            float locZ = FastMath.nextRandomInt(-465, 95);
            Vector3f randomLoc = new Vector3f(locX, locY, locZ);
            enemy = new Enemy(assetManager, bulletAppState, randomLoc);
            enemyList.add(enemy);
            rootNode.attachChild(enemy);
        }
    }
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
                        Bullet b = ((Bullet) bullets.get(20));
                        bullets.remove(b);
                        b.removeBullet();
                    }
                }
            }
        }
    };

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() instanceof Enemy && event.getNodeB() instanceof Bullet) {
            Enemy e = (Enemy) event.getNodeA();
            
            float damage = rayGun.getDamage();
            if (e.gotKilled(damage))
            {
                player.killCounter++;
                killCounterPerWave++;
            }

            Bullet b = (Bullet) event.getNodeB();
            bulletDirection = b.getDirection();
            bullets.remove(b);
            b.removeBullet();
        }
    }
}
