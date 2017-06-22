package omnikryptec.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity.RenderType;
import omnikryptec.entity.Rangeable;
import omnikryptec.main.OmniKryptecEngine;

public class RenderUtil {

	private static boolean cullingBackFace = false;
	private static boolean inWireframe = false;
	private static boolean isAlphaBlending = false;
	private static boolean additiveBlending = false;
	private static boolean antialiasing = false;
	private static boolean depthTesting = false;
	private static boolean scissor = false;

	public static void enableScissor(boolean b) {
		if (b && !scissor) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			scissor = true;
		} else if (!b && scissor) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			scissor = false;
		}
	}

	public static void scissor(int i1, int i2, int i3, int i4) {
		enableScissor(true);
		GL11.glScissor(i1, i2, i3, i4);
	}

	public static void antialias(boolean enable) {
		if (enable && !antialiasing) {
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			antialiasing = true;
		} else if (!enable && antialiasing) {
			GL11.glDisable(GL13.GL_MULTISAMPLE);
			antialiasing = false;
		}
	}

	public static boolean isAntialias() {
		return antialiasing;
	}

	public static void enableAlphaBlending() {
		if (!isAlphaBlending) {
			if(!additiveBlending){
				GL11.glEnable(GL11.GL_BLEND);
			}
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			isAlphaBlending = true;
			additiveBlending = false;
		}
	}

	public static boolean isAlphaBlending() {
		return isAlphaBlending;
	}

	public static void enableAdditiveBlending() {
		if (!additiveBlending) {
			if(!isAlphaBlending){
				GL11.glEnable(GL11.GL_BLEND);
			}
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			additiveBlending = true;
			isAlphaBlending = false;
		}
	}

	public static boolean isAdditiveBlending() {
		return additiveBlending;
	}

	public static void disableBlending() {
		if (isAlphaBlending || additiveBlending) {
			GL11.glDisable(GL11.GL_BLEND);
			isAlphaBlending = false;
			additiveBlending = false;
		}
	}

	public static boolean isBlending() {
		return isAlphaBlending || additiveBlending;
	}

	public static void enableDepthTesting(boolean enable) {
		if (enable && !depthTesting) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			depthTesting = true;
		} else if (!enable && depthTesting) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			depthTesting = false;
		}
	}

	public static boolean isDepthTesting() {
		return depthTesting;
	}

	public static void cullBackFaces(boolean cull) {
		if (cull && !cullingBackFace) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			cullingBackFace = true;
		} else if (!cull && cullingBackFace) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			cullingBackFace = false;
		}
	}

	public static boolean isCullBackFaces() {
		return cullingBackFace;
	}

	public static void goWireframe(boolean goWireframe) {
		if (goWireframe && !inWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			inWireframe = true;
		} else if (!goWireframe && inWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			inWireframe = false;
		}
	}

	public static boolean isWireframe() {
		return inWireframe;
	}

	public static void clear(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public static void clear(float[] f) {
		clear(f[0], f[1], f[2], f.length > 3 ? f[3] : 1);
	}

	private static float rad;
	private static Vector3f pos, cpos;

	public static boolean inRenderRange(Rangeable e, Camera c) {
		if (e.getType() == RenderType.ALWAYS) {
			return true;
		} else if (e.getType() == RenderType.FOLIAGE) {
			rad = OmniKryptecEngine.instance().getDisplayManager().getSettings().getRadiusFoliage();
			pos = e.getAbsolutePos();
			cpos = c.getAbsolutePos();
			if (pos.lengthSquared() < cpos.lengthSquared() + rad && pos.lengthSquared() > cpos.lengthSquared() - rad) {
				return true;
			}
		} else if (e.getType() == RenderType.MEDIUM) {
			rad = OmniKryptecEngine.instance().getDisplayManager().getSettings().getRadiusMedium();
			pos = e.getAbsolutePos();
			cpos = c.getAbsolutePos();
			if (pos.lengthSquared() < cpos.lengthSquared() + rad && pos.lengthSquared() > cpos.lengthSquared() - rad) {
				return true;
			}
		} else if (e.getType() == RenderType.BIG) {
			rad = OmniKryptecEngine.instance().getDisplayManager().getSettings().getRadiusBig();
			pos = e.getAbsolutePos();
			cpos = c.getAbsolutePos();
			if (pos.lengthSquared() < cpos.lengthSquared() + rad && pos.lengthSquared() > cpos.lengthSquared() - rad) {
				return true;
			}
		}
		return false;
	}

	// public static void setLightScissor(Vector4f lightpos, int sx, int sy){
	// int[] rect = {0,0,sx,sy};
	// float d;
	// float r = lightpos.w;
	// float r2 = r*r;
	// Vector4f l = lightpos;
	// Vector4f l2 = new Vector4f(l.x*l.x, l.y*l.y, l.z*l.z, l.w*l.w);
	// float el = 1.2f;
	// float e2 = 1.2f*((float)Display.getWidth()/(float)Display.getHeight());
	//
	// }

}
