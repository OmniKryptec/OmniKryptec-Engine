package omnikryptec.model;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.exceptions.OmniKryptecException;
import omnikryptec.logger.Logger;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.texture.Texture;

public class Material {

	private Vector4f mdata;

	private Texture normalmap;
	private Texture specularmap;
	private Texture extrainfo;
	private Vector3f extrainfovec;
	private boolean hasTransparency = false;
	private Renderer renderer = RendererRegistration.DEF_ENTITY_RENDERER;

	public Material() {
		this(0, 1);
	}

	public Material(float reflec, float shinedamper) {
		this(null, null, reflec, shinedamper);
	}

	public Material(Texture normalmap, Texture specularmap, float reflec, float shinedamper) {
		this(normalmap, specularmap, new Vector3f(reflec, reflec, reflec), shinedamper);
	}

	public Material(Texture normalmap, Texture specularmap, Vector3f reflec, float shinedamper) {
		this(normalmap, specularmap, new Vector4f(reflec.x, reflec.y, reflec.z, shinedamper));
	}
	
	public Material(Texture normalmap, Texture specularmap, Vector4f mdata) {
		this.mdata = mdata;
		this.normalmap = normalmap;
		this.specularmap = specularmap;
	}
	
	public final Texture getNormalmap() {
		return normalmap;
	}

	public final Texture getSpecularmap() {
		return specularmap;
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
	public final Material setExtraInfoVec(Vector3f vec) {
		this.extrainfovec = vec;
		return this;
	}

	public final Vector3f getExtraInfoVec() {
		return extrainfovec;
	}
	
	public final Material setReflectivity(float f){
		return setReflectivity(new Vector3f(f, f, f));
	}
	
	public final Material setReflectivity(Vector3f reflectivity) {
		mdata.set(reflectivity.x, reflectivity.y, reflectivity.z);
		return this;
	}

	public final Material setNormalmap(Texture normalmap) {
		this.normalmap = normalmap;
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
		mdata.w = sh;
		return this;
	}

	public float getShineDamper() {
		return mdata.w;
	}
	
	public final Material setLightData(Vector4f mdata){
		this.mdata = mdata;
		return this;
	}
	
	public final Vector4f getMData(){
		return mdata;
	}

}
