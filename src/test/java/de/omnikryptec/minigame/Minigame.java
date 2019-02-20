package de.omnikryptec.minigame;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class Minigame extends EngineLoader {
    public static void main(final String[] args) {
        new Minigame().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings) {
        libsettings.set(LibSetting.DEBUG, true);
        libsettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Minigame");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, true);
        
        //windowSettings.set(WindowSetting.Fullscreen, true);
        //windowSettings.set(WindowSetting.Width, 10000);
        //windowSettings.set(WindowSetting.Height, 2000);
    }
    
    @Override
    protected void onInitialized() {
        OpenGLUtil.setMultisample(true);
        final SceneBuilder builder = getGameController().getGlobalScene().createBuilder();
        //getResManager().addCallback(LoadingProgressCallback.DEBUG_CALLBACK);
        //getResManager().stage(new AdvancedFile("src/test/resources"));
        //getResManager().processStaged(false);
        IECSManager mgr = builder.addDefaultECSManager();
        mgr.addSystem(new RendererSystem());
        mgr.addSystem(new InputSystem());
        mgr.addEntity(makePlayer());
    }
    
    private Entity makePlayer() {
        Entity e = new Entity();
        e.addComponent(new PositionComponent());
        e.addComponent(new RenderComponent(10, 10));
        e.addComponent(new PlayerComp(50,50));
        return e;
    }
    
    
}
