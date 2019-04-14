package de.omnikryptec.minigame;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.core.update.ProvidingLayer;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityListener;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.Light2D;
import de.omnikryptec.render.Renderer2D;
import de.omnikryptec.render.RendererContext;
import de.omnikryptec.render.SimpleSprite;
import de.omnikryptec.render.Sprite;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

public class RendererSystem extends ComponentSystem implements EntityListener {
    
    public static Camera CAMERA = new Camera(new Matrix4f().ortho2D(-400, 400, -400, 400));
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<RenderComponent> rendMapper = new ComponentMapper<>(RenderComponent.class);
    
    private RendererContext renderer;
    
    public RendererSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(RenderComponent.class)));
        this.renderer = new RendererContext();
        this.renderer.init(new ProvidingLayer(LibAPIManager.LIB_API_EVENT_BUS));
        this.renderer.addRenderer(new Renderer2D());
        this.renderer.setMainProjection(CAMERA);
        
    }
    
    private class MyLight extends Light2D {
        private Color color;
        private float x, y;
        
        @Override
        public void draw(Batch2D batch) {
            batch.color().set(color);
            batch.drawRect(new Matrix3x2f().translate(x, y), 300, 300);
            //batch.draw(RenderAPI.get().createTexture2D((TextureData)Minigame.RESPROVIDER.get(TextureData.class, "light.png"), new TextureConfig()), new Matrix3x2f().translate(x, y), 1000, 1000, false, false);
        }
    };
    
    @Override
    public void addedToIECSManager(IECSManager iecsManager) {
        super.addedToIECSManager(iecsManager);
        iecsManager.addEntityListener(getFamily(), this);
        MyLight l1 = new MyLight();
        l1.color = new Color(1, 0, 0);
        l1.x = -120;
        l1.y =  50;
        MyLight l2 = new MyLight();
        l2.color = new Color(0, 1, 0);
        l2.x = 0;
        l2.y = -70;
        MyLight l3 = new MyLight();
        l3.color = new Color(0, 0, 1);
        l3.x = 120;
        l3.y = 50;
        this.renderer.getIRenderedObjectManager().add(Light2D.TYPE, l1);
        this.renderer.getIRenderedObjectManager().add(Light2D.TYPE, l2);
        this.renderer.getIRenderedObjectManager().add(Light2D.TYPE, l3);
        //this.renderer.getEnvironmentSettings().set(EnvironmentKeys2D.AmbientLight, new Color(0.3f, 0.3f, 0.3f));
    }
    
    @Override
    public void removedFromIECSManager(IECSManager iecsManager) {
        super.removedFromIECSManager(iecsManager);
        iecsManager.removeEntityListener(getFamily(), this);
    }
    
    @Override
    public void entityAdded(Entity entity) {
        SimpleSprite sprite = new SimpleSprite();
        sprite.setPosition(posMapper.get(entity).pos);
        sprite.setColor(rendMapper.get(entity).color);
        sprite.setWidth(rendMapper.get(entity).w);
        sprite.setHeight(rendMapper.get(entity).h);
        sprite.setLayer(rendMapper.get(entity).layer);
        rendMapper.get(entity).backingSprite = sprite;
        renderer.getIRenderedObjectManager().add(Sprite.TYPE, sprite);
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        RenderedObject o = rendMapper.get(entity).backingSprite;
        renderer.getIRenderedObjectManager().remove(Sprite.TYPE, o);
    }
    
    @Override
    public void update(IECSManager manager, Time time) {
        //this.renderer.getEnvironmentSettings().set(EnvironmentKeys2D.AmbientLight,
        //      Color.ofTemperature(Mathf.pingpong(time.currentf * 1000, 8000)));
        this.renderer.update(time);
    }
    
}
