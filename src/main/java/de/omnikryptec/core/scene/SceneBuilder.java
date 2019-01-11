/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.core.scene;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Table;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.core.UpdateableContainer.ExecuteMode;
import de.omnikryptec.core.UpdateableContainer.ExecuteTime;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.ShaderSource;
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
        private ExecuteMode mode = ExecuteMode.Default;
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
        final VertexBuffer buffer = RenderAPI.get().createVertexBuffer();
        
        buffer.storeData(new float[] { -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f }, false);
        //buffer.storeData((float[]) data.getAttribute(VertexAttribute.Position), false);
        
        final IndexBuffer indexBuffer = RenderAPI.get().createIndexBuffer();
        indexBuffer.storeData(new int[] { 0, 1, 2, 2, 1, 3 }, false);
        //indexBuffer.storeData((int[]) data.getAttribute(VertexAttribute.Index), false);
        
        final VertexArray array = RenderAPI.get().createVertexArray();
        array.addVertexBuffer(buffer, new VertexBufferLayout.VertexBufferElement(Type.FLOAT, 2, true));
        array.setIndexBuffer(indexBuffer);
        
        final String vertex = "$define shader VERTEX test$ #version 330 core\nlayout(location = 0) in vec4 pos;\nvoid main() {\ngl_Position = pos;}";
        ShaderParser.instance().parse(vertex);
        final String fragment = "$define shader FRAGMENT test$ #version 330 core\nout vec4 col;\nvoid main() {\ncol = vec4(1.0, 0.0, 1.0, 1.0);}";
        final Shader shader = RenderAPI.get().createShader();
        ShaderParser.instance().parse(fragment);
        Table<String, ShaderType, ShaderSource> data = ShaderParser.instance().createCurrentShaderTable();
        shader.create(data.get("test", ShaderType.Vertex), data.get("test", ShaderType.Fragment));
        
        addUpdateable(new Updateable() {
            @Override
            public void update(final Time time) {
                shader.bindShader();
                array.bindArray();
                GL11.glDrawElements(GL11.GL_TRIANGLES, array.vertexCount(), GL11.GL_UNSIGNED_INT, 0);
                array.unbindArray();
            }
        });
        
    }
}
