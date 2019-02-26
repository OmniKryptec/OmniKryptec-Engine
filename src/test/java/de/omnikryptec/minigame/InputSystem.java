package de.omnikryptec.minigame;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.sampling.PoissonSampling;
import org.lwjgl.glfw.GLFW;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.updater.Time;

public class InputSystem extends ComponentSystem {
    
    public InputSystem() {
        super(Family.of(ComponentType.of(MovementComponent.class), ComponentType.of(PlayerComponent.class),
                ComponentType.of(PositionComponent.class)));
    }
    
    private InputManager mgr = LibAPIManager.instance().getInputManager();
    
    private ComponentMapper<MovementComponent> movMapper = new ComponentMapper<>(MovementComponent.class);
    private ComponentMapper<PlayerComponent> playMapper = new ComponentMapper<>(PlayerComponent.class);
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    
    @Override
    public void update(IECSManager iecsManager, Time time) {
        mgr.preUpdate(time);
        mgr.update(time);
        for (Entity e : entities) {
            MovementComponent mov = movMapper.get(e);
            PlayerComponent play = playMapper.get(e);
            float vy = 0;
            float vx = 0;
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_W)) {
                vy += play.maxYv;
            }
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_S)) {
                vy -= play.maxYv;
            }
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_A)) {
                vx -= play.maxXv;
            }
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_D)) {
                vx += play.maxXv;
            }
            mov.dx = vx;
            mov.dy = vy;
            if (mgr.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
                Vector2dc mp = mgr.getMousePosition();
                Vector2f v = MathUtil.screenToWorldspace2D(
                        MathUtil.relativeMousePosition(mp,
                                RenderAPI.get().getWindow().getDefaultFrameBuffer().viewport(), new Vector2f()),
                        RendererSystem.CAMERA.getProjection().invert(new Matrix4f()), null);
                //Vector2d v = new Vector2d(vec4.x, vec4.y);
                v.normalize(500, v);
                Vector2f p = new Vector2f(mov.dx, mov.dy);
                v.add(p, v);
                PositionComponent pos = posMapper.get(e);
                Minigame.BUS.post(new ShootEvent(pos.x, pos.y, v));
            }
        }
        
        mgr.postUpdate(time);
    }
    
}
