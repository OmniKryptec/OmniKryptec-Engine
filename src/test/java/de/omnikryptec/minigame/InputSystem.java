package de.omnikryptec.minigame;

import java.util.BitSet;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.util.updater.Time;

public class InputSystem extends ComponentSystem {
    
    public InputSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(PlayerComp.class)));
       // mgr.setLongButtonPressEnabled(true);
        mgr.init();
    }
    
    private InputManager mgr = new InputManager(RenderAPI.get().getWindow().getWindowID());
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<PlayerComp> playMapper = new ComponentMapper<>(PlayerComp.class);

    
    @Override
    public void update(IECSManager iecsManager, Time time) {
        mgr.preUpdate(time);
        mgr.update(time);
        for (Entity e : entities) {
            PositionComponent pos = posMapper.get(e);
            PlayerComp play = playMapper.get(e);
            float vy = 0;
            float vx = 0;
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_W)) {
                vy += play.vy;
            }
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_S)) {
                vy -= play.vy;
            }
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_A)) {
                vx -= play.vx;
            }
            if (mgr.isKeyboardKeyPressed(GLFW.GLFW_KEY_D)) {
                vx += play.vx;
            }
            vx *= time.deltaf;
            vy *= time.deltaf;
            pos.x += vx;
            pos.y += vy;
        }
        
        mgr.postUpdate(time);
    }
    
}
