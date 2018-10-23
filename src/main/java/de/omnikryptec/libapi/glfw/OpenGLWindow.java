package de.omnikryptec.libapi.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class OpenGLWindow extends Window<OpenGLWindowInfo> {

	public OpenGLWindow(OpenGLWindowInfo info) {
		super(info);
		GLFW.glfwMakeContextCurrent(getWindowID());
		GL.createCapabilities();
		GLFW.glfwSwapInterval(info.isVsync() ? 1 : 0);
	}

	@Override
	protected void setAdditionalGlfwWindowHints(OpenGLWindowInfo info) {
		int mav = info.getMajVersion();
		int miv = info.getMinVersion();
		if (mav > 3 || (mav > 2 && miv > 1)) {
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, mav);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, miv);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		}
	}

	@Override
	protected void swap() {
		GLFW.glfwSwapBuffers(windowId);		
	}

}
