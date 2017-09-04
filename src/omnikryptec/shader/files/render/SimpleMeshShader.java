package omnikryptec.shader.files.render;

import omnikryptec.main.AbstractScene;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Material;
import omnikryptec.resource.texture.Texture;
import omnikryptec.shader.base.Attribute;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec4;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.RenderUtil;

public class SimpleMeshShader extends Shader {

	private final UniformMatrix u_view = new UniformMatrix("viewmatrix");
	private final UniformMatrix u_projection = new UniformMatrix("projmatrix");

	private final UniformSampler tex = new UniformSampler("tex");
	private final UniformVec4 uvs = new UniformVec4("uvs");

	public SimpleMeshShader() {
		super(new AdvancedFile(SHADER_LOCATION_RENDER, "simple_mesh_shader_vert.glsl"),
				new AdvancedFile(SHADER_LOCATION_RENDER, "simple_mesh_shader_frag.glsl"), new Attribute("pos", 0),
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
		if(m.getMaterial().hasTransparency()) {
			RenderUtil.cullBackFaces(false);
		}
	}

	@Override
	public void onModelRenderEnd(AdvancedModel m) {
        if (m.getMaterial().hasTransparency()) {
            RenderUtil.cullBackFaces(true);
        }
	}
	
	@Override
	public void onRenderStart(AbstractScene s) {
		u_view.loadMatrix(s.getCamera().getViewMatrix());
		u_projection.loadMatrix(s.getCamera().getProjectionMatrix());
	}

}
