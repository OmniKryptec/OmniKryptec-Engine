package omnikryptec.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import omnikryptec.resource.texture.Texture;

public class OpenGL {
	
	public static void gl11viewport(int[] vp) {
		gl11viewport(vp[0], vp[1], vp[2], vp[3]);
	}
	
	public static void gl11viewport(int x, int y, int w, int h) {
		GL11.glViewport(x, y, w, h);
	}
	
	// OpenGL-Objects
	public static void gl20enableVertexAttribArray(int i) {
		GL20.glEnableVertexAttribArray(i);
	}

	public static void gl30bindVertexArray(int i) {
		GL30.glBindVertexArray(i);
	}

	public static void gl20disableVertexAttribArray(int i) {
		GL20.glDisableVertexAttribArray(i);
	}

	public static void gl30deleteVertexArrays(int i) {
		GL30.glDeleteVertexArrays(i);
	}

	public static void gl20vertexAttribPointer(int attributeNr, int attributeSize, int glmode, boolean normalized,
			int i, int j) {
		GL20.glVertexAttribPointer(attributeNr, attributeSize, glmode, normalized, i, j);
	}

	public static void gl30vertexAttribIPointer(int attributeNr, int attributeSize, int glmode, int i, int j) {
		GL30.glVertexAttribIPointer(attributeNr, attributeSize, glmode, i, j);
	}

	public static int gl30genVertexArrays() {
		return GL30.glGenVertexArrays();
	}

	public static int gl15genBuffers() {
		return GL15.glGenBuffers();
	}

	public static void gl15bindBuffer(int type, int id) {
		GL15.glBindBuffer(type, id);
	}

	public static void gl15bufferData(int type, FloatBuffer data, int glmode) {
		GL15.glBufferData(type, data, glmode);
	}

	public static void gl15bufferData(int type, IntBuffer data, int glmode) {
		GL15.glBufferData(type, data, glmode);
	}

	public static void gl15bufferData(int type, int i, int glmode) {
		GL15.glBufferData(type, i, glmode);
	}

	public static void gl15bufferSubData(int type, int i, FloatBuffer buffer) {
		GL15.glBufferSubData(type, i, buffer);
	}

	public static void gl33vertexAttribDivisor(int attributeNr, int divisor) {
        GL33.glVertexAttribDivisor(attributeNr, divisor);		
	}

	public static void gl15deleteBuffers(int id) {
    	GL15.glDeleteBuffers(id);		
	}
	
	// Textures

	/**
	 * Should be used via {@link Texture}
	 * 
	 * @param type
	 * @param id
	 */
	public static void gl11bindTexture(int type, int id) {
		GL11.glBindTexture(type, id);
	}

	public static void gl13activeTextureGL(int unit) {
		GL13.glActiveTexture(unit);
	}

	public static void gl13activeTextureZB(int unit) {
		gl13activeTextureGL(GL13.GL_TEXTURE0 + unit);
	}

	// Rendering
	public static void gl11drawElements(int mode, int indexcount, int type, long indices) {
		GL11.glDrawElements(mode, indexcount, type, indices);
	}

	public static void gl31drawElementsInstanced(int mode, int indexcount, int type, long indices, int count) {
		GL31.glDrawElementsInstanced(mode, indexcount, type, indices, count);
	}

	public static void gl11drawArrays(int glmode, int offset, int count) {
		GL11.glDrawArrays(glmode, offset, count);
	}

	public static void gl31drawArraysInstanced(int glmode, int i, int indexCount, int count) {
		GL31.glDrawArraysInstanced(glmode, i, indexCount, count);		
	}

	
	// Querys
	public static int gl15genQueries() {
		return GL15.glGenQueries();
	}

	public static void gl15deleteQueries(int id) {
		GL15.glDeleteQueries(id);
	}

	public static void gl15beginQuery(int type, int id) {
		GL15.glBeginQuery(type, id);
	}

	public static void gl15endQuery(int id) {
		GL15.glEndQuery(id);
	}

	public static int gl15getQueryObjecti(int id, int pname) {
		return GL15.glGetQueryObjecti(id, pname);
	}

	// Shader
	public static void gl20loadMatrix4f(int location, boolean transposed, FloatBuffer matrixbuffer) {
		GL20.glUniformMatrix4fv(location, transposed, matrixbuffer);
	}

	public static int gl20getUniformLocation(int shaderid, CharSequence name) {
		return GL20.glGetUniformLocation(shaderid, name);
	}

	public static int gl20createProgram() {
		return GL20.glCreateProgram();
	}

	public static void gl20attachShader(int programid, int shaderid) {
		GL20.glAttachShader(programid, shaderid);
	}

	public static void gl20linkProgram(int program) {
		GL20.glLinkProgram(program);
	}

	public static void gl20validateProgram(int program) {
		GL20.glValidateProgram(program);
	}

	public static void gl20useProgram(int program) {
		GL20.glUseProgram(program);
	}

	public static void gl20deleteShader(int shaderid) {
		GL20.glDeleteShader(shaderid);
	}

	public static void gl20detachShader(int programid, int shaderid) {
		GL20.glDetachShader(programid, shaderid);
	}

	public static void gl20deleteProgram(int program) {
		GL20.glDeleteProgram(program);
	}

	public static void gl20bindAttribLocation(int program, int index, CharSequence name) {
		GL20.glBindAttribLocation(program, index, name);
	}

	public static int gl20createShader(int type) {
		return GL20.glCreateShader(type);
	}

	public static void gl20shaderSource(int shader, CharSequence source) {
		GL20.glShaderSource(shader, source);
	}

	public static void gl20compileShader(int shader) {
		GL20.glCompileShader(shader);
	}

	public static int gl20getShaderi(int shader, int glenum) {
		return GL20.glGetShaderi(shader, glenum);
	}

	public static String gl20getShaderInfoLog(int shader, int info) {
		return GL20.glGetShaderInfoLog(shader, info);
	}

}
