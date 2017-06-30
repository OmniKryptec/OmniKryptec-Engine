package omnikryptec.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class UniformMatrix extends Uniform {

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public UniformMatrix(String name) {
        super(name);
    }

    public void loadMatrix(Matrix4fc matrix) {
        matrixBuffer.put(matrix.m00());
        matrixBuffer.put(matrix.m01());
        matrixBuffer.put(matrix.m02());
        matrixBuffer.put(matrix.m03());
        matrixBuffer.put(matrix.m10());
        matrixBuffer.put(matrix.m11());
        matrixBuffer.put(matrix.m12());
        matrixBuffer.put(matrix.m13());
        matrixBuffer.put(matrix.m20());
        matrixBuffer.put(matrix.m21());
        matrixBuffer.put(matrix.m22());
        matrixBuffer.put(matrix.m23());
        matrixBuffer.put(matrix.m30());
        matrixBuffer.put(matrix.m31());
        matrixBuffer.put(matrix.m32());
        matrixBuffer.put(matrix.m33());
        //funktioniert nicht
        //matrixBuffer = matrix.get(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
    }

}
