package de.omnikryptec.minigame;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;

public class Minigame extends EngineLoader {
    
    public static final EventBus BUS = new EventBus(false);
    
    public static void main(final String[] args) {
        new Minigame().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loaderSettings, final Settings<LibSetting> libSettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings,
            KeySettings keySettings) {
        libSettings.set(LibSetting.DEBUG, true);
        libSettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Minigame");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, true);
        windowSettings.set(WindowSetting.Width, 400);
        windowSettings.set(WindowSetting.Height, 400);
        apiSettings.set(OpenGLRenderAPI.MINOR_VERSION, 3);
        apiSettings.set(OpenGLRenderAPI.MAJOR_VERSION, 3);
    }
    
    @Override
    protected void onInitialized() {
        OpenGLUtil.setMultisample(true);
        final SceneBuilder builder = getGameController().getGlobalScene().createBuilder();
        IECSManager mgr = builder.addDefaultECSManager();
        mgr.addSystem(new RendererSystem());
        mgr.addSystem(new CollisionSystem());
        mgr.addSystem(new InputSystem());
        mgr.addSystem(new MovementSystem());
        mgr.addEntity(makePlayer());
        mgr.addEntity(makeThing(100, 100));
        mgr.addEntity(makeThing(10, -230));
        
    }
    
    private Entity makePlayer() {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(0, 0));
        e.addComponent(new RenderComponent(10, 10));
        e.addComponent(new PlayerComponent(50, 50));
        e.addComponent(new MovementComponent(0, 0));
        e.addComponent(new HitBoxComponent(10, 10));
        return e;
    }
    
    private Entity makeThing(float x, float y) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new RenderComponent(15, 15, new Color(0, 1, 1)));
        e.addComponent(new HitBoxComponent(15, 15));
        return e;
    }
    
}
