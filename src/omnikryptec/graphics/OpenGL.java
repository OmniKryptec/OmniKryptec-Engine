package omnikryptec.graphics;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

public class OpenGL {

		//Rendering
		public static void gl11drawElements(int mode, int indexcount, int type, long indices){
            GL11.glDrawElements(mode, indexcount, type, indices);
		}
		
        public static void gl31rawElementsInstanced(int mode, int indexcount, int type, long indices, int count){
        	GL31.glDrawElementsInstanced(mode, indexcount, type, indices, count);
        }
        
		
		
		//Shader
		public static void gl20loadMatrix4f(int location, boolean transposed, FloatBuffer matrixbuffer){
	        GL20.glUniformMatrix4fv(location, transposed, matrixbuffer);
		}
		
		public static int gl20getUniformLocation(int shaderid, CharSequence name){
			return GL20.glGetUniformLocation(shaderid, name);
		}
		
		public static int gl20createProgram(){
			return GL20.glCreateProgram();
		}
		
		public static void gl20attachShader(int programid, int shaderid){
			GL20.glAttachShader(programid, shaderid);
		}
		
		public static void gl20linkProgram(int program){
			GL20.glLinkProgram(program);
		}
		
		public static void gl20validateProgram(int program){
			GL20.glValidateProgram(program);
		}
		
		public static void gl20useProgram(int program){
			GL20.glUseProgram(program);
		}
		
		public static void gl20deleteShader(int shaderid){
			GL20.glDeleteShader(shaderid);
		}
		
		public static void gl20detachShader(int programid, int shaderid){
			GL20.glDetachShader(programid, shaderid);
		}
		
		public static void gl20deleteProgram(int program){
			GL20.glDeleteProgram(program);
		}
		
		public static void gl20bindAttribLocation(int program, int index, CharSequence name){
			GL20.glBindAttribLocation(program, index, name);
		}
		
		public static int gl20createShader(int type){
			return GL20.glCreateShader(type);
		}
		
		public static void gl20shaderSource(int shader, CharSequence source){
			GL20.glShaderSource(shader, source);
		}
		
		public static void gl20compileShader(int shader){
			GL20.glCompileShader(shader);
		}
		
		public static int gl20getShaderi(int shader, int glenum){
			return GL20.glGetShaderi(shader, glenum);
		}
		
		public static String gl20getShaderInfoLog(int shader, int info){
			return GL20.glGetShaderInfoLog(shader, info);
		}

	
}
