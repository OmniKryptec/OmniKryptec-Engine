package de.omnikryptec.minigame;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityListener;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render.renderer.Renderer2D.EnvironmentKeys2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

public class RendererSystem extends AbstractComponentSystem implements EntityListener {
    
    public static Camera CAMERA = new Camera(new Matrix4f().ortho2D(-600, 600, -600, 600));
    
    private final ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<RenderComponent> rendMapper = new ComponentMapper<>(RenderComponent.class);
    
    private final LocalRendererContext renderer;
    
    public RendererSystem(final LocalRendererContext renderer) {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(RenderComponent.class)));
        this.renderer = renderer;
        this.renderer.addRenderer(new Renderer2D());
        this.renderer.setMainProjection(CAMERA);
        
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
        this.renderer.getEnvironmentSettings().set(EnvironmentKeys2D.AmbientLight, new Color());//new Color(0.3f, 0.3f, 0.3f));
    }
    
    @Override
    public void removedFromIECSManager(final IECSManager iecsManager) {
        super.removedFromIECSManager(iecsManager);
        iecsManager.removeEntityListener(getFamily(), this);
    }
    
    @Override
    public void entityAdded(final Entity entity) {
        final SimpleSprite sprite = new SimpleSprite() {
            public void draw(Batch2D batch) {
                super.draw(batch);
                batch.drawLine(getTransform().worldspacePos().x(), getTransform().worldspacePos().y(),
                        getTransform().worldspacePos().x() + 100, getTransform().worldspacePos().y(), 2);
                batch.drawStringSimple("OOOF", Minigame.font, 40, getTransform().worldspacePos().x(),
                        getTransform().worldspacePos().y());
            };
        };
        sprite.setTransform(this.posMapper.get(entity).transform);
        sprite.setColor(this.rendMapper.get(entity).color);
        sprite.setWidth(this.rendMapper.get(entity).w);
        sprite.setHeight(this.rendMapper.get(entity).h);
        sprite.setLayer(this.rendMapper.get(entity).layer);
        sprite.setTexture(this.rendMapper.get(entity).texture);
        this.rendMapper.get(entity).backingSprite = sprite;
        this.renderer.getIRenderedObjectManager().add(Sprite.TYPE, sprite);
    }
    
    @Override
    public void entityRemoved(final Entity entity) {
        final RenderedObject o = this.rendMapper.get(entity).backingSprite;
        this.renderer.getIRenderedObjectManager().remove(Sprite.TYPE, o);
    }
    
    @Override
    public void update(final IECSManager manager, final Time time) {
    }
    
}
