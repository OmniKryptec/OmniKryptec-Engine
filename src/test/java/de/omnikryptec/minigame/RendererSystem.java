package de.omnikryptec.minigame;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.updater.Time;

public class RendererSystem extends ComponentSystem {
    
    private final Texture texture = RenderAPI.get().createTexture2D(EngineLoader.instance().getResProvider().get(TextureData.class, "final_tree_3.png"),new TextureConfig());
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<RenderComponent> rendMapper = new ComponentMapper<>(RenderComponent.class);
    private ShadedBatch2D batch = new ShadedBatch2D(1000);
    
    public RendererSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(RenderComponent.class)));
        LibAPIManager.LIB_API_EVENT_BUS.register(this);
        FrameBuffer defaultBuffer = RenderAPI.get().getWindow().getDefaultFrameBuffer();
        batch.setViewProjection(new Matrix4f().ortho2D(-defaultBuffer.getWidth() / 2, defaultBuffer.getWidth() / 2,
                -defaultBuffer.getHeight() / 2, defaultBuffer.getHeight() / 2));
    }
    
    @Override
    public void update(IECSManager manager, Time time) {
        RenderAPI.get().clear(SurfaceBuffer.Color);
        batch.begin();
        
        for (Entity entity : entities) {
            PositionComponent pos = posMapper.get(entity);
            RenderComponent rend = rendMapper.get(entity);
            if (rend.color != null) {
                batch.color().set(rend.color);
            } else {
                batch.color().setAll(1);
            }
            batch.drawRect(new Matrix3x2f().translate(pos.x, pos.y), rend.w, rend.h);
            batch.draw(texture, new Matrix3x2f().translate(pos.x, pos.y), rend.w,rend.h,false, false);
        }
        batch.end();
    }
    
    @EventSubscription
    public void win(WindowEvent.WindowResized ev) {
        //batch.setViewProjection(new Matrix4f().ortho2D(-ev.width / 2, ev.width / 2, -ev.height / 2, ev.height / 2));
    }
    
}
