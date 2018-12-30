package de.omnikryptec.core.scene;

import org.lwjgl.opengl.GL11;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.core.UpdateableContainer.ExecuteMode;
import de.omnikryptec.core.UpdateableContainer.ExecuteTime;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Shader;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

/**
 * A class to simplify configuring and adding {@link Updateable}s to a
 * {@link Scene}.
 * 
 * @author pcfreak9000
 * @see Scene#createBuilder()
 */
public class SceneBuilder {
    
    private class Config {
        private boolean async = false;
        private ExecuteTime time = ExecuteTime.Normal;
        private ExecuteMode mode = null;
    }
    
    private final Scene scene;
    private Config config;
    
    /**
     * Creates a new {@link SceneBuilder} with a new, empty {@link Scene}
     */
    public SceneBuilder() {
        this(new Scene());
    }
    
    /**
     * Creates a {@link SceneBuilder} with an existing {@link Scene}
     * 
     * @param scene the scene
     * @see Scene#createBuilder()
     */
    SceneBuilder(final Scene scene) {
        this.scene = scene;
        this.config = new Config();
    }
    
    /**
     * The scene in its current state
     * 
     * @return the scene
     */
    public Scene get() {
        return this.scene;
    }
    
    /**
     * The next {@link Updateable} will be added to the async pipeline.
     * 
     * @return this
     */
    public SceneBuilder async() {
        this.config.async = true;
        return this;
    }
    
    /**
     * Sets the {@link ExecuteTime} of the next {@link Updateable} added.
     * 
     * @param time the execute time
     * @return this
     * @see ExecuteTime
     */
    public SceneBuilder time(final ExecuteTime time) {
        this.config.time = time;
        return this;
    }
    
    /**
     * Sets the {@link ExecuteMode} of the next {@link Updateable} added.
     * 
     * @param mode the execute mode
     * @return this
     * @see ExecuteMode
     */
    public SceneBuilder mode(final ExecuteMode mode) {
        this.config.mode = mode;
        return this;
    }
    
    /**
     * Resets the config to its defaults: synchronized, {@link ExecuteTime#Normal}
     * and {@link ExecuteMode#Default}
     * 
     * @return this
     */
    public SceneBuilder resetConfig() {
        this.config = new Config();
        return this;
    }
    
    /**
     * adds an {@link Updateable} with the currently set configurations and resets
     * the config afterwards.
     * 
     * @param updt the updateable
     * @see #resetConfig()
     */
    public void addUpdateable(final Updateable updt) {
        if (this.config.async) {
            this.scene.getUpdateableContainerAsync().addUpdateable(this.config.mode, this.config.time, updt);
        } else {
            this.scene.getUpdateableContainerSync().addUpdateable(this.config.mode, this.config.time, updt);
        }
        resetConfig();
    }
    
    public IECSManager addDefaultECSManager() {
        final IECSManager iecsm = IECSManager.createDefault();
        addUpdateable(iecsm);
        return iecsm;
    }
    
    public EventBus addEventBus() {
        final EventBus ebus = new EventBus();
        addUpdateable(ebus);
        return ebus;
    }
    
    public void addGraphicsClearTest() {
        addUpdateable(new Updateable() {
            @Override
            public void update(final Time time) {
                if (time.opCount % 40 == 0) {
                    OpenGLUtil.setClearColor(Color.randomRGB());
                }
                OpenGLUtil.clear(BufferType.COLOR);
            }
        });
    }
    
    public void addGraphicsBasicImplTest() {
        VertexBuffer buffer = RenderAPI.get().createVertexBuffer();
        buffer.storeData(new float[] { -0.5f, -0.5f, 0, 0.5f, 0.5f, -0.5f }, false);
        VertexArray array = RenderAPI.get().createVertexArray();
        array.addVertexBuffer(buffer, new VertexBufferLayout.VertexBufferElement(Type.FLOAT, 2, true));
        String vertex = "#version 330 core\nlayout(location = 0) in vec4 pos;\nvoid main() {\ngl_Position = pos;}";
        String fragment = "#version 330 core\nout vec4 col;\nvoid main() {\ncol = vec4(1.0, 0.0, 1.0, 1.0);}";
        Shader shader = RenderAPI.get().createShader();
        shader.create(new Shader.ShaderAttachment(ShaderType.Vertex, vertex),
                new Shader.ShaderAttachment(ShaderType.Fragment, fragment));
        addUpdateable(new Updateable() {
            @Override
            public void update(Time time) {
                shader.bindShader();
                array.bindArray();
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
                array.unbindArray();
            }
        });
    }
}
