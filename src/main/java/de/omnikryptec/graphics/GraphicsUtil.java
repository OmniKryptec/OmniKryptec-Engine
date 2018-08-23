package de.omnikryptec.graphics;

import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.settings.GameSettings;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.EnumCollection.RenderType;
import omnikryptec.util.EnumCollection.UpdateType;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GraphicsUtil {

	private static boolean cullingBackFace = false;
	private static boolean inWireframe = false;
	private static boolean antialiasing = false;
	private static boolean depthTesting = false;
	private static boolean scissor = false;
	private static boolean depthMask = true;
	private static BlendMode blendmode=BlendMode.DISABLE;
	
	
	public static boolean isGLContextAvailable() {
		return GLFW.glfwGetCurrentContext()!=0;
	}
	
	public static void enableDepthMask(boolean b) {
		if (depthMask && !b) {
			GL11.glDepthMask(b);
		} else if (!depthMask && b) {
			GL11.glDepthMask(b);
		}
	}

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

	public static void blendMode(BlendMode mode) {
		if(blendmode!=mode) {
			if(blendmode==BlendMode.DISABLE) {
				GL11.glEnable(GL11.GL_BLEND);
			}
			switch(mode) {
			case ADDITIVE:
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				break;
			case ALPHA:
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case MULTIPLICATIVE:
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
				break;
			case DISABLE:
				GL11.glDisable(GL11.GL_BLEND);
				break;
			default:
				break;
			}
			blendmode = mode;
		}
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

	public static void clear(Color c) {
		clear(c.getArray());
	}

	public static void clear(float[] f) {
		clear(f[0], f[1], f[2], f.length > 3 ? f[3] : 1);
	}

	private static float rad_fol,rad_med,rad_big;
	private static Vector3f cpos;
	private static long lastupdate = -1;
	
	public static boolean inRenderRange(Entity e, Camera c) {
		return inRenderRange(e.getTransform().getPosition(true), e.getType(), c);
	}

	public static boolean inRenderRange(Vector3f pos, RenderType type, Camera c) {
		//In andere Funktion auslagern hier wird zu oft gecalled
		if (needsUpdate(lastupdate, GameSettings.CHECKCHANGEFRAMES)) {
			lastupdate = OmniKryptecEngine.instance().getDisplayManager().getFramecount();
			rad_fol = OmniKryptecEngine.instance().getDisplayManager().getSettings().getFloat(GameSettings.RADIUS_FOLIAGE);
			rad_med = OmniKryptecEngine.instance().getDisplayManager().getSettings().getFloat(GameSettings.RADIUS_MEDIUM);
			rad_big = OmniKryptecEngine.instance().getDisplayManager().getSettings().getFloat(GameSettings.RADIUS_BIG);
			rad_fol *= rad_fol;
			rad_med *= rad_med;
			rad_big *= rad_big;
		}
		if (type == RenderType.ALWAYS) {
			return true;
		} else if (type == RenderType.FOLIAGE) {
			cpos = c.getTransform().getPosition(true);
			if (pos.lengthSquared() < cpos.lengthSquared() + rad_fol && pos.lengthSquared() > cpos.lengthSquared() - rad_fol) {
				return true;
			}
		} else if (type == RenderType.MEDIUM) {
			cpos = c.getTransform().getPosition(true);
			if (pos.lengthSquared() < cpos.lengthSquared() + rad_med && pos.lengthSquared() > cpos.lengthSquared() - rad_med) {
				return true;
			}
		} else if (type == RenderType.BIG) {
			cpos = c.getTransform().getPosition(true);
			if (pos.lengthSquared() < cpos.lengthSquared() + rad_big && pos.lengthSquared() > cpos.lengthSquared() - rad_big) {
				return true;
			}
		}
		return false;
	}

	public static boolean needsUpdate(long lastUpdate) {
		return needsUpdate(lastUpdate, 1);
	}
	
	public static boolean needsUpdate(long lastUpdate, int freq) {
		return needsUpdate(lastUpdate, freq, UpdateType.DYNAMIC);
	}
	
	public static boolean needsUpdate(long lastUpdate, UpdateType t) {
		return needsUpdate(lastUpdate, 1, t);
	}
	
	public static boolean needsUpdate(long lastUpdate, int frequenzy, UpdateType t) {
		if (lastUpdate < 0) {
			return true;
		}
		return t == UpdateType.DYNAMIC ? OmniKryptecEngine.instance().getDisplayManager().getFramecount() - frequenzy >= lastUpdate : false;
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
