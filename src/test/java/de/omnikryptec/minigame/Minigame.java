package de.omnikryptec.minigame;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.update.UpdateableFactory;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.minigame.ShootEvent.Projectile;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;
import org.joml.Vector2f;

import java.util.Random;

public class Minigame extends EngineLoader {
    
    public static final EventBus BUS = new EventBus(false);
    
    public static void main(final String[] args) {
        new Minigame().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loaderSettings, final Settings<LibSetting> libSettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings) {
        libSettings.set(LibSetting.DEBUG, true);
        libSettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Minigame");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, true);
        windowSettings.set(WindowSetting.Width, 600);
        windowSettings.set(WindowSetting.Height, 600);
    }
    
    private IECSManager mgr;
    
    @Override
    protected void onInitialized() {
        getResourceManager().stage(new AdvancedFile("intern:/de/omnikryptec/resources"));
        getResourceManager().processStaged(false, true);
        BUS.register(this);
        mgr = UpdateableFactory.createDefaultIECSManager();
        getGameController().getGlobalScene().setUpdateableSync(mgr);
        mgr.addSystem(new CollisionSystem());
        mgr.addSystem(new PlayerSystem());
        mgr.addSystem(new RendererSystem());
        mgr.addSystem(new MovementSystem());
        mgr.addSystem(new RangedSystem());
        mgr.addEntity(makePlayer(0, 0));
        
        for (int i = -30; i < 30; i++) {
            for (int j = -30; j < 30; j++) {
                if (random.nextFloat() < 0.25f) {
                    mgr.addEntity(makeThing(i * 20, j * 20));
                }
            }
        }
        
    }
    
    private Entity makePlayer(float x, float y) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(10, 10));
        e.addComponent(new PlayerComponent(300, 300, 5, 5));
        e.addComponent(new MovementComponent(0, 0));
        e.addComponent(new CollisionComponent(10, 10));
        return e;
    }
    
    private Entity makeThing(float x, float y) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(15, 15, new Color(0, 1, 1)));
        e.addComponent(new CollisionComponent(15, 15));
        e.addComponent(new MovementComponent(0, 0));
        e.flags = -10;
        return e;
    }
    
    private Entity makeFlying(float x, float y, Vector2f dir, float range, int f) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x - 2.5f, y - 2.5f));
        e.addComponent(new RenderComponent(5, 5, new Color(1, 0, f == 20 ? 1 : 0)));
        e.addComponent(new MovementComponent(dir.x, dir.y));
        e.addComponent(new RangedComponent(range, x, y));
        e.addComponent(new CollisionComponent(5, 5));
        
        e.flags = f;
        return e;
    }
    
    @EventSubscription
    public void shoot(ShootEvent ev) {
        if (ev.projectile == Projectile.Normal) {
            mgr.addEntity(makeFlying(ev.x, ev.y, ev.dir, ev.range, 10));
        } else if (ev.projectile == Projectile.Bomb) {
            mgr.addEntity(makeFlying(ev.x, ev.y, ev.dir, ev.range, 20));
        }
    }
    
    @EventSubscription
    public void rangemax(RangeMaxedEvent ev) {
        if (ev.entity.flags == 20) {
            BUS.post(new BombExplodeEvent(ev.entity));
        }
    }
    
    @EventSubscription
    public void bombExplode(BombExplodeEvent ev) {
        for (int i = 0; i < 100; i++) {
            Vector2f r = MathUtil.randomDirection2D(random, 0, 2 * Mathf.PI, new Vector2f()).mul(500);
            BUS.post(new ShootEvent(mapper.get(ev.bomb).pos.x, mapper.get(ev.bomb).pos.y, r, 150, Projectile.Normal));
        }
    }
    
    private ComponentMapper<PositionComponent> mapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<RenderComponent> rend = new ComponentMapper<>(RenderComponent.class);
    private Random random = new Random();
    
    @EventSubscription
    public void collide(CollisionEvent ev) {
        Entity bomb = ev.getEntity(20);
        Entity hit = ev.getEntity(-10);
        Entity d = ev.getEntity(10);
        if (bomb != null && hit != null) {
            mgr.removeEntity(bomb);
            downOrRemove(hit);
            BUS.post(new BombExplodeEvent(bomb));
        }
        if (d != null && hit != null) {
            downOrRemove(hit);
            mgr.removeEntity(d);
        }
    }
    
    private void downOrRemove(Entity hit) {
        RenderComponent c = rend.get(hit);
        
        float f = 0.025f * random.nextFloat();
        c.color.setR(c.color.getR() + f);
        c.color.setG(c.color.getG() - f);
        c.color.setB(c.color.getB() - f);
        if (c.color.getR() >= 1) {
            mgr.removeEntity(hit);
        }
        if (f >= 0.0247f) {
            BUS.post(new BombExplodeEvent(hit));
        }
    }
    
}
