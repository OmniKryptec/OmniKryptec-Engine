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

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityListener;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.render.AdaptiveCamera;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.batch.AdvancedBatch2D;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.objects.AdvancedSprite;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.renderer2.AdvancedRenderer2D;
import de.omnikryptec.render.renderer2.Renderer2D.EnvironmentKeys2D;
import de.omnikryptec.render.renderer2.ViewManager;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.updater.Time;

public class RendererSystem extends AbstractComponentSystem implements EntityListener {
    
    public static Camera CAMERA = new AdaptiveCamera(RendererSystem::get);
    
    private static Matrix4f get(int w, int h) {
        Matrix4f m = new Matrix4f();
        int[] vp = MathUtil.calculateViewport(w / (double) h, 800, 600);
        m.setOrtho2D(-vp[2], vp[2], -vp[3], vp[3]);
        return m;
    }
    
    private final ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<RenderComponent> rendMapper = new ComponentMapper<>(RenderComponent.class);
    
    private final ViewManager viewMgr;
    private final AdvancedRenderer2D renderer;
    
    public RendererSystem(ViewManager vm) {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(RenderComponent.class)));
        this.viewMgr = vm;
        this.viewMgr.addRenderer(renderer = new AdvancedRenderer2D());
        this.viewMgr.getMainView().setProjection(CAMERA);
        
    }
    
    private class MyLight extends Light2D {
        private Color color;
        private float x, y;
        
        @Override
        public void draw(final Batch2D batch) {
            batch.color().set(this.color);
            batch.drawRect(new Matrix3x2f().translate(this.x, this.y), 300, 300);
        }
    };
    
    @Override
    public void addedToIECSManager(final IECSManager iecsManager) {
        super.addedToIECSManager(iecsManager);
        iecsManager.addEntityListener(getFamily(), this);
        final MyLight l1 = new MyLight();
        l1.color = new Color(1, 0, 0);
        l1.x = -120;
        l1.y = 50;
        final MyLight l2 = new MyLight();
        l2.color = new Color(0, 1, 0);
        l2.x = 0;
        l2.y = -70;
        final MyLight l3 = new MyLight();
        l3.color = new Color(0, 0, 1);
        l3.x = 120;
        l3.y = 50;
        //        this.renderer.getIRenderedObjectManager().add(Light2D.TYPE, l1);
        //        this.renderer.getIRenderedObjectManager().add(Light2D.TYPE, l2);
        //        this.renderer.getIRenderedObjectManager().add(Light2D.TYPE, l3);
        this.viewMgr.getMainView().getEnvironment().set(EnvironmentKeys2D.AmbientLight, new Color());//new Color(0.3f, 0.3f, 0.3f));
    }
    
    @Override
    public void removedFromIECSManager(final IECSManager iecsManager) {
        super.removedFromIECSManager(iecsManager);
        iecsManager.removeEntityListener(getFamily(), this);
    }
    
    @Override
    public void entityAdded(final Entity entity) {
        final AdvancedSprite sprite = new AdvancedSprite() {
            private long i = 0;
            private Color borderColor = new Color();
            
            public void draw(Batch2D batch) {
                super.draw(batch);
                i++;
                AdvancedBatch2D adv = (AdvancedBatch2D) batch;
                adv.signedDistanceFieldData().set(0.5f, 0.6f);
                if (i % 400 == 0) {
                    this.getColor().randomizeRGB();
                    borderColor.randomizeRGB();
                }
                adv.borderColor().set(borderColor);
                adv.borderSDFData().set(0.6f, 0.7f);
                adv.drawStringSimple("OOOF", Omnikryptec.getFontsS().getFontSDF("Candara"), 80,
                        getTransform().worldspacePos().x(), getTransform().worldspacePos().y(),
                        (float) LibAPIManager.instance().getGLFW().getTime());
            };
        };
        sprite.setTransform(this.posMapper.get(entity).transform);
        sprite.setColor(this.rendMapper.get(entity).color);
        sprite.setWidth(this.rendMapper.get(entity).w);
        sprite.setHeight(this.rendMapper.get(entity).h);
        sprite.setLayer(this.rendMapper.get(entity).layer);
        sprite.setTexture(this.rendMapper.get(entity).texture);
        this.rendMapper.get(entity).backingSprite = sprite;
        this.renderer.add(sprite);
    }
    
    @Override
    public void entityRemoved(final Entity entity) {
        this.renderer.remove((AdvancedSprite) this.rendMapper.get(entity).backingSprite);
    }
    
    @Override
    public void update(final IECSManager manager, final Time time) {
    }
    
}
