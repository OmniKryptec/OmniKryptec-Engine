package de.omnikryptec.minigame;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.minigame.CollisionSystem.CollisionEvent;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import org.joml.Vector2f;

import java.util.Random;

public class Minigame extends EngineLoader {
    
    public static final EventBus BUS = new EventBus(false);
    
    public static void main(final String[] args) {
        new Minigame().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loaderSettings, final Settings<LibSetting> libSettings, final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings, KeySettings keySettings) {
        libSettings.set(LibSetting.DEBUG, true);
        libSettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Minigame");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, true);
        windowSettings.set(WindowSetting.Width, 600);
        windowSettings.set(WindowSetting.Height, 600);
        apiSettings.set(OpenGLRenderAPI.MINOR_VERSION, 3);
        apiSettings.set(OpenGLRenderAPI.MAJOR_VERSION, 3);
    }
    
    private IECSManager mgr;
    
    @Override
    protected void onInitialized() {
        //getResManager().stage(new AdvancedFile("src/test/resource"));
        getResManager().stage(new AdvancedFile("intern:/de/omnikryptec/resources"));
        getResManager().processStaged(false,true);
        //getResourceProvider().get(clazz, name)
        BUS.register(this);
        final SceneBuilder builder = getGameController().getGlobalScene().createBuilder();
        mgr = builder.addDefaultECSManager();
        mgr.addSystem(new RendererSystem());
        mgr.addSystem(new CollisionSystem());
        mgr.addSystem(new InputSystem());
        mgr.addSystem(new MovementSystem());
        mgr.addSystem(new RangedSystem());
        mgr.addEntity(makePlayer());
        for (int i = -30; i < 30; i++) {
            for (int j = -30; j < 30; j++) {
                if (random.nextFloat() < 0.25f) {
                    mgr.addEntity(makeThing(i * 20, j * 20));
                }
            }
        }
    }
    
    private Entity makePlayer() {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(0, 0));
        e.addComponent(new RenderComponent(10, 10));
        e.addComponent(new PlayerComponent(50, 50, 5, 5));
        e.addComponent(new MovementComponent(0, 0));
        e.addComponent(new HitBoxComponent(10, 10));
        return e;
    }
    
    private Entity makeThing(float x, float y) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(15, 15, new Color(0, 1, 1)));
        e.addComponent(new HitBoxComponent(15, 15));
        e.addComponent(new MovementComponent(0, 0));
        e.userData = -10;
        return e;
    }
    
    private Entity makeFlying(float x, float y, Vector2f dir, float range) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x - 2.5f, y - 2.5f));
        e.addComponent(new RenderComponent(5, 5, new Color(1, 0, 0)));
        e.addComponent(new MovementComponent(dir.x, dir.y));
        e.addComponent(new RangedComponent(range, x, y));
        e.addComponent(new HitBoxComponent(5, 5));
        e.userData = 10;
        return e;
    }
    
    @EventSubscription
    public void shoot(ShootEvent ev) {
        mgr.addEntity(makeFlying(ev.x, ev.y, ev.dir, ev.range));
    }
    
    private ComponentMapper<PositionComponent> mapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<RenderComponent> rend = new ComponentMapper<>(RenderComponent.class);
    private ComponentMapper<MovementComponent> mov = new ComponentMapper<>(MovementComponent.class);
    private Random random = new Random();
    private final float TMP = Mathf.sqrt(2) * 15 / 2f;
    
    @EventSubscription
    public void collide(CollisionEvent ev) {
        int d = -1;
        int b = -1;
        for (int i = 0; i < ev.colliding.length; i++) {
            if (ev.colliding[i].userData != null && (Integer) ev.colliding[i].userData == 10) {
                d = i;
            }
        }
        for (int i = 0; i < ev.colliding.length; i++) {
            if (ev.colliding[i].userData != null && (Integer) ev.colliding[i].userData == -10) {
                b = i;
            }
        }
        if (d != -1 && b != -1) {
            PositionComponent pos = mapper.get(ev.colliding[b]);
            MovementComponent mov1 = mov.get(ev.colliding[b]);
            MovementComponent mov2 = mov.get(ev.colliding[d]);
            mov1.dx += mov2.dx / 15;
            mov1.dy += mov2.dy / 15;
            Integer[] possib = { 0, 1, 2, 3 };
            int[] weights = { 4, 10, 2, 1 };
            Integer amount = MathUtil.getWeightedRandom(random, possib, weights);
            for (int i = 0; i < amount; i++) {
                Vector2f vec2 = MathUtil.randomDirection2D(random, 0, 2 * Mathf.PI, new Vector2f());
                BUS.post(new ShootEvent(pos.x + 15 / 2f + vec2.x * TMP, pos.y + 15 / 2f + vec2.y * TMP, vec2.mul(200),
                        400));

            }
            mgr.removeEntity(ev.colliding[d]);
            RenderComponent r = rend.get(ev.colliding[b]);
            float f = 1 + 1f / 255;
            r.color.setB(r.color.getB() / f);
            r.color.clip();
        }

    }
    
}
