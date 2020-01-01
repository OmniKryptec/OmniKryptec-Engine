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

package de.omnikryptec.old.postprocessing.main;

import de.omnikryptec.old.shader.base.Shader;

import java.util.List;

public abstract class PostProcessingStageShaded extends PostProcessingStage {

    private Shader shader;

    protected PostProcessingStageShaded() {
    }

    public PostProcessingStageShaded(Shader shader) {
	this.shader = shader;
    }

    protected final void setShader(Shader shader) {
	this.shader = shader;
    }

    protected final Shader getShader() {
	return shader;
    }

    @Override
    public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
	shader.start();
	bindTexture(before, beforelist, shader, stage);
	renderQuad(true);
	afterRendering();
    }

    public abstract void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using,
	    int stage);

    public void afterRendering() {
    }

}
