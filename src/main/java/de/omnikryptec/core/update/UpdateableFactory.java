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

package de.omnikryptec.core.update;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.*;
import de.omnikryptec.libapi.exposed.render.FBTarget.TextureFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec4;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.VertexAttribute;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public class UpdateableFactory {
    
    public static IECSManager createDefaultIECSManager() {
        return IECSManager.createDefault();
    }
    
    public static InputManager createInputManagerSimple() {
        return createInputManager(null);
    }
    
    public static InputManager createInputManager(KeySettings keySettings) {
        return new InputManager(keySettings);
    }
    
    public static IUpdatable createScreenClearTest() {
        return new IUpdatable() {
            @Override
            public void update(final Time time) {
                if (time.opCount % 40 == 0) {
                    RenderAPI.get().setClearColor(new Color().randomizeRGB());
                }
                RenderAPI.get().clear(SurfaceBufferType.Color);
            }
            
            @Override
            public boolean passive() {
                return false;
            }
        };
    }
    
    public static IUpdatable createRenderTest(TextureData dat) {
        
        final FrameBuffer fbo = RenderAPI.get().createFrameBuffer(2000, 2000, 0, 3);
        
        fbo.bindFrameBuffer();
        fbo.assignTargets(new FBTarget(TextureFormat.RGBA8, 0), new FBTarget(TextureFormat.DEPTH24),
                new FBTarget(TextureFormat.RGBA8, 1));
        final Texture texture = RenderAPI.get().createTexture2D(dat, new TextureConfig());
        
        return new IUpdatable() {
            
            private final ShadedBatch2D batch = new ShadedBatch2D(250);
            private final Matrix3x2f t = new Matrix3x2f();
            private final Camera cam = new Camera(new Matrix4f().ortho2D(0, 4, 0, 3));
            
            @Override
            public void update(final Time time) {
                this.cam.getTransform().set(new Matrix4f().translate(Mathf.pingpong(time.currentf, 2), 0, 0));
                this.batch.setIProjection(cam);
                this.batch.begin();
                final float s = Mathf.pingpong(time.currentf, Mathf.PI);
                this.t.identity();
                this.t.scale(s, s);
                this.t.rotateAbout(s, 0.5f, 0.5f);
                
                this.batch.color().randomizeRGB();
                this.batch.draw(texture, this.t, 1, 1, false, false);
                this.batch.color().randomizeRGB();
                this.batch.drawLine(0, 0, 3, 2, 0.1f);
                this.batch.end();
                fbo.resolveToFrameBuffer(RenderAPI.get().getWindow().getDefaultFrameBuffer(), 1);
            }
            
        };
    }
    
}
