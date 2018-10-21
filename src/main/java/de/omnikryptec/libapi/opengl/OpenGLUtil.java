package de.omnikryptec.libapi.opengl;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.omnikryptec.util.data.Color;

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

	public static enum PolyMode {
		FILL(GL11.GL_FILL), LINE(GL11.GL_LINE), POINT(GL11.GL_POINT);

		public final int id;

		private PolyMode(int id) {
			this.id = id;
		}
	}

	public static enum BufferType {
		COLOR(GL11.GL_COLOR_BUFFER_BIT), DEPTH(GL11.GL_DEPTH_BUFFER_BIT), @Deprecated
		ACCUM(GL11.GL_ACCUM_BUFFER_BIT), STENCIL(GL11.GL_STENCIL_BUFFER_BIT);

		public final int id;

		private BufferType(int id) {
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

	private static enum CACHE_ENUM {
		DEPTH_MASK_KEY, CULL_FACE_KEY, POLY_MODE_KEY
	}

	private static Map<Feature, Boolean> featureCache = new EnumMap<>(Feature.class);
	private static Map<CACHE_ENUM, Object> cache = new EnumMap<>(CACHE_ENUM.class);
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
		Object o = cache.get(CACHE_ENUM.CULL_FACE_KEY);
		if (o == null || ((CullMode) o) != mode) {
			GL11.glCullFace(mode.id);
			cache.put(CACHE_ENUM.CULL_FACE_KEY, mode);
		}
	}

	public static void setScissor(int x, int y, int width, int height) {
		GL11.glScissor(x, y, width, height);
	}

	public static void setDepthMask(boolean b) {
		Object o = cache.get(CACHE_ENUM.DEPTH_MASK_KEY);
		if (o == null || ((boolean) o) != b) {
			GL11.glDepthMask(b);
			cache.put(CACHE_ENUM.DEPTH_MASK_KEY, b);
		}
	}

	public static void setPolyMode(PolyMode mode) {
		Object o = cache.get(CACHE_ENUM.POLY_MODE_KEY);
		if (o == null || ((PolyMode) o) != mode) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, mode.id);
			cache.put(CACHE_ENUM.POLY_MODE_KEY, mode);
		}
	}

	public static void setClearColor(Color color) {
		setClearColor(color.getR(), color.getG(), color.getB(), color.getA());
	}

	public static void setClearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}

	public static void clear(BufferType... buffers) {
		int mask = 0;
		for (BufferType b : buffers) {
			mask |= b.id;
		}
		GL11.glClear(mask);
	}

}
