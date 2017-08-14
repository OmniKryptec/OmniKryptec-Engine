package omnikryptec.util.error;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class SystemInfoItem implements ErrorItem{

	@Override
	public String getError() {
		StringBuilder b = new StringBuilder();
		try{
			b.append("OpenGL-Vendor: ").append(GL11.glGetString(GL11.GL_VENDOR)).append("\n");
			b.append("OpenGL-Version: ").append(GL11.glGetString(GL11.GL_VERSION)).append("\n");
			b.append("Renderer: ").append(GL11.glGetString(GL11.GL_RENDERER)).append("\n");
			b.append("GLFW: ").append(GLFW.glfwGetVersionString()).append("\n");
		}catch(Exception e){
			b.append("No OpenGL information available.").append("\n");
		}
		b.append("\n");
		b.append("Java-Version: ").append(System.getProperty("java.version"));
		b.append("\n");
		return b.toString();
	}

}
