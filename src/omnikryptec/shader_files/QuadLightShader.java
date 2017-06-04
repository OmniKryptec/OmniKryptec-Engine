package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec2;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class QuadLightShader extends Shader {

	public final UniformSampler diffuse = new UniformSampler("diffuse");
	public final UniformSampler normal = new UniformSampler("normal");
	public final UniformSampler specular = new UniformSampler("specular");
	public final UniformSampler depth = new UniformSampler("depth");
	public final UniformVec4 light = new UniformVec4("lightu");
	public final UniformVec3 lightColor = new UniformVec3("lightColor");
	public final UniformMatrix viewv = new UniformMatrix("vm");
	public final UniformMatrix proj = new UniformMatrix("proj");
	
	public final UniformVec2 pixSizes = new UniformVec2("pixelSize");
	
	public QuadLightShader() {
		super(Shader.class.getResourceAsStream(oc_shader_loc+"light_shader_vert.glsl"), Shader.class.getResourceAsStream(oc_shader_loc+"light_shader_frag_sq.glsl"), Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(depth,diffuse,specular,normal,light,lightColor, viewv, proj, pixSizes);
		start();
		diffuse.loadTexUnit(0);
		normal.loadTexUnit(1);
		specular.loadTexUnit(2);
		depth.loadTexUnit(3);
	}

}
