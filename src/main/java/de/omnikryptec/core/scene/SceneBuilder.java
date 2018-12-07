package de.omnikryptec.core.scene;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.core.UpdateableContainer.ExecuteMode;
import de.omnikryptec.core.UpdateableContainer.ExecuteTime;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

public class SceneBuilder {

    private class Config {
        private boolean async = false;
        private ExecuteTime time = ExecuteTime.Normal;
        private ExecuteMode mode = null;
    }

    private final Scene scene;
    private Config config;

    public SceneBuilder() {
        this(new Scene());
    }

    SceneBuilder(Scene scene) {
        this.scene = scene;
        this.config = new Config();
    }

    public Scene get() {
        return scene;
    }

    public SceneBuilder async() {
        config.async = true;
        return this;
    }

    public SceneBuilder time(ExecuteTime time) {
        config.time = time;
        return this;
    }

    public SceneBuilder mode(ExecuteMode mode) {
        config.mode = mode;
        return this;
    }

    public SceneBuilder resetConfig() {
        this.config = new Config();
        return this;
    }
    
    public void addUpdateable(Updateable updt) {
        if (config.async) {
            scene.getUpdateableContainerAsync().addUpdateable(config.mode, config.time, updt);
        } else {
            scene.getUpdateableContainerSync().addUpdateable(config.mode, config.time, updt);
        }
        resetConfig();
    }

    public IECSManager addDefaultECSManager() {
        IECSManager iecsm = IECSManager.createDefault();
        addUpdateable(iecsm);
        return iecsm;
    }

    public EventBus addEventBus() {
        EventBus ebus = new EventBus();
        addUpdateable(ebus);
        return ebus;
    }

    public void addGraphicsClearTest() {
        addUpdateable(new Updateable() {
            public void update(Time time) {
                if (time.opsCount % 40 == 0) {
                    OpenGLUtil.setClearColor(Color.randomRGB());
                }
                OpenGLUtil.clear(BufferType.COLOR);
            }
        });
    }
}
