package de.omnikryptec.shader.base;

import org.joml.Matrix4f;

/**
 * UniformMatrixArray
 *
 * @author Panzer1119
 */
public class UniformMatrixArray extends Uniform {

	private final UniformMatrix[] uniformMatrices;

	public UniformMatrixArray(String name, int size) {
		super(name);
		uniformMatrices = new UniformMatrix[size];
		for (int i = 0; i < size; i++) {
			uniformMatrices[i] = new UniformMatrix(name + "[" + i + "]");
		}
	}

	@Override
	protected final void storeUniformLocation(Shader programID) {
		for (UniformMatrix uniformMatrix : uniformMatrices) {
			uniformMatrix.storeUniformLocation(programID);
		}
	}

	public final UniformMatrixArray loadMatrixArray(Matrix4f[] matrices) {
		for (int i = 0; i < matrices.length; i++) {
			uniformMatrices[i].loadMatrix(matrices[i]);
		}
		return this;
	}

}
