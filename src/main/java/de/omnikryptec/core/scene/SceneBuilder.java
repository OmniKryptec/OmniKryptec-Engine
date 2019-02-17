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

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.core.UpdateableContainer.ExecuteMode;
import de.omnikryptec.core.UpdateableContainer.ExecuteTime;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.TextureFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.Mesh;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec4;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.batch.RenderBatch2D;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.VertexAttribute;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;
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
     * Adds an {@link Updateable} with the currently set configurations and resets
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
                    RenderAPI.get().setClearColor(new Color().randomizeRGB());
                }
                RenderAPI.get().clear(SurfaceBuffer.Color);
            }
        });
    }

    public void addGraphicsBasicImplTest(final TextureData dat) {
        final MeshData data = new MeshData(VertexAttribute.Index, new int[] { 0, 1, 2, 2, 1, 3 },
                VertexAttribute.Position, 2, new float[] { -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f });

        final Mesh mesh = new Mesh(data);
        final Shader shader = RenderAPI.get().createShader();
        shader.create("test");
        final UniformVec4 color = shader.getUniform("u_col");
        final UniformFloat instanceCount = shader.getUniform("instancesMax");
        final UniformSampler sampler = shader.getUniform("sampler");
        final FrameBuffer fbo = RenderAPI.get().createFrameBuffer(2000, 2000, 0, 2);

        fbo.bindFrameBuffer();
        fbo.assignTargets(new FBTarget(TextureFormat.RGBA8, 0), new FBTarget(TextureFormat.DEPTH24));
        final Texture texture = RenderAPI.get().createTexture2D(dat, new TextureConfig());

        shader.bindShader();
        sampler.setSampler(0);
        color.loadVec4(1, 1, 1, 1);
        //OpenGLUtil.setEnabled(RenderConfig.BLEND, true);
        //OpenGLUtil.setBlendMode(BlendMode.ALPHA);
        //final int instances = 2;

        addUpdateable(new Updateable() {
            private final RenderBatch2D batch = new RenderBatch2D(250);
            private final Matrix3x2f t = new Matrix3x2f();
            private final Camera cam = new Camera(new Matrix4f().ortho2D(0, 4, 0, 3));

            @Override
            public void preUpdate(final Time time) {
            }

            @Override
            public void update(final Time time) {

                this.cam.getTransform().set(new Matrix4f().translate(Mathf.pingpong(time.currentf, 2), 0, 0));
                this.batch.setProjection(this.cam);
                //batch.setGlobalTransform(new Matrix4f().rotate(Mathf.PI/4, new Vector3f(1,0,0)));
                this.batch.begin();
                //batch.drawTest();
                final float s = Mathf.pingpong(time.currentf, Mathf.PI);
                this.t.identity();
                this.t.scale(s, s);
                this.t.rotateAbout(s, 0.5f, 0.5f);
                //t.rotateAbout(s, 0.5f, 0.5f);
                //t.rotate((Mathf.pingpong(time.currentf, Mathf.PI)-Mathf.PI/2), 0, 0, 1);
                this.batch.color().randomizeRGB();
                this.batch.draw(texture, this.t, 1, 1, false, false);
                this.batch.color().randomizeRGB();
                this.batch.drawLine(0, 0, 3, 2, 0.1f);
                this.batch.end();
                //shader.bindShader();
                //color.loadColor(Color.randomRGB());
                //texture.bindTexture(0);
                //instanceCount.loadFloat(instances);
                //RenderAPI.get().renderInstanced(mesh, instances);
            }

            @Override
            public void postUpdate(final Time time) {
                fbo.resolveToScreen();
            }

            @Override
            public ExecuteMode defaultExecuteMode() {
                return ExecuteMode.EmbracingUpdt;
            }
        });

    }
}
