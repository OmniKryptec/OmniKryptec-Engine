package omnikryptec.storing;

import omnikryptec.renderer.IRenderer;
import omnikryptec.texture.ITexture;
import omnikryptec.texture.Texture;

public class Material {
	
	private static ITexture defaultNormalMap;
	
	public static void setDefaultNormalMap(ITexture t){
		if(t!=null){
			defaultNormalMap = t;
		}
	}
	
	public static ITexture getDefaultNormalMap(){
		return defaultNormalMap;
	}
	
	private float reflectivity;
	private float shineDamper;
	
	private ITexture normalmap;
	private ITexture specularmap;
	private boolean hasTransparency=false;
	private IRenderer renderer;
	
	public Material(){
		this(0, 10);
	}
	
	public Material(float reflec, float sd){
		this(null, null, reflec, sd);
	}
	
	public Material(Texture normalmap, Texture specularmap, float reflec, float shinedamp){
		this.reflectivity = reflec;
		this.shineDamper = shinedamp;
		if(normalmap!=null){
			this.normalmap = normalmap;
		}else{
			this.normalmap = getDefaultNormalMap();
		}
		this.specularmap = specularmap;
	}
	
	public ITexture getNormalmap(){
		return normalmap;
	}
	
	public ITexture getSpecularmap(){
		return specularmap;
	}
	
	public float getReflectivity(){
		return reflectivity;
	}
	
	public float getShineDamper(){
		return shineDamper;
	}
	
	public boolean hasTransparency(){
		return hasTransparency;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public void setNormalmap(Texture normalmap) {
		if(normalmap!=null){
			this.normalmap = normalmap;
		}else{
			this.normalmap = getDefaultNormalMap();
		}
	}

	public void setSpecularmap(Texture specularmap) {
		this.specularmap = specularmap;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}
	
	public IRenderer getRenderer(){
		return renderer;
	}
	
	
}
