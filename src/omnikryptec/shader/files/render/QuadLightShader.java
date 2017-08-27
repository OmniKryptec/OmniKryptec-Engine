package omnikryptec.shader.files.render;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec2;
import omnikryptec.shader.base.UniformVec3;

public class QuadLightShader extends Shader {

	public final UniformVec3 att = new UniformVec3("att");
	public final UniformSampler diffuse = new UniformSampler("diffuse");
	public final UniformSampler normal = new UniformSampler("normal");
	public final UniformSampler specular = new UniformSampler("specular");
	public final UniformSampler depth = new UniformSampler("depth");
	public final UniformVec3 light = new UniformVec3("lightu");
	public final UniformVec3 lightColor = new UniformVec3("lightColor");
	public final UniformMatrix viewv = new UniformMatrix("vm");
	public final UniformMatrix proj = new UniformMatrix("proj");

	public final UniformVec2 pixSizes = new UniformVec2("pixelSize");

	public QuadLightShader() {
		super("LightQuadShader", Shader.class.getResourceAsStream(Shader.DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(oc_shader_loc + "light_shader_frag_sq.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(depth, diffuse, specular, normal, light, lightColor, viewv, proj, pixSizes, att);
		start();
		diffuse.loadTexUnit(0);
		normal.loadTexUnit(1);
		specular.loadTexUnit(2);
		depth.loadTexUnit(3);
	}

}
