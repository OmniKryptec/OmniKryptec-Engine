package de.omnikryptec.minigame;

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
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.Light2D;
import de.omnikryptec.render.Renderer2D;
import de.omnikryptec.render.Renderer2D.EnvironmentKeys;
import de.omnikryptec.render.RendererContext;
import de.omnikryptec.render.SimpleSprite;
import de.omnikryptec.render.Sprite;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;
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
    
    @Override
    public void addedToIECSManager(IECSManager iecsManager) {
        super.addedToIECSManager(iecsManager);
        iecsManager.addEntityListener(getFamily(), this);
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
        this.renderer.getEnvironmentSettings().set(EnvironmentKeys.AmbientLight, Color.ofTemperature(Mathf.pingpong(time.currentf*1000, 8000)));

        renderer.update(time);
        //        batch.begin();
        //        for (Entity entity : entities) {
        //            PositionComponent pos = posMapper.get(entity);
        //            RenderComponent rend = rendMapper.get(entity);
        //            if (rend.color != null) {
        //                batch.color().set(rend.color);
        //            } else {
        //                batch.color().setAll(1);
        //            }
        //            batch.drawRect(new Matrix3x2f().translate(pos.x, pos.y), rend.w, rend.h);
        //            //batch.draw(texture, new Matrix3x2f().translate(pos.x, pos.y), rend.w,rend.h,false, false);
        //        }
        //        batch.end();
    }
    
}
