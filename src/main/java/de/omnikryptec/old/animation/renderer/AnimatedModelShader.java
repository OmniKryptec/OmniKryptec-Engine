/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.animation.renderer;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.shader.base.UniformMatrix;
import de.omnikryptec.graphics.shader.base.UniformMatrixArray;
import de.omnikryptec.graphics.shader.base.UniformSampler;
import de.omnikryptec.graphics.shader.base.UniformVec3;
import de.omnikryptec.old.animation.AnimatedModel;
import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.Scene3D;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.resource.model.Material;
import de.omnikryptec.old.settings.GameSettings;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.util.EnumCollection.BlendMode;
import de.omnikryptec.old.util.Instance;

public class AnimatedModelShader extends Shader {

    //private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
    //private static final int DIFFUSE_TEX_UNIT = 0;
    private static final AdvancedFile VERTEX_SHADER = new AdvancedFile(true, "", "de", "omnikryptec", "animation", "renderer", "animatedEntityVertex.glsl");
    private static final AdvancedFile FRAGMENT_SHADER = new AdvancedFile(true, "", "de", "omnikryptec", "animation", "renderer", "animatedEntityFragment.glsl");

    public final UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public final UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public final UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
    protected UniformMatrixArray jointTransforms = new UniformMatrixArray("jointTransforms", Instance.getGameSettings().getInteger(GameSettings.ANIMATION_MAX_JOINTS));
    private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

    /**
     * Creates the shader program for the {@link AnimatedModelRenderer} by
     * loading up the vertex and fragment shader code files. It also gets the
     * location of all the specified uniform variables, and also indicates that
     * the diffuse texture will be sampled from texture unit 0.
     */
    public AnimatedModelShader() {
        super(VERTEX_SHADER.createInputStream(), FRAGMENT_SHADER.createInputStream(), "in_position", "in_textureCoords", "in_normal", "in_tangents", "in_jointIndices", "in_weights");
        registerUniforms(transformationMatrix, viewMatrix, projectionMatrix, diffuseMap, lightDirection, jointTransforms);
        start();
        diffuseMap.loadTexUnit(0);
    }

    private AnimatedModel model;

    @Override
    public void onModelRenderStart(AdvancedModel m) {
        model = (AnimatedModel) m;
        model.getModel().getVao().bind(0, 1, 2, 3, 4, 5);
        model.getMaterial().getTexture(Material.DIFFUSE).bindToUnitOptimized(0);
        jointTransforms.loadMatrixArray(model.getJointTransforms());
        GraphicsUtil.blendMode(BlendMode.DISABLE);
        GraphicsUtil.enableDepthTesting(true);
    }

    public void onRenderStart(Scene3D s) {
        viewMatrix.loadMatrix(s.getCamera().getViewMatrix());
        projectionMatrix.loadMatrix(s.getCamera().getProjectionMatrix());
    }

    @Override
    public void onRenderInstance(Entity e) {
        transformationMatrix.loadMatrix(e.getTransformation());
    }

}
