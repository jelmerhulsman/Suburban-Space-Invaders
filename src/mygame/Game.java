package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
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
public class Game extends SimpleApplication implements PhysicsCollisionListener {

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
    int enemiesPerWave = 1;
    private ArrayList<Enemy> enemies;
    //finals
    final float KNOCKBACK_TIME = 0.2f;
    final boolean ENABLE_SHADOWS = false;
    final int SHADOW_SIZE = 1024;
    final float ENEMY_SPEED = 0.3f;
    final int ENEMY_DAMAGE = 10;
    final int CROSSHAIR_SIZE = 40;
    final int SCORE_PER_KILL = 5;
    final float PLAYER_SPEED = 0.5f;
    final int WEAPON_DAMAGE = 10;
    final boolean ENABLE_FOG = false;

    public void simpleInitApp() {
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

        player = new Player(assetManager, bulletAppState, new Vector3f(0, 100f, 0));
        rootNode.attachChild(player);

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

    private void initGameHUD() {
        gameHUD = new GameHUD(assetManager, guiNode, settings, guiFont, CROSSHAIR_SIZE);
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

    public void initAudio() {
        wave_snd = new AudioNode(assetManager, "Sounds/new_wave.wav", false);
        wave_snd.setPositional(false);
        wave_snd.setLooping(false);
        wave_snd.setVolume(0.75f);
        rootNode.attachChild(wave_snd);
    }

    @Override
    public void simpleUpdate(float tpf) {
        this.tpf = tpf;

        updatePlayerWalk();
        updateEnemyWalk();
        updateWeapon(tpf);
        updateHUD();

        if (enemies.isEmpty()) {
            enemiesPerWave = (int) ((enemiesPerWave + 1f) * 1.5f);
            spawnEnemyWave();
            wave_snd.play();
            player.waveCounter++;
        }
    }

    public void updatePlayerWalk() {
        player.knockBackTimer += tpf;

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

    public void updateEnemyWalk() {
        Vector3f playerLoc = player.getWorldTranslation();

        for (Enemy e : enemies) {
            e.knockBackTimer += tpf;
            Vector3f enemyLoc = e.getWorldTranslation();

            enemyWalkDirection.set(0, 0, 0);

            float diffX = FastMath.floor(playerLoc.x) - FastMath.floor(enemyLoc.x);
            float diffZ = FastMath.floor(playerLoc.z) - FastMath.floor(enemyLoc.z);

            float diffTotal = FastMath.abs(diffX) + FastMath.abs(diffZ);
            if (diffTotal < ENEMY_SPEED) {
                diffTotal = ENEMY_SPEED;
            }

            diffX = (diffX / diffTotal) * ENEMY_SPEED;
            diffZ = (diffZ / diffTotal) * ENEMY_SPEED;
            enemyWalkDirection.set(diffX, 0, diffZ);

            if (player.knockBackTimer < KNOCKBACK_TIME) {
                enemyWalkDirection.set(0, 0, 0);
            } else if (enemyLoc.distance(playerLoc) < 8) {
                knockDirection = enemyWalkDirection;
                if (player.gotKilled(ENEMY_DAMAGE))
                {
                    //Death screen
                }
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
        rayGun.restoreEnergy(player.isMoving);
    }

    public void updateHUD() {
        float percentageEnergy = ((rayGun.getEnergy() / 50f));
        float percentageHealth = ((player.getHealth() / 100f));
        gameHUD.updateHUD(percentageEnergy, percentageHealth);
        gameHUD.updateScore(player.killCounter * SCORE_PER_KILL, player.waveCounter);
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
                float locY = 150f;
                float locZ = FastMath.nextRandomInt(-305, 95);
                randomLoc = new Vector3f(locX, locY, locZ);
            } while (randomLoc.distance(playerLoc) < 210f);

            Enemy e = new Enemy(assetManager, bulletAppState, randomLoc);
            enemies.add(e);
            rootNode.attachChild(e);
        }
    }
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
