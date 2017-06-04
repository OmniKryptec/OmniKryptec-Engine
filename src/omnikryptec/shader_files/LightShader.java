package omnikryptec.shader_files;

import java.io.InputStream;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec2;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class LightShader extends Shader {

	public static final LightShader DEFAULT_ATTENUATION_LIGHTSHADER = new LightShader(LightShader.class.getResourceAsStream(oc_shader_loc+"light_shader_vert.glsl"), LightShader.class.getResourceAsStream(oc_shader_loc+"light_shader_frag.glsl"));

	
	public final UniformSampler diffuse = new UniformSampler("diffuse");
	public final UniformSampler normal = new UniformSampler("normal");
	public final UniformSampler specular = new UniformSampler("specular");
	public final UniformSampler depth = new UniformSampler("depth");
	public final UniformVec4 light = new UniformVec4("lightu");
	public final UniformVec3 lightColor = new UniformVec3("lightColor");
	public final UniformMatrix viewv = new UniformMatrix("vm");
	public final UniformMatrix proj = new UniformMatrix("proj");
	
	public final UniformVec2 pixSizes = new UniformVec2("pixelSize");
	
	public LightShader(InputStream vertexsh, InputStream fragmentsh) {
		super(vertexsh, fragmentsh, "position");
		registerUniforms(depth,diffuse,specular,normal,light,lightColor, viewv, proj, pixSizes);
		start();
		diffuse.loadTexUnit(0);
		normal.loadTexUnit(1);
		specular.loadTexUnit(2);
		depth.loadTexUnit(3);
	}
	
}
