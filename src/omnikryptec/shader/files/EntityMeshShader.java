package omnikryptec.shader.files;

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

public class EntityMeshShader extends Shader{

	private final UniformMatrix u_view = new UniformMatrix("viewmatrix");
	private final UniformMatrix u_projection = new UniformMatrix("projmatrix");
	
	private final UniformSampler tex = new UniformSampler("tex");
	private final UniformVec4 uvs = new UniformVec4("uvs");
	
	public EntityMeshShader(){
		super("EntityMeshShader",
				new AdvancedFile(SHADER_LOCATION, "entity_mesh_shader_vert.glsl"),
				new AdvancedFile(SHADER_LOCATION, "entity_mesh_shader_frag.glsl"), new Attribute("pos", 0), new Attribute("texcoords", 1),
				new Attribute("transmatrix",4), new Attribute("colour",8));
		super.registerUniforms(u_view, u_projection, tex, uvs);
		start();
		tex.loadTexUnit(0);
	}
	
	
	private Texture tmp;
	@Override
	public void onModelRender(AdvancedModel m){
        m.getModel().getVao().bind(0, 1, 4, 5, 6, 7, 8);
		tmp = m.getMaterial().getTexture(Material.DIFFUSE);
		if(tmp!=null){
			tmp.bindToUnit(0);
			uvs.loadVec4(tmp.getUVs());
		}
	}
	
	@Override
	public void onRenderStart(Scene s) {
		u_view.loadMatrix(s.getCamera().getViewMatrix());
		u_projection.loadMatrix(s.getCamera().getProjectionMatrix());
	}
	
}
