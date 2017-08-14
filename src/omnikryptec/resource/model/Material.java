package omnikryptec.resource.model;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;

import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.texture.Texture;
import omnikryptec.util.exceptions.OmniKryptecException;
import omnikryptec.util.logger.Logger;

public class Material {
	
	public static final String DIFFUSE = "diffuse";
	public static final String SPECULAR = "specular";
	public static final String NORMAL = "normal";
	public static final String INFO = "info";

	public static final String REFLECTIVITY = "reflectivity";
	
	private Map<String, Texture> textures = new HashMap<>(); 
	private Map<String, Float> floats = new HashMap<>(); 
	private Map<String, Vector3f> vec4fs = new HashMap<>(); 

	private Renderer<?> renderer = RendererRegistration.SIMPLE_MESH_RENDERER;
	private boolean hasTransparency = false;

	
	public Material setTexture(String type, Texture t){
		textures.put(type, t);
		return this;
	}
	
	public Material setFloat(String type, float f){
		floats.put(type, f);
		return this;
	}
	
	public Material setVector3f(String type, Vector3f v){
		vec4fs.put(type, v);
		return this;
	}
	
	public Texture getTexture(String type){
		return textures.get(type);
	}
	
	public float getFloat(String type){
		return floats.get(type);
	}
	
	public Vector3f getVector3f(String type){
		return vec4fs.get(type);
	}
	
	public final boolean hasTransparency() {
		return hasTransparency;
	}

	public final Material setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
		return this;
	}

	public final Renderer<?> getRenderer() {
		return renderer;
	}
	
	public final Material setRenderer(Renderer<?> renderer) {
		if (!RendererRegistration.exists(renderer)) {
			Logger.logErr("This renderer is not registered!",
					new OmniKryptecException("Renderer is not registered: " + renderer));
		}
		this.renderer = renderer;
		return this;
	}
	
	
//********************************************************DEPRECATED*******************************************************************	
	private Vector4f mdata;
	private Texture normalmap;
	private Texture specularmap;
	private Texture extrainfo;
	private Vector3f extrainfovec;

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
		mdata.set(reflectivity, mdata.w);
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
