package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec2;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class LightShader extends Shader {


	
	public static final UniformSampler diffuse = new UniformSampler("diffuse");
	public static final UniformSampler normal = new UniformSampler("normal");
	public static final UniformSampler specular = new UniformSampler("specular");
	public static final UniformSampler depth = new UniformSampler("depth");
	public static final UniformVec4 light = new UniformVec4("lightu");
	public static final UniformVec3 lightColor = new UniformVec3("lightColor");
	public static final UniformVec2 planes = new UniformVec2("planes");
	public static final UniformMatrix viewv = new UniformMatrix("vm");
	public static final UniformMatrix viewv2 = new UniformMatrix("vm2");
	public static final UniformVec3 abc = new UniformVec3("cam");
	
	public LightShader() {
		super(LightShader.class.getResourceAsStream(oc_shader_loc+"light_shader_vert.glsl"), LightShader.class.getResourceAsStream(oc_shader_loc+"light_shader_frag.glsl"), "position", "vpos", abc,viewv2,depth,diffuse,specular,normal,light,lightColor,planes, viewv);
		start();
		diffuse.loadTexUnit(0);
		normal.loadTexUnit(1);
		specular.loadTexUnit(2);
		depth.loadTexUnit(3);
	}

}
