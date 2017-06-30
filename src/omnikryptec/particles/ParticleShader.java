package omnikryptec.particles;

import omnikryptec.shader.base.Attribute;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformFloat;
import omnikryptec.shader.base.UniformMatrix;

public class ParticleShader extends Shader {

	private static final String VERTEX_FILE = "/omnikryptec/particles/particle_shader_vert.glsl";
	private static final String FRAGMENT_FILE = "/omnikryptec/particles/particle_shader_frag.glsl";

	public final UniformFloat nrOfRows = new UniformFloat("nrRows");
	public final UniformMatrix projMatrix = new UniformMatrix("projectionMatrix");

	public ParticleShader() {
		super("ParticleShader", Shader.class.getResourceAsStream(VERTEX_FILE),
				Shader.class.getResourceAsStream(FRAGMENT_FILE), new Attribute("position", 0),
				new Attribute("modelViewMatrix", 1), new Attribute("texOffsets", 5), new Attribute("blendFac", 6));
		registerUniforms(nrOfRows, projMatrix);

	}

}
