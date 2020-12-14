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

package de.omnikryptec.render.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render3.IProjection;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.math.transform.Transform3Df;

public class AdvancedShaderSlot extends AbstractProjectedShaderSlot {
    
    private final UniformMatrix viewProjectionUniform;
    private final UniformMatrix transformUniform;
    
    private IProjection projection;
    private Transform3Df transform;
    
    public static final int REFLECTION_TEXTURE_UNIT = 1;
    
    public AdvancedShaderSlot() {
        this.shaderProgram.create("engineRenderBatch2DShaderRef");
        this.transformUniform = this.shaderProgram.getUniform("u_transform");
        this.viewProjectionUniform = this.shaderProgram.getUniform("u_projview");
        
        final UniformSampler sampler = this.shaderProgram.getUniform("sampler");
        final UniformSampler refl = this.shaderProgram.getUniform("reflected");
        this.shaderProgram.bindShader();
        this.transformUniform.loadMatrix(Mathf.IDENTITY4f);
        this.viewProjectionUniform.loadMatrix(new Matrix4f().ortho2D(0, 1, 0, 1));
        sampler.setSampler(0);
        refl.setSampler(REFLECTION_TEXTURE_UNIT);
    }
    
    @Override
    protected void onBound() {
        if (this.projection != null) {
            this.viewProjectionUniform.loadMatrix(this.projection.getProjection());
        }
        if (this.transform != null) {
            this.transformUniform.loadMatrix(this.transform.worldspace());
        }
    }
    
    @Override
    public void setProjection(final IProjection projection) {
        this.projection = projection;
    }
    
    public void setViewProjectionMatrix(final Matrix4fc mat) {
        this.shaderProgram.bindShader();
        this.viewProjectionUniform.loadMatrix(mat);
    }
    
    public void setTransformMatrix(final Matrix4fc mat) {
        this.shaderProgram.bindShader();
        this.transformUniform.loadMatrix(mat);
    }
    
    @Override
    public void setTransform(final Transform3Df transform) {
        this.transform = transform;
        if (this.transform == null) {
            setTransformMatrix(Mathf.IDENTITY4f);
        }
    }
    
}
