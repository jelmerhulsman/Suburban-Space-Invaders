package mygame;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
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
import com.jme3.post.filters.ColorOverlayFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import java.util.ArrayList;

/**
 *
 * @author Bralts & Hulsman
 */
public class GameRunningState extends AbstractAppState implements PhysicsCollisionListener {

    private BulletAppState bulletAppState;
    private Spatial suburbs;
    private RigidBodyControl suburbsControl;
    private Player player;
    private AudioNode wave_snd;
    private Vector3f playerWalkDirection;
    private Vector3f enemyWalkDirection;
    private boolean left, right, up, down;
    private boolean debugMode;
    private Vector3f camDir;
    private Vector3f camLeft;
    private Weapon rayGun;
    private Vector3f knockDirection;
    private Vector3f bulletDirection;
    private GameHUD gameHUD;
    private PointLight sun;
    private float tpf = 0;
    private float hitTimer = 0;
    int enemiesPerWave = 1;
    private ArrayList<Enemy> enemies;
    //finals
    final float KNOCKBACK_TIME = 0.2f;
    final boolean ENABLE_SHADOWS = false;
    final int SHADOW_SIZE = 1024;
    final float ENEMY_SPEED = 0.3f;
    final int ENEMY_DAMAGE = 5;
    final int CROSSHAIR_SIZE = 40;
    final int SCORE_PER_KILL = 5;
    final float PLAYER_SPEED = 0.5f;
    final int WEAPON_DAMAGE = 10;
    final boolean ENABLE_FOG = false;
    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private BulletAppState physics;
    private ViewPort viewPort;
    private Camera cam;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private ColorOverlayFilter cof;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app; // can cast Application to something more specific
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.physics = this.stateManager.getState(BulletAppState.class);
        this.viewPort = this.app.getViewPort();
        this.cam = this.app.getCamera();
        this.flyCam = this.stateManager.getState(FlyCamAppState.class).getCamera();
        this.inputManager = this.app.getInputManager();
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        initPhysics();
        initScene();
        initSceneController();

        initLight();
        if (ENABLE_SHADOWS) {
            initShadow();
        }

        initPlayer();

        if (ENABLE_FOG) {
            initFog();
        }
        initFilter();
        initGameHUD();
        initKeys();
        initAudio();

