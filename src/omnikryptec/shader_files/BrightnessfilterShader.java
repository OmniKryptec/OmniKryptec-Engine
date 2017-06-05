package omnikryptec.shader_files;

import org.lwjgl.util.vector.Vector4f;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec4;

public class BrightnessfilterShader extends Shader {

	public final UniformSampler scene = new UniformSampler("scene");
	public final UniformSampler extra = new UniformSampler("extra");
	public final UniformVec4 colofextrainfo = new UniformVec4("info");
	
	public BrightnessfilterShader(Vector4f colofextrainfo) {
		super(Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC), Shader.class.getResourceAsStream(oc_shader_loc+"bloom_shader_frag.glsl"), Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(scene, extra, this.colofextrainfo);
		start();
		scene.loadTexUnit(0);
		extra.loadTexUnit(1);
		this.colofextrainfo.loadVec4(colofextrainfo);
	}

}
