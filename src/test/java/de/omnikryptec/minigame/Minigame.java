package de.omnikryptec.minigame;

import java.util.Random;

import org.joml.Matrix3x2f;
import org.joml.Vector2dc;
import org.joml.Vector2f;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.core.update.UpdateableFactory;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.gui.GuiComponent;
import de.omnikryptec.gui.GuiConstraints;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.minigame.ShootEvent.Projectile;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.resource.Font;
import de.omnikryptec.resource.FontFile;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.Profiler;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;

public class Minigame extends Omnikryptec {
    
    private IECSManager mgr;
    private final ComponentMapper<PositionComponent> mapper = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<RenderComponent> rend = new ComponentMapper<>(RenderComponent.class);
    private final Random random = new Random();
    
    public static void main(final String[] args) {
        new Minigame().start();
    }
    
    public static class TestComponent extends GuiComponent {
        
        private final float g, b;
        
        private float x, y, w, h;
        
        public TestComponent(final float g, final float b) {
            this.g = g;
            this.b = b;
        }
        
        @Override
        protected void renderComponent(final Batch2D batch) {
            batch.color().set(1, this.g, this.b, 0.4f);
            batch.drawRect(new Matrix3x2f().setTranslation(this.x, this.y), this.w, this.h);
        }
        
        @Override
        protected void calculateActualPosition(final GuiConstraints constraints) {
            this.x = constraints.getX() + constraints.getMaxWidth() * 0.1f;
            this.y = constraints.getY() + constraints.getMaxHeight() * 0.1f;
            this.w = constraints.getMaxWidth() * 0.8f;
            this.h = constraints.getMaxHeight() * 0.8f;
        }
        
        @EventSubscription
        public void event(InputEvent.MouseButtonEvent ev) {
            Vector2dc v = getInput().getMousePosition();
            if(v.x()<300) {
                ev.consume();
            }
        }
        
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loaderSettings, final Settings<LibSetting> libSettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings,
            final KeySettings keys) {
        //libSettings.set(LibSetting.DEBUG, true);
        libSettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Minigame");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, true);
        windowSettings.set(WindowSetting.Width, 600);
        windowSettings.set(WindowSetting.Height, 600);
        Profiler.setEnabled(true);
    }
    
    public static Font font;
    
    @Override
    protected void onInitialized() {
        getResourceManager().load(false, true, new AdvancedFile("intern:/de/omnikryptec/resources/"));
        FontFile fontfile = getResourceProvider().get(FontFile.class, "text.fnt");
        font = new Font(fontfile, getTextures().get("text1.png"));
        getEventBus().register(this);
        this.mgr = UpdateableFactory.createDefaultIECSManager();
        final Scene sn = getGame().createNewScene(true);
        sn.setGameLogic(this.mgr);
        this.mgr.addSystem(new CollisionSystem());
        this.mgr.addSystem(new PlayerSystem());
        this.mgr.addSystem(new RendererSystem(sn.getRendering()));
        this.mgr.addSystem(new MovementSystem());
        this.mgr.addSystem(new RangedSystem());
        this.mgr.addEntity(makePlayer(0, 0));
        this.mgr.addEntity(makeBackground());
        for (int i = -30; i < 30; i++) {
            for (int j = -30; j < 30; j++) {
                if (this.random.nextFloat() < 0.25f) {
                    this.mgr.addEntity(makeThing(i * 20, j * 20));
                }
            }
        }
        getGame().getGuiManager().setGui(new TestComponent(0, 0));
    }
    
    @Override
    protected void onShutdown() {
        System.out.println(Profiler.currentInfo());
    }
    
    private Entity makeBackground() {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(-1000, -1000));
        e.addComponent(new RenderComponent(2000, 2000, new Color(1, 1, 1), -100));
        return e;
    }
    
    private Entity makePlayer(final float x, final float y) {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(10, 10, new Color(1, 1, 0), 10));
        e.addComponent(new PlayerComponent(300, 300, 5, 5));
        e.addComponent(new MovementComponent(0, 0));
        e.addComponent(new CollisionComponent(10, 10));
        return e;
    }
    
    private Entity makeThing(final float x, final float y) {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(15, 15, new Color(0, 1, 1), 8));
        e.addComponent(new CollisionComponent(15, 15));
        e.addComponent(new MovementComponent(0, 0));
        e.flags = -10;
        return e;
    }
    
    private Entity makeFlying(final float x, final float y, final Vector2f dir, final float range, final int f) {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(x - 2.5f, y - 2.5f));
        e.addComponent(new RenderComponent(5, 5, new Color(1, 0, f == 20 ? 1 : 0), 9));
        e.addComponent(new MovementComponent(dir.x, dir.y));
        e.addComponent(new RangedComponent(range, x, y));
        e.addComponent(new CollisionComponent(5, 5));
        
        e.flags = f;
        return e;
    }
    
    @EventSubscription
    public void shoot(final ShootEvent ev) {
        if (ev.projectile == Projectile.Normal) {
            this.mgr.addEntity(makeFlying(ev.x, ev.y, ev.dir, ev.range, 10));
        } else if (ev.projectile == Projectile.Bomb) {
            this.mgr.addEntity(makeFlying(ev.x, ev.y, ev.dir, ev.range, 20));
        }
    }
    
    @EventSubscription
    public void rangemax(final RangeMaxedEvent ev) {
        if (ev.entity.flags == 20) {
            getEventBus().post(new BombExplodeEvent(ev.entity));
        }
    }
    
    @EventSubscription
    public void bombExplode(final BombExplodeEvent ev) {
        for (int i = 0; i < 100; i++) {
            final Vector2f r = MathUtil.randomDirection2D(this.random, 0, 2 * Mathf.PI, new Vector2f()).mul(500);
            getEventBus().post(new ShootEvent(this.mapper.get(ev.bomb).transform.worldspacePos().x(),
                    this.mapper.get(ev.bomb).transform.worldspacePos().y(), r, 150, Projectile.Normal));
        }
    }
    
    @EventSubscription
    public void collide(final CollisionEvent ev) {
        final Entity bomb = ev.getEntity(20);
        final Entity hit = ev.getEntity(-10);
        final Entity d = ev.getEntity(10);
        if (bomb != null && hit != null) {
            this.mgr.removeEntity(bomb);
            downOrRemove(hit);
            getEventBus().post(new BombExplodeEvent(bomb));
        }
        if (d != null && hit != null) {
            downOrRemove(hit);
            this.mgr.removeEntity(d);
        }
    }
    
    private void downOrRemove(final Entity hit) {
        final RenderComponent c = this.rend.get(hit);
        
        final float f = 0.025f * this.random.nextFloat();
        c.color.setR(c.color.getR() + f);
        c.color.setG(c.color.getG() - f);
        c.color.setB(c.color.getB() - f);
        if (c.color.getR() >= 1) {
            this.mgr.removeEntity(hit);
        }
        if (f >= 0.0247f) {
            getEventBus().post(new BombExplodeEvent(hit));
        }
    }
    
}