        enemies = new ArrayList<Enemy>();
        enemyWalkDirection = new Vector3f();
        spawnEnemyWave();
        flyCam.setEnabled(true);
    }

    private void initPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void initScene() {
        suburbs = assetManager.loadModel("Models/Suburbs/Suburbs.j3o");
        suburbs.scale(5f);
        rootNode.attachChild(suburbs);
    }

    private void initSceneController() {
        CollisionShape suburbsShape = CollisionShapeFactory.createMeshShape(suburbs);
        suburbsControl = new RigidBodyControl(suburbsShape, 0f);
        suburbs.addControl(suburbsControl);
        bulletAppState.getPhysicsSpace().add(suburbsControl);
    }

    private void initLight() {
        BoundingBox suburbsBox = (BoundingBox) suburbs.getWorldBound();

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

        PointLightShadowRenderer dlsr = new PointLightShadowRenderer(assetManager, SHADOW_SIZE);
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

        player = new Player(assetManager, bulletAppState, new Vector3f(0, 20f, 0));
        rootNode.attachChild(player);

        rayGun = new Weapon(assetManager);
        rootNode.attachChild(rayGun);
    }

    public void initFog() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

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

        CartoonEdgeFilter cartoony = new CartoonEdgeFilter();
        cartoony.setEdgeWidth(1f);
        cartoony.setEdgeIntensity(0.5f);

        cof = new ColorOverlayFilter();
        cof.setColor(ColorRGBA.Red);

        fpp.addFilter(cof);
        fpp.addFilter(bloom);
        fpp.addFilter(fxaa);
        fpp.addFilter(cartoony);

        viewPort.addProcessor(fpp);
    }

    private void initGameHUD() {
        gameHUD = new GameHUD(assetManager, app.getGuiNode(), CROSSHAIR_SIZE);
    }

    private void initKeys() {
        inputManager.setCursorVisible(false);
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

    public void initAudio() {
        wave_snd = new AudioNode(assetManager, "Sounds/new_wave.wav", false);
        wave_snd.setPositional(false);
        wave_snd.setLooping(false);
        wave_snd.setVolume(0.75f);
        rootNode.attachChild(wave_snd);
    }

    public void gotHitEffect() {
        hitTimer = 0;
        cof.setEnabled(true);
    }

    @Override
    public void update(float tpf) {
        this.tpf = tpf;

        updatePlayer();
        updateEnemy();
        updateWeapon(tpf);
        updateGameHUD();

        if (enemies.isEmpty()) {
            player.restoreHealth();

            enemiesPerWave = (int) ((enemiesPerWave + 1f) * 1.5f);
            spawnEnemyWave();

            wave_snd.play();
            player.waveCounter++;
        }

        hitTimer += tpf;
        if (hitTimer > 0.1f) {
            cof.setEnabled(false);
        }
    }

    public void updatePlayer() {
        if (player.knockBackTimer > KNOCKBACK_TIME) {
            camDir.set(cam.getDirection()).multLocal(PLAYER_SPEED);
            camLeft.set(cam.getLeft()).multLocal(PLAYER_SPEED);
            playerWalkDirection.set(0, 0, 0);

            if (!(left && right)) {
                if (left) {
                    playerWalkDirection.addLocal(camLeft.x, 0, camLeft.z);
                } else if (right) {
                    playerWalkDirection.addLocal(camLeft.x * -1, 0, camLeft.z * -1);
                }
            }
            if (!(up && down)) {
                if (up) {
                    playerWalkDirection.addLocal(camDir.x, 0, camDir.z);
                } else if (down) {
                    playerWalkDirection.addLocal(camDir.x * -1, 0, camDir.z * -1);
                }
            }

            if ((up && left) || (up && right) || (down && left) || (down && right)) {
                playerWalkDirection = playerWalkDirection.multLocal(FastMath.sqrt(PLAYER_SPEED));
            }

            player.movePawn(playerWalkDirection);
        } else {
            player.knockBackTimer += tpf;
            player.knockBack(knockDirection);
        }

        Vector3f playerLoc = player.getWorldTranslation();
        if (cam.getLocation().distance(playerLoc) > 0.1f) {
            player.isMoving = true;
        } else {
            player.isMoving = false;
        }

        cam.setLocation(playerLoc);
    }

    public void updateEnemy() {
        Vector3f playerLoc = player.getWorldTranslation();

        for (Enemy e : enemies) {
            Vector3f enemyLoc = e.getWorldTranslation();
            enemyWalkDirection.set(0, 0, 0);
            //app.fpsText.setText(Math.floor(enemyLoc.x) +", "+ Math.floor(enemyLoc.y) +", "+ Math.floor(enemyLoc.z));

            float moveX = FastMath.floor(playerLoc.x) - FastMath.floor(enemyLoc.x);
            float moveZ = FastMath.floor(playerLoc.z) - FastMath.floor(enemyLoc.z);
            float moveTotal = FastMath.abs(moveX) + FastMath.abs(moveZ);

            moveX = (moveX / moveTotal) * ENEMY_SPEED;
            moveZ = (moveZ / moveTotal) * ENEMY_SPEED;
            enemyWalkDirection.set(moveX, 0, moveZ);

            if (player.knockBackTimer > KNOCKBACK_TIME) {
                if (enemyLoc.distance(playerLoc) <= 5) {
                    knockDirection = new Vector3f(enemyWalkDirection);
                    gotHitEffect();
                    if (player.gotKilled(ENEMY_DAMAGE)) {
                        //this.stop();
                    }
                }
            } else {
                enemyWalkDirection.set(0, 0, 0);
            }

            if (e.knockBackTimer > KNOCKBACK_TIME) {
                if (enemyLoc.distance(playerLoc) > 5) {
                    e.jump();
                    e.movePawn(enemyWalkDirection);
                }
            } else {
                e.knockBackTimer += tpf;
                e.knockBack(bulletDirection);
            }

            e.lookAt(new Vector3f(playerLoc.x, 0, playerLoc.z), new Vector3f(0, 1, 0));
        }
    }

    public void updateWeapon(float tpf) {
        Vector3f gunLoc = cam.getLocation().add(cam.getDirection().mult(3));
        rayGun.setLocalTranslation(gunLoc);
        rayGun.setLocalRotation(cam.getRotation());

        rayGun.increaseTimer(tpf);
        if (player.isShootingTimer > 1) {
            rayGun.restoreEnergy(player.isMoving);
        }
        player.isShootingTimer += tpf;
    }

    public void updateGameHUD() {
        float percentageEnergy = ((rayGun.getEnergy() / 50f));
        float percentageHealth = ((player.getHealth() / 100f));
        gameHUD.updateBars(percentageEnergy, percentageHealth);
        gameHUD.updateScore(player.killCounter * SCORE_PER_KILL, player.waveCounter);
        if (debugMode) {
            app.setDisplayStatView(true);
            app.setDisplayFps(true);
        } else {
            app.setDisplayStatView(false);
            app.setDisplayFps(false);
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
                if (player.jump()) {
                    player.groan();
                }
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
            Vector3f randomLoc;
            Vector3f playerLoc = player.getWorldTranslation();

            do {
                float locX = FastMath.nextRandomInt(-190, 490);
                float locZ = FastMath.nextRandomInt(-305, 95);
                randomLoc = new Vector3f(locX, 150f, locZ);
            } while (randomLoc.distance(playerLoc) < 210f);

            Enemy e = new Enemy(assetManager, bulletAppState, randomLoc);
            enemies.add(e);
            rootNode.attachChild(e);
        }
    }
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            if (binding.equals("Shoot")) {
                player.isShootingTimer = 0;
                if (rayGun.shoot()) {
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
                }
            }
        }
    };

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() instanceof Enemy && event.getNodeB() instanceof Bullet) {
            Enemy e = (Enemy) event.getNodeA();

            if (e.gotKilled(WEAPON_DAMAGE)) {
                e.killEffect(bulletAppState);
                enemies.remove(e);
                player.killCounter++;
            }

            Bullet b = (Bullet) event.getNodeB();
            bulletDirection = b.getDirection();
            b.removeBullet();
        }
    }
}
