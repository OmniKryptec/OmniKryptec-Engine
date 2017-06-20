package omnikryptec.particles;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformMatrix;

public class ParticleShader extends Shader {

	private static final String VERTEX_FILE = "/omnikryptec/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "/omnikryptec/particles/particleFShader.txt";

	
	public final UniformFloat nrOfRows = new UniformFloat("nrRows");
	public final UniformMatrix projMatrix = new UniformMatrix("projectionMatrix");
	
	public ParticleShader() {
		super("ParticleShader", Shader.class.getResourceAsStream(VERTEX_FILE), Shader.class.getResourceAsStream(FRAGMENT_FILE), "position", "modelViewMatrix", "texOffsets", "blendFac");
		registerUniforms(nrOfRows, projMatrix);
	}


}
