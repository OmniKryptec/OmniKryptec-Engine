package de.omnikryptec.minigame;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.util.updater.Time;

public class RendererSystem extends ComponentSystem {
    
    public RendererSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(RenderComponent.class)));
        LibAPIManager.LIBAPI_EVENTBUS.register(this);
        Window w = RenderAPI.get().getWindow();
        batch.setViewProjection(new Matrix4f().ortho2D(-w.getBufferWidth() / 2, w.getBufferWidth() / 2,
                -w.getBufferHeight() / 2, w.getBufferHeight() / 2));
    }
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<RenderComponent> rendMapper = new ComponentMapper<>(RenderComponent.class);
    
    private ShadedBatch2D batch = new ShadedBatch2D(1000);
    
    @Override
    public void update(IECSManager manager, Time time) {
        RenderAPI.get().clear(SurfaceBuffer.Color);
        batch.begin();
        
        for (Entity entity : entities) {
            PositionComponent pos = posMapper.get(entity);
            RenderComponent rend = rendMapper.get(entity);
            if (rend.color != null) {
                batch.color().setFrom(rend.color);
            } else {
                batch.color().setAll(1);
            }
            batch.drawRect(new Matrix3x2f().translate(pos.x, pos.y), rend.w, rend.h);
        }
        batch.end();
    }
    
    @EventSubscription
    public void win(WindowEvent.WindowResized ev) {
        batch.setViewProjection(new Matrix4f().ortho2D(-ev.width / 2, ev.width / 2, -ev.height / 2, ev.height / 2));
    }
    
}
