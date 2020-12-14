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

package de.omnikryptec.render3.postprocessing;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;

@Deprecated
public class SceneRenderBufferManager {
    
    private FrameBuffer multisampledScene;
    private FrameBuffer[] scene;
    
    private final FBTarget[] targets;
    
    private final boolean multisampled;
    
    public SceneRenderBufferManager(final RenderAPI api, final int multisamples, final FBTarget... targets) {
        this.targets = targets;
        this.multisampled = multisamples > 0;
        
        if (targets.length == 0) {
            throw new IllegalArgumentException("requires at least one FBTarget");
        } else {
            final int width = api.getSurface().getWidth();
            final int height = api.getSurface().getHeight();
            this.multisampledScene = api.createFrameBuffer(width, height, multisamples, targets.length);
            this.multisampledScene.assignTargetsB(targets);
            if (targets.length > 1 || this.multisampled) {
                this.scene = new FrameBuffer[targets.length];
                for (int i = 0; i < this.scene.length; i++) {
                    this.scene[i] = api.createFrameBuffer(width, height, 0, 1);
                    this.scene[i].assignTargetB(0, targets[i]);
                }
            }
        }
    }
    
    public FrameBuffer get(final int index) {
        return this.scene == null ? this.multisampledScene : this.scene[index];
    }
    
    public void beginRender() {
        this.multisampledScene.bindFrameBuffer();
    }
    
    public void endRender() {
        this.multisampledScene.unbindFrameBuffer();
        if (this.scene != null) {
            for (int i = 0; i < this.scene.length; i++) {
                this.multisampledScene.resolveToFrameBuffer(this.scene[i], this.targets[i].attachmentIndex);
            }
        }
    }
    
    public void resize(final int width, final int height) {
        this.multisampledScene = this.multisampledScene.resizedClone(width, height);
        if (this.scene != null) {
            for (int i = 0; i < this.scene.length; i++) {
                this.scene[i] = this.scene[i].resizedClone(width, height);
            }
        }
    }
    
}
