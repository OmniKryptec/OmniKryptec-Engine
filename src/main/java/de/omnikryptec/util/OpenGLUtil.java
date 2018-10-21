package de.omnikryptec.util;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class OpenGLUtil {

	public static enum BlendMode {
		ADDITIVE, ALPHA, MULTIPLICATIVE
	}

	public static enum CullMode {
		BACK(GL11.GL_BACK), FRONT(GL11.GL_FRONT);

		public final int id;

		private CullMode(int id) {
			this.id = id;
		}
	}

	public static enum Feature {
		BLEND(GL11.GL_BLEND), DEPTH_TEST(GL11.GL_DEPTH_TEST), CULL_FACES(GL11.GL_CULL_FACE),
		MULTISAMPLE(GL13.GL_MULTISAMPLE), SCISSORTEST(GL11.GL_SCISSOR_TEST);

		public final int id;

		private Feature(int id) {
			this.id = id;
		}
	}

	private static final Object DEPTH_MASK_KEY = GL11.GL_DEPTH_WRITEMASK;
	private static final Object CULL_FACE_KEY = Feature.CULL_FACES; 
	
	private static Map<Feature, Boolean> featureCache = new HashMap<>();
	private static Map<Object, Object> cache = new HashMap<>();
	private static BlendMode blendMode = null;

	public static boolean isFeatureEnabled(Feature f) {
		return featureCache.get(f) == null ? false : featureCache.get(f);
	}

	public static void setEnabled(Feature feature, boolean b) {
		Boolean cached = featureCache.get(feature);
		if (cached == null || (((boolean) cached) != b)) {
			if (b) {
				GL11.glEnable(feature.id);
				featureCache.put(feature, true);
			} else {
				GL11.glDisable(feature.id);
				featureCache.put(feature, false);
			}
		}
	}

	public static void setBlendMode(BlendMode mode) {
		if (blendMode != mode) {
			switch (mode) {
			case ADDITIVE:
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				break;
			case ALPHA:
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case MULTIPLICATIVE:
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
				break;
			default:
				throw new IllegalArgumentException("Illegal blend mode");
			}
			blendMode = mode;
		}
	}

	public static void setCullMode(CullMode mode) {
		Object o = cache.get(CULL_FACE_KEY);
		if (o == null || ((CullMode) o) != mode) {
			GL11.glCullFace(mode.id);
			cache.put(CULL_FACE_KEY, mode);
		}
	}

	public static void setScissor(int x, int y, int width, int height) {
		GL11.glScissor(x, y, width, height);
	}

	public static void setDepthMask(boolean b) {
		Object o = cache.get(DEPTH_MASK_KEY);
		if (o == null || ((boolean) o) != b) {
			GL11.glDepthMask(b);
			cache.put(DEPTH_MASK_KEY, b);
		}
	}

}
