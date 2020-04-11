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

package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;
import org.joml.Matrix4f;

public class View {

    private IProjection projection;
    private FrameBuffer targetFbo;
    private Settings<EnvironmentKey> environmentSettings;
    private Postprocessor postprocessor;

    public View() {
        this.environmentSettings = new Settings<>();
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

    public void setEnvironment(Settings<EnvironmentKey> environmentSettings) {
        this.environmentSettings = environmentSettings;
    }

    public void setPostprocessor(Postprocessor postprocessor) {
        this.postprocessor = postprocessor;
    }

    public Postprocessor getPostprocessor() {
        return this.postprocessor;
    }

    public IProjection getProjection() {
        return this.projection;
    }

    public Settings<EnvironmentKey> getEnvironment() {
        return this.environmentSettings;
    }

    public FrameBuffer getTargetFbo() {
        return this.targetFbo;
    }
}
