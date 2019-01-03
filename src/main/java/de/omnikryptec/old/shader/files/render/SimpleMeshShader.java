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

package de.omnikryptec.old.shader.files.render;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.shader.base.uniform.UniformMatrix;
import de.omnikryptec.graphics.shader.base.uniform.UniformSampler;
import de.omnikryptec.graphics.shader.base.uniform.UniformVec4;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.resource.model.Material;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.shader.base.Attribute;
import de.omnikryptec.old.shader.base.Shader;
import org.joml.Vector4f;

public class SimpleMeshShader extends Shader {

    private final UniformMatrix u_view = new UniformMatrix("viewmatrix");
    private final UniformMatrix u_projection = new UniformMatrix("projmatrix");

    private final UniformSampler tex = new UniformSampler("tex");
    private final UniformVec4 uvs = new UniformVec4("uvs");

    public SimpleMeshShader() {
	super(new AdvancedFile(true, SHADER_LOCATION_RENDER, "simple_mesh_shader_vert.glsl"),
		new AdvancedFile(true, SHADER_LOCATION_RENDER, "simple_mesh_shader_frag.glsl"), new Attribute("pos", 0),
		new Attribute("texcoords", 1), new Attribute("transmatrix", 4), new Attribute("colour", 8));
	super.registerUniforms(u_view, u_projection, tex, uvs);
	start();
	tex.loadTexUnit(0);
    }

    private Texture tmp;

    @Override
    public void onModelRenderStart(AdvancedModel m) {
	m.getModel().getVao().bind(0, 1, 4, 5, 6, 7, 8);
	tmp = m.getMaterial().getTexture(Material.DIFFUSE);
	if (tmp != null) {
	    tmp.bindToUnitOptimized(0);
	    uvs.loadVec4(tmp.getUVs());
	}
	if (m.getMaterial().hasTransparency()) {
	    GraphicsUtil.cullBackFaces(false);
	}
    }

    @Override
    public void onModelRenderEnd(AdvancedModel m) {
	if (m.getMaterial().hasTransparency()) {
	    GraphicsUtil.cullBackFaces(true);
	}
    }

    @Override
    public void onRenderStart(AbstractScene3D s, Vector4f cp) {
	u_view.loadMatrix(s.getCamera().getViewMatrix());
	u_projection.loadMatrix(s.getCamera().getProjectionMatrix());
    }

}
