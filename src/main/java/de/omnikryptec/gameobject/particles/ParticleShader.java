package de.omnikryptec.gameobject.particles;

import omnikryptec.shader.base.*;

public class ParticleShader extends Shader {

	private static final String VERTEX_FILE = "/omnikryptec/gameobject/particles/particle_shader_vert.glsl";
	private static final String FRAGMENT_FILE = "/omnikryptec/gameobject/particles/particle_shader_frag.glsl";

	public final UniformFloat nrOfRows = new UniformFloat("nrRows");
	public final UniformMatrix projMatrix = new UniformMatrix("projectionMatrix");
	public final UniformVec4 uvs = new UniformVec4("uvs");
	
	public ParticleShader() {
		super("ParticleShader", Shader.class.getResourceAsStream(VERTEX_FILE),
				Shader.class.getResourceAsStream(FRAGMENT_FILE), new Attribute("position", 0),
				new Attribute("modelViewMatrix", 1), new Attribute("texOffsets", 5), new Attribute("blendFac", 6), new Attribute("color", 7));
		registerUniforms(nrOfRows, projMatrix, uvs);

	}

}
