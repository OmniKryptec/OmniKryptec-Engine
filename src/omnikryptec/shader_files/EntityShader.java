package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;

public class EntityShader extends Shader {

	private static final String loc = "/omnikryptec/shader_files/";
	
	public static final UniformMatrix transformation = new UniformMatrix("transmatrix");
	public static final UniformMatrix view = new UniformMatrix("viewmatrix");
	public static final UniformMatrix projection = new UniformMatrix("projmatrix");
	public static final UniformSampler tex = new UniformSampler("tex"); 
	
	public EntityShader() {
		super(EntityShader.class.getResourceAsStream(loc+"entity_shader_vert.glsl"), EntityShader.class.getResourceAsStream(loc+"entity_shader_frag.glsl"), "pos", "texcoords", "normal", "tangent", transformation, view, projection, tex);
		start();
		tex.loadTexUnit(0);
	}

}
