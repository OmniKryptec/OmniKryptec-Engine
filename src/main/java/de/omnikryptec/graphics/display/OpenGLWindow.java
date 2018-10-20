package de.omnikryptec.graphics.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class OpenGLWindow extends Window {

	private int mav, miv;

	OpenGLWindow(OpenGLWindowInfo info) {
		super(info);
		this.mav = info.getMajVersion();
		this.miv = info.getMinVersion();
	}

	@Override
	protected void setAdditionalGlfwWindowHints() {
		if (mav > 3 || (mav > 2 && miv > 1)) {
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, mav);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, miv);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		}
	}

	@Override
	protected void onInitFinish() {
		GLFW.glfwMakeContextCurrent(getWindowID());
		GL.createCapabilities();
	}

}
