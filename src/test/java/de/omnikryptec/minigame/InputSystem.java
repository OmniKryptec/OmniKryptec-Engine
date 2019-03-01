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

    private static final float PS = 100;
    private float missing = 0;

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
            if (/* mgr.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1) */mgr.isMouseInsideWindow()) {
                float am = time.deltaf * PS + missing;
                int amount = (int) (am);
                missing = am - amount;
                Vector2dc mouseRaw = mgr.getMousePosition();
                PositionComponent pos = posMapper.get(e);
                Vector2f movVec = new Vector2f(mov.dx, mov.dy);
                Vector2f mouseO = MathUtil.screenToWorldspace2D(
                        MathUtil.relativeMousePosition(mouseRaw,
                                RenderAPI.get().getWindow().getDefaultFrameBuffer().viewport(), new Vector2f()),
                        RendererSystem.CAMERA.getProjection().invert(new Matrix4f()), null);
                for (int i = 0; i < amount; i++) {
                    if (MathUtil.isMouseInViewport(mouseRaw,
                            RenderAPI.get().getWindow().getDefaultFrameBuffer().viewport())) {
                        Vector2f mouse = new Vector2f(mouseO);
                        mouse.sub(pos.x, pos.y);
                        //Vector2d v = new Vector2d(vec4.x, vec4.y);
                        mouse.normalize(400, mouse);
                        mouse.add(movVec, mouse);
                        Minigame.BUS.post(new ShootEvent(pos.x + play.shOffsetX, pos.y + play.shOffsetY, mouse, 200));
                        Minigame.BUS.post(
                                new ShootEvent(pos.x + play.shOffsetX, pos.y + play.shOffsetY, mouse.mul(-1), 200));
                    }
                }
            }
        }

        mgr.postUpdate(time);
    }

}
