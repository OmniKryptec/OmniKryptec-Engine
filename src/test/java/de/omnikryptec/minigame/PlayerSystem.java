package de.omnikryptec.minigame;

import org.joml.Vector2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.minigame.ShootEvent.Projectile;
import de.omnikryptec.util.Profiler;
import de.omnikryptec.util.settings.keys.KeysAndButtons;
import de.omnikryptec.util.updater.Time;

public class PlayerSystem extends AbstractComponentSystem {
    
    public PlayerSystem() {
        super(Family.of(ComponentType.of(MovementComponent.class), ComponentType.of(PlayerComponent.class),
                ComponentType.of(PositionComponent.class)));
    }
    
    private InputManager inputManager = Omnikryptec.getInput();
    
    private ComponentMapper<MovementComponent> movMapper = new ComponentMapper<>(MovementComponent.class);
    private ComponentMapper<PlayerComponent> playMapper = new ComponentMapper<>(PlayerComponent.class);
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    
    private float again;
    
    @Override
    public void update(IECSManager iecsManager, Time time) {
        Profiler.begin("PlayerSystem");
        again += time.deltaf;
        for (Entity e : entities) {
            MovementComponent mov = movMapper.get(e);
            PlayerComponent play = playMapper.get(e);
            PositionComponent plus = posMapper.get(e);
            RendererSystem.CAMERA.getTransform().localspaceWrite().setTranslation(
                    -plus.transform.worldspacePos().x(), -plus.transform.worldspacePos().y(), 0);
            float vy = 0;
            float vx = 0;
            if (inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
                vy += play.maxYv;
            }
            if (inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
                vy -= play.maxYv;
            }
            if (inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
                vx -= play.maxXv;
            }
            if (inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
                vx += play.maxXv;
            }
            if (inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_CONTROL)) {
                vx /= 2;
                vy /= 2;
            }
            mov.dx = vx;
            mov.dy = vy;
            if (inputManager.isMouseButtonPressed(KeysAndButtons.OKE_MOUSE_BUTTON_1) && inputManager.isMouseInsideViewport()
                    && again > 0.15f) {
                again = 0;
                Vector2f dir = inputManager.getMousePositionInWorld2D(RendererSystem.CAMERA, null);
                dir.add(-plus.transform.worldspacePos().x(), -plus.transform.worldspacePos().y());
                dir.normalize(200);
                dir.add(mov.dx, mov.dy);
                Omnikryptec.getEventBus().post(new ShootEvent(plus.transform.worldspacePos().x() + play.shOffsetX,
                        plus.transform.worldspacePos().y() + play.shOffsetY, dir, 1000, Projectile.Bomb));
            }
        }
        Profiler.end();
    }
    
}
