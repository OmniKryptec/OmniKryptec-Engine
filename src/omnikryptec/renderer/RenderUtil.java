package omnikryptec.renderer;

import org.lwjgl.opengl.GL11;

public class RenderUtil {

	private static boolean depthtest = false;
	private static boolean faceculling = false;

	public static void enableDepthTest(boolean b) {
		if (b && !depthtest) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			depthtest = true;
		} else if (depthtest) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			depthtest = false;
		}
	}

	public static void enabledCulling(boolean b) {
		if (b && !faceculling) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_FRONT);
			faceculling = true;
		} else {
			GL11.glDisable(GL11.GL_CULL_FACE);
			faceculling = false;
		}
	}

}
