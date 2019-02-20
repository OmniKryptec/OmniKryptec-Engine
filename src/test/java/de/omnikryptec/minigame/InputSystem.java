package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.util.updater.Time;
import org.lwjgl.glfw.GLFW;

public class InputSystem extends ComponentSystem {
    
    public InputSystem() {
        super(Family.of(ComponentType.of(MovementComp.class), ComponentType.of(PlayerComp.class)));
        // mgr.setLongButtonPressEnabled(true);
        mgr.init();
    }
    
    private InputManager mgr = new InputManager(RenderAPI.get().getWindow().getWindowID());
    
    private ComponentMapper<MovementComp> posMapper = new ComponentMapper<>(MovementComp.class);
    private ComponentMapper<PlayerComp> playMapper = new ComponentMapper<>(PlayerComp.class);
    
    @Override
    public void update(IECSManager iecsManager, Time time) {
        mgr.preUpdate(time);
        mgr.update(time);
        for (Entity e : entities) {
            MovementComp pos = posMapper.get(e);
            PlayerComp play = playMapper.get(e);
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
            pos.dx = vx;
            pos.dy = vy;
        }
        
        mgr.postUpdate(time);
    }
    
}
