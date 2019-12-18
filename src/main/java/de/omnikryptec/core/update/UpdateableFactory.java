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

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.renderer.RendererContext;
import de.omnikryptec.resource.loadervpc.TextureHelper;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;

public class UpdateableFactory {
    
    public static IECSManager createDefaultIECSManager() {
        return IECSManager.createDefault();
    }
    
    public static InputManager createInputManagerSimple() {
        return createInputManager(null);
    }
    
    public static InputManager createInputManager(final KeySettings keySettings) {
        return new InputManager(keySettings);
    }
    
    @Deprecated
    public static RendererContext createRendererContext() {
        return new RendererContext();
    }
    
    @Deprecated
    public static IUpdatable createScreenClearTest() {
        return new IUpdatable() {
            
            private final Color color = new Color();
            
            @Override
            public void update(final Time time) {
                if (time.opCount % 40 == 0) {
                    this.color.randomizeRGB();
                }
                LibAPIManager.instance().getGLFW().getRenderAPI().getCurrentFrameBuffer().clearColor(this.color);
            }
        };
    }
    
    @Deprecated
    public static IUpdatable createRenderTest(final TextureHelper help) {
        
        final FrameBuffer fbo = LibAPIManager.instance().getGLFW().getRenderAPI().createFrameBuffer(2000, 2000, 0, 3);
        
        fbo.bindFrameBuffer();
        fbo.assignTargets(new FBTarget(FBAttachmentFormat.RGBA8, 0), new FBTarget(FBAttachmentFormat.DEPTH24),
                new FBTarget(FBAttachmentFormat.RGBA8, 1));
        final Texture texture = help.get("jd.png");
        final Texture t2 = help.get("gurke");
        
        return new IUpdatable() {
            private final SimpleBatch2D batch = new SimpleBatch2D(250);
            private final Matrix3x2f t = new Matrix3x2f();
            private final Camera cam = new Camera(new Matrix4f().ortho2D(0, 4, 0, 3));
            
            @Override
            public void update(final Time time) {
                //this.cam.getTransform().set(new Matrix4f().translate(Mathf.pingpong(time.currentf, 2), 0, 0));
                //this.batch.setIProjection(cam);
                this.batch.begin();
                final float s = Mathf.pingpong(time.currentf, Mathf.PI);
                this.t.identity();
                this.t.scale(s, s);
                this.t.rotateAbout(s, 0.5f, 0.5f);
                this.batch.color().randomizeRGB();
                this.batch.draw(t2, null, false, false);
                this.batch.draw(texture, this.t, false, false);
                this.batch.color().randomizeRGB();
                this.batch.drawLine(0, 0, 3, 2, 0.1f);
                this.batch.end();
                fbo.resolveToFrameBuffer(LibAPIManager.instance().getGLFW().getRenderAPI().getSurface(), 1);
            }
            
        };
    }
    
}
