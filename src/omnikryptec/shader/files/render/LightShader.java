package omnikryptec.shader.files.render;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec2;
import omnikryptec.shader.base.UniformVec3;

public class LightShader extends Shader {

	public final UniformSampler diffuse = new UniformSampler("diffuse");
	public final UniformSampler normal = new UniformSampler("normal");
	public final UniformSampler specular = new UniformSampler("specular");
	public final UniformSampler depth = new UniformSampler("depth");
	public final UniformVec3 light = new UniformVec3("lightu");
	public final UniformVec3 lightColor = new UniformVec3("lightColor");
	public final UniformMatrix invprojv = new UniformMatrix("invprojv");

	public final UniformVec2 pixSizes = new UniformVec2("pixelSize");

	public final UniformVec3 cam = new UniformVec3("campos");

	public LightShader() {
		super(LightShader.class.getResourceAsStream(Shader.DEFAULT_PP_VERTEX_SHADER_LOC),
				LightShader.class.getResourceAsStream(oc_shader_loc + "light_shader_frag.glsl"), "position");
		registerUniforms(depth, diffuse, specular, normal, light, lightColor, invprojv, pixSizes, cam);
		start();
		diffuse.loadTexUnit(0);
		normal.loadTexUnit(1);
		specular.loadTexUnit(2);
		depth.loadTexUnit(3);
	}

}
