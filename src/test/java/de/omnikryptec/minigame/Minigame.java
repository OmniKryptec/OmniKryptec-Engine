/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.minigame;

import java.util.Random;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.impl.ECSManager;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.gui.GuiComponent;
import de.omnikryptec.gui.GuiConstraints;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.openal.DistanceModel;
import de.omnikryptec.libapi.openal.Sound;
import de.omnikryptec.minigame.ShootEvent.Projectile;
import de.omnikryptec.render3.d2.compat.BorderedBatch2D;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;

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
        protected void renderComponent(final BorderedBatch2D batch, float aspect) {
            batch.color().set(1, this.g, this.b, 0.4f);
            batch.drawRect(new Matrix3x2f().setTranslation(this.x, this.y).scale(this.w, this.y));
            batch.color().setAll(1);
            //System.out.println(aspect);
            batch.drawStringSDFautoc("GURKEE", getFontsS().getFontSDF("candara"), 0.1f, aspect, 0.57f, this.x, this.y,
                    0);
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
            Vector2fc v = getInput().getMousePositionRelative();
            if (v.x() < 0.375f && ev.action != KeysAndButtons.OKE_RELEASE) {
                ev.consume();
            }
        }
        
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loaderSettings, final Settings<LibSetting> libSettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings,
            final KeySettings keys) {
        libSettings.set(LibSetting.DEBUG, true);
        loaderSettings.set(LoaderSetting.INIT_OPENAL, true);
        loaderSettings.set(LoaderSetting.SHOW_WINDOW_AFTER_CREATION, WindowMakeVisible.AFTER_INIT);
        libSettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        libSettings.set(LibSetting.DEBUG_CLASSES, false);
        windowSettings.set(WindowSetting.Name, "Minigame");
        //windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.Width, 800);
        windowSettings.set(WindowSetting.Height, 600);
        
        Profiler.setEnabled(true);
    }
    
    @Override
    protected void onInitialized() {
        getResourceManager().load(false, true, new AdvancedFile("intern:/de/omnikryptec/resources/"));
        getEventBus().register(this);
        this.mgr = new ECSManager(true);//If not in concurrent mode, the collisionsystem and the entitytaskqueue make problems, for whatever reason
        //this.mgr = UpdateableFactory.createDefaultIECSManager();
        final Scene sn = getGame().createAndAddScene();
        //sn.setTimeTransform(t -> new Time(t.opCount, t.ops, t.current, t.delta*3));
        sn.setGameLogic(this.mgr);
        this.mgr.addSystem(new CollisionSystem());
        this.mgr.addSystem(new PlayerSystem());
        this.mgr.addSystem(new RendererSystem(sn.getViewManager()));
        this.mgr.addSystem(new MovementSystem());
        this.mgr.addSystem(new RangedSystem());
        this.mgr.addSystem(new AudioSystem());
        this.mgr.addSystem(new RandomColorSystem());
        this.mgr.addEntity(makePlayer(0, 0));
        this.mgr.addEntity(makeBackground());
        for (int i = -30; i < 30; i++) {
            for (int j = -30; j < 30; j++) {
                if (this.random.nextFloat() < 0.25f) {
                    this.mgr.addEntity(makeThing(i * 20, j * 20));
                }
            }
        }
        getAudio().setDistanceModel(DistanceModel.EXPONENT);
        //getGame().getGuiManager().setGui(new TestComponent(0, 0));
    }
    
    @Override
    protected void onShutdown() {
        System.out.println(Profiler.currentInfo());
    }
    
    private Entity makeBackground() {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(-1000, -1000));
        e.addComponent(new RenderComponent(2000, 2000, new Color(0.1f, 0.15f, 0.15f), -100));
        return e;
    }
    
    private Entity makeSoundEffect(Sound s, float x, float y) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        AudioComponent ac = new AudioComponent();
        ac.audioSource.play(s);
        ac.audioSource.setDeltaPitch(this.random.nextFloat());
        e.addComponent(ac);
        return e;
    }
    
    private Entity makePlayer(final float x, final float y) {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(10, 10, new Color(1, 1, 0), 10));
        e.addComponent(new PlayerComponent(300, 5, 5));
        e.addComponent(new MovementComponent(0, 0));
        e.addComponent(new CollisionComponent(10, 10));
        e.addComponent(new RandomColorComponent());
        
        return e;
    }
    
    private Entity makeThing(final float x, final float y) {
        final Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(15, 15, new Color(0, 1, 1), 8));
        e.addComponent(new CollisionComponent(15, 15));
        e.addComponent(new MovementComponent(0, 0));
        e.addComponent(new RandomColorComponent());
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
        e.addComponent(new RandomColorComponent());
        
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
        Entity ent = makeSoundEffect(getSounds().getCached("explosion.wav"),
                this.mapper.get(ev.bomb).transform.worldspacePos().x(),
                this.mapper.get(ev.bomb).transform.worldspacePos().y());
        if (ent != null) {
            this.mgr.addEntity(ent);
        }
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
        if (this.random.nextFloat() >= 0.987) {
            getEventBus().post(new BombExplodeEvent(hit));
        }
    }
    
}
