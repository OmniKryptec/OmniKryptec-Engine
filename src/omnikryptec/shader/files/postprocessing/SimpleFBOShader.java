package omnikryptec.shader.files.postprocessing;

import omnikryptec.shader.base.Shader;
import omnikryptec.util.AdvancedFile;

public class SimpleFBOShader extends Shader{

	public SimpleFBOShader() {
		super(DEF_SHADER_LOC_PP_VS,
				new AdvancedFile(SHADER_LOCATION_PP, "simple_fbo_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
	}
	
}
