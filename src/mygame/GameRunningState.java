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
    //app basic variables

    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private BulletAppState bulletAppState;
    private ViewPort viewPort;
    private Camera cam;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    // app final variables
    final private int PLAYER_HEALTH = 100;
    final private float PLAYER_SPEED = 0.5f;
    final private int WEAPON_DAMAGE = 10;
    final private int WEAPON_ENERGY = 50;
    final private int ENEMY_HEALTH = 100;
    final private float ENEMY_SPEED = 0.3f;
    final private int ENEMY_DAMAGE = 5;
    final private int ENEMY_RANGE = 5;
    final private int CROSSHAIR_SIZE = 40;
    final private int SCORE_PER_KILL = 5;
    final private float KNOCKBACK_TIME = 0.2f;
    final private int SHADOW_SIZE = 1024;
    final private boolean ENABLE_SHADOWS = false;
    final private boolean ENABLE_FOG = false;
    //app variables
    private Spatial suburbs;
    private RigidBodyControl suburbsControl;
    private PointLight sun;
    private Vector3f playerWalkDirection, enemyWalkDirection;
    private boolean left, right, up, down;
    private Vector3f camDir, camLeft;
    private Vector3f knockDirection, bulletDirection;
    private Player player;
    private ArrayList<Enemy> enemies;
    private Weapon rayGun;
    private GameHUD gameHUD;
    private AudioNode wave_snd;
    private boolean debugMode;
    private float tpf;
    private int enemiesPerWave;
    private ColorOverlayFilter bloodOverlay;
    private AudioNode attack1_snd, attack2_snd, attack3_snd, attack4_snd, attack5_snd;
    private AudioNode themeSong;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app; // can cast Application to something more specific
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.bulletAppState = this.stateManager.getState(BulletAppState.class);
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

        tpf = 0;
        enemiesPerWave = 1;
        spawnEnemyWave();
        themeSong.play();
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

    private void initShadow() {
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

        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(0);
        flyCam.setZoomSpeed(0);

        player = new Player(assetManager, bulletAppState, PLAYER_HEALTH, new Vector3f(0, 20f, 0));
        rootNode.attachChild(player);

        rayGun = new Weapon(assetManager, WEAPON_ENERGY);
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

        bloodOverlay = new ColorOverlayFilter();
        bloodOverlay.setColor(ColorRGBA.Red);

        fpp.addFilter(bloodOverlay);
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

        inputManager.addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(analogListener, "Exit");
    }

    private void initAudio() {
        
        themeSong = new AudioNode(assetManager, "Sounds/Main_Theme_Loop.wav", false);
        themeSong.setPositional(false);
        themeSong.setLooping(true);
        themeSong.setVolume(0.5f);
        rootNode.attachChild(themeSong);
        
        wave_snd = new AudioNode(assetManager, "Sounds/new_wave.wav", false);
        wave_snd.setPositional(false);
        wave_snd.setLooping(false);
        wave_snd.setVolume(0.5f);
        rootNode.attachChild(wave_snd);
        
        attack1_snd = new AudioNode(assetManager, "Sounds/Alien/Attack1.wav", false);
        attack1_snd.setPositional(true);
        attack1_snd.setLooping(false);
        attack1_snd.setReverbEnabled(false);
        attack1_snd.setRefDistance(30f);
        attack1_snd.setMaxDistance(1000f);
        attack1_snd.setVolume(1.5f);
        rootNode.attachChild(attack1_snd);

        attack2_snd = new AudioNode(assetManager, "Sounds/Alien/Attack2.wav", false);
        attack2_snd.setPositional(true);
        attack2_snd.setLooping(false);
        attack2_snd.setReverbEnabled(false);
        attack2_snd.setRefDistance(30f);
        attack2_snd.setMaxDistance(1000f);
        attack2_snd.setVolume(1.5f);
        rootNode.attachChild(attack2_snd);
        
        attack3_snd = new AudioNode(assetManager, "Sounds/Alien/Attack3.wav", false);
        attack3_snd.setPositional(true);
        attack3_snd.setLooping(false);
        attack3_snd.setReverbEnabled(false);
        attack3_snd.setRefDistance(30f);
        attack3_snd.setMaxDistance(1000f);
        attack3_snd.setVolume(1.5f);
        rootNode.attachChild(attack3_snd);
        
        attack4_snd = new AudioNode(assetManager, "Sounds/Alien/Attack4.wav", false);
        attack4_snd.setPositional(true);
        attack4_snd.setLooping(false);
        attack4_snd.setReverbEnabled(false);
        attack4_snd.setRefDistance(30f);
        attack4_snd.setMaxDistance(1000f);
        attack4_snd.setVolume(1.5f);
        rootNode.attachChild(attack4_snd);
        
        attack5_snd = new AudioNode(assetManager, "Sounds/Alien/Attack5.wav", false);
        attack5_snd.setPositional(true);
        attack5_snd.setLooping(false);
        attack5_snd.setReverbEnabled(false);
        attack5_snd.setRefDistance(30f);
        attack5_snd.setMaxDistance(1000f);
        attack5_snd.setVolume(1.5f);
        rootNode.attachChild(attack5_snd);
        
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
            player.addSurvivedWave();
            gameHUD.updateScore(player.getKills() * SCORE_PER_KILL, player.getSurvivedWaves());

            wave_snd.play();
            enemiesPerWave = (int) ((enemiesPerWave + 1f) * 1.5f);
            spawnEnemyWave();
        }
    }

    private void updatePlayer() {
        if (player.knockBackTimer > KNOCKBACK_TIME) {
            bloodOverlay.setEnabled(false);
            camDir.set(cam.getDirection()).multLocal(PLAYER_SPEED);
            camLeft.set(cam.getLeft()).multLocal(PLAYER_SPEED);
            playerWalkDirection.set(0, 0, 0);

            if (!(left && right)) {
                if (left) {
                    playerWalkDirection.addLocal(camLeft.x, 0, camLeft.z);
                    player.playWalkSound();
                } else if (right) {
                    playerWalkDirection.addLocal(camLeft.x * -1, 0, camLeft.z * -1);
                    player.playWalkSound();
                }
            }
            if (!(up && down)) {
                if (up) {
                    playerWalkDirection.addLocal(camDir.x, 0, camDir.z);
                    player.playWalkSound();
                } else if (down) {
                    playerWalkDirection.addLocal(camDir.x * -1, 0, camDir.z * -1);
                    player.playWalkSound();
                }
            }

            if ((up && left) || (up && right) || (down && left) || (down && right)) {
                playerWalkDirection = playerWalkDirection.multLocal(FastMath.sqrt(PLAYER_SPEED));
                player.playWalkSound();
            }

            player.movePawn(playerWalkDirection);
        } else {
            bloodOverlay.setEnabled(true);
            player.knockBackTimer += tpf;
            player.knockPawnBack(knockDirection);
        }

        Vector3f playerLoc = player.getWorldTranslation();
        if (cam.getLocation().distance(playerLoc) > 0.1f) {
            player.isMoving(true);
        } else {
            player.isMoving(false);
        }
        cam.setLocation(playerLoc);
        app.getListener().setLocation(cam.getLocation());
        app.getListener().setRotation(cam.getRotation());
    }
    
    public void playAttackSound(Vector3f location)
    {
        float randomSound = FastMath.rand.nextFloat();
        
        if(randomSound < 0.2f)
        {
            attack1_snd.setLocalTranslation(location);
            attack1_snd.play();
        }
        else if(randomSound < 0.4f)
        {
            attack2_snd.setLocalTranslation(location);
            attack2_snd.play();
        }
        else if(randomSound < 0.6f)
        {
            attack3_snd.setLocalTranslation(location);
            attack3_snd.play();
        }
        else if(randomSound < 0.8f)
        {
            attack4_snd.setLocalTranslation(location);
            attack4_snd.play();
        }
        else
        {
            attack5_snd.setLocalTranslation(location);
            attack5_snd.play();
        }
    }

    private void updateEnemy() {
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
                if (enemyLoc.distance(playerLoc) <= ENEMY_RANGE) {
                    playAttackSound(enemyLoc);
                    knockDirection = new Vector3f(enemyWalkDirection);
                    if (player.gotKilled(ENEMY_DAMAGE)) {
                        //this.stop();
                    }
                }
            } else {
                enemyWalkDirection.set(0, 0, 0);
            }

            if (e.knockBackTimer > KNOCKBACK_TIME) {
                if (enemyLoc.distance(playerLoc) > ENEMY_RANGE) {
                    e.jump();
                    e.movePawn(enemyWalkDirection);
                }
            } else {
                e.knockBackTimer += tpf;
                e.knockPawnBack(bulletDirection);
            }

            e.lookAt(new Vector3f(playerLoc.x, 0, playerLoc.z), new Vector3f(0, 1, 0));
        }
    }

    private void updateWeapon(float tpf) {
        Vector3f gunLoc = cam.getLocation().add(cam.getDirection().mult(3));
        rayGun.setLocalTranslation(gunLoc);
        rayGun.setLocalRotation(cam.getRotation());

        rayGun.increaseTimers(tpf);
        rayGun.restoreEnergy(player.isMoving());
    }

    private void updateGameHUD() {
        float percentageEnergy = ((rayGun.getEnergy() / 50f));
        float percentageHealth = ((player.getHealth() / 100f));
        gameHUD.updateBars(percentageEnergy, percentageHealth);
        if (debugMode) {
            app.setDisplayStatView(true);
            app.setDisplayFps(true);
        } else {
            app.setDisplayStatView(false);
            app.setDisplayFps(false);
        }
    }

    private void spawnEnemyWave() {
        for (int i = 0; i < enemiesPerWave; i++) {
            Vector3f randomLoc;
            Vector3f playerLoc = player.getWorldTranslation();

            do {
                float locX = FastMath.nextRandomInt(-190, 490);
                float locZ = FastMath.nextRandomInt(-305, 95);
                randomLoc = new Vector3f(locX, 150f, locZ);
            } while (randomLoc.distance(playerLoc) < 210f);

            Enemy e = new Enemy(assetManager, bulletAppState, ENEMY_HEALTH, randomLoc);
            enemies.add(e);
            rootNode.attachChild(e);
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
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            if (binding.equals("Shoot")) {
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

            if (binding.equals("Exit")) {
                exit(false);
            }
        }
    };

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() instanceof Enemy && event.getNodeB() instanceof Bullet) {
            Enemy e = (Enemy) event.getNodeA();

            if (e.gotKilled(WEAPON_DAMAGE)) {
                e.killEffect(bulletAppState);
                enemies.remove(e);
                player.addKill();
                gameHUD.updateScore(player.getKills() * SCORE_PER_KILL, player.getSurvivedWaves());
            }

            Bullet b = (Bullet) event.getNodeB();
            bulletDirection = b.getDirection();
            b.removeBullet();
        }
    }

    private void exit(boolean dead) {
        if (dead) {
            //show score and go back to menu
        } else {
            //go back to menu
        }
    }
}
