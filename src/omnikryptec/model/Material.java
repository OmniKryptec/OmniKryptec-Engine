package omnikryptec.model;

import org.lwjgl.util.vector.Vector4f;

import omnikryptec.exceptions.OmniKryptecException;
import omnikryptec.logger.Logger;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.texture.Texture;

public class Material {

	private static Texture defaultNormalMap;

	public static final void setDefaultNormalMap(Texture t) {
		if (t != null) {
			defaultNormalMap = t;
		}
	}

	public static final Texture getDefaultNormalMap() {
		return defaultNormalMap;
	}

	private float reflectivity;
	private float shinedamper;

	private Texture normalmap;
	private Texture specularmap;
	private Texture extrainfo;
	private Vector4f extrainfovec;
	private boolean hasTransparency = false;
	private Renderer renderer = RendererRegistration.DEF_ENTITY_RENDERER;

	public Material() {
		this(0);
	}

	public Material(float reflec) {
		this(null, null, reflec);
	}

	public Material(Texture normalmap, Texture specularmap, float reflec) {
		this.reflectivity = reflec;
		if (normalmap != null) {
			this.normalmap = normalmap;
		} else {
			this.normalmap = getDefaultNormalMap();
		}
		this.specularmap = specularmap;
	}

	public final Texture getNormalmap() {
		return normalmap;
	}

	public final Texture getSpecularmap() {
		return specularmap;
	}

	public final float getReflectivity() {
		return reflectivity;
	}

	public final boolean hasTransparency() {
		return hasTransparency;
	}

	/**
	 * setting an extrainfomap overrides this settings
	 * 
	 * @param vec
	 * @return
	 */
	public final Material setExtraInfoVec(Vector4f vec) {
		this.extrainfovec = vec;
		return this;
	}

	public final Vector4f getExtraInfoVec() {
		return extrainfovec;
	}

	/**
	 * if no specularmap is present this will be used
	 * 
	 * @param reflectivity
	 * @return
	 */
	public final Material setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
		return this;
	}

	public final Material setNormalmap(Texture normalmap) {
		if (normalmap != null) {
			this.normalmap = normalmap;
		} else {
			this.normalmap = getDefaultNormalMap();
		}
		return this;
	}

	public final Material setSpecularmap(Texture specularmap) {
		this.specularmap = specularmap;
		return this;
	}

	public final Material setExtraInfoMap(Texture info) {
		this.extrainfo = info;
		return this;
	}

	public final Texture getExtraInfo() {
		return extrainfo;
	}

	public final Material setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
		return this;
	}

	public final Renderer getRenderer() {
		return renderer;
	}

	public final Material setRenderer(Renderer renderer) {
		if (!RendererRegistration.exists(renderer)) {
			Logger.logErr("This renderer is not registered!",
					new OmniKryptecException("Renderer is not registered: " + renderer));
		}
		this.renderer = renderer;
		return this;
	}

	public Material setShineDamper(float sh) {
		shinedamper = sh;
		return this;
	}

	public float getShineDamper() {
		return shinedamper;
	}

}
