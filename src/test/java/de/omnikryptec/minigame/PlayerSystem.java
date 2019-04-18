package de.omnikryptec.minigame;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.minigame.ShootEvent.Projectile;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.updater.Time;

public class PlayerSystem extends ComponentSystem {

    public PlayerSystem() {
        super(Family.of(ComponentType.of(MovementComponent.class), ComponentType.of(PlayerComponent.class),
                ComponentType.of(PositionComponent.class)));
    }

    private InputManager inputManager = Minigame.INPUT;

    private ComponentMapper<MovementComponent> movMapper = new ComponentMapper<>(MovementComponent.class);
    private ComponentMapper<PlayerComponent> playMapper = new ComponentMapper<>(PlayerComponent.class);
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);

    private float again;

    @Override
    public void update(IECSManager iecsManager, Time time) {
        again += time.deltaf;
        for (Entity e : entities) {
            MovementComponent mov = movMapper.get(e);
            PlayerComponent play = playMapper.get(e);
            PositionComponent plus = posMapper.get(e);
            RendererSystem.CAMERA.getTransform().localspaceWrite().setTranslation(-plus.pos.x, -plus.pos.y, 0);
            float vy = 0;
            float vx = 0;
            if (inputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_W)) {
                vy += play.maxYv;
            }
            if (inputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_S)) {
                vy -= play.maxYv;
            }
            if (inputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_A)) {
                vx -= play.maxXv;
            }
            if (inputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_D)) {
                vx += play.maxXv;
            }
            if (inputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                vx /= 2;
                vy /= 2;
            }
            mov.dx = vx;
            mov.dy = vy;
            if (inputManager.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1) && inputManager.isMouseInsideWindow()
                    && again > 0.15f) {
                again = 0;
                Vector2f dir = MathUtil.screenToWorldspace2D(
                        MathUtil.relativeMousePosition(inputManager.getMousePosition(),
                                RenderAPI.get().getSurface().viewport(), new Vector2f()),
                        RendererSystem.CAMERA.getProjection().invert(new Matrix4f()), new Vector2f());
                dir.add(-plus.pos.x, -plus.pos.y);
                dir.normalize(200);
                dir.add(mov.dx, mov.dy);
                Minigame.BUS.post(
                        new ShootEvent(plus.pos.x + play.shOffsetX, plus.pos.y + play.shOffsetY, dir, 1000, Projectile.Bomb));
            }
        }
    }

}
