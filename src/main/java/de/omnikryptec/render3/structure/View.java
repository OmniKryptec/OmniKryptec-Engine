/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.render3.structure;

import org.joml.Matrix4f;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.Camera;
import de.omnikryptec.render3.IProjection;
import de.omnikryptec.render3.postprocessing.Postprocessor;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

public class View {
    
    private IProjection projection;
    private FrameBuffer targetFbo;
    private Postprocessor postprocessor;
    private Texture resultImage;
    private Color clearColor = new Color(0,0,0,0);//Non-zero can cause just seeing the clear color...
    
    public View() {
        this.projection = new Camera(new Matrix4f().ortho2D(0, 1, 0, 1));
        setTargetToSurface();
    }
    
    public void setProjection(IProjection projection) {
        this.projection = projection;
    }
    
    public void setTargetToSurface() {
        setTargetFbo(LibAPIManager.instance().getGLFW().getRenderAPI().getSurface());
    }
    
    public void setTargetFbo(FrameBuffer targetFbo) {
        this.targetFbo = targetFbo;
    }
    
    public void setPostprocessor(Postprocessor postprocessor) {
        this.postprocessor = postprocessor;
    }
    
    public Postprocessor getPostprocessor() {
        return this.postprocessor;
    }
    
    public boolean hasPostprocessor() {
        return this.postprocessor != null;
    }
    
    public IProjection getProjection() {
        return this.projection;
    }
    
    public FrameBuffer getTargetFbo() {
        return this.targetFbo;
    }
    
    public Texture getResult() {
        return resultImage;
    }
    
    public void renderResult(Time time) {
        if (hasPostprocessor()) {
            this.resultImage = this.postprocessor.postprocess(time, this, targetFbo.getTexture(0));
        } else {
            this.resultImage = getTargetFbo().getTexture(0);
        }
    }
    
    public Color getClearColor() {
        return clearColor;
    }
}
