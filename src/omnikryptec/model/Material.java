package omnikryptec.model;

import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RendererRegistration;
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
	
	private ITexture normalmap;
	private ITexture specularmap;
	private boolean hasTransparency=false;
	private IRenderer renderer = RendererRegistration.DEF_ENTITY_RENDERER;
	
	public Material(){
		this(0);
	}
	
	public Material(float reflec){
		this(null, null, reflec);
	}
	
	public Material(Texture normalmap, Texture specularmap, float reflec){
		this.reflectivity = reflec;
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
	
	
	public boolean hasTransparency(){
		return hasTransparency;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
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