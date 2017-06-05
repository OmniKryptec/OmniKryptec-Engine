package omnikryptec.model;

import omnikryptec.exceptions.OmniKryptecException;
import omnikryptec.logger.Logger;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.texture.ITexture;

public class Material {
	
	
    private static ITexture defaultNormalMap;
    
    public static final void setDefaultNormalMap(ITexture t) {
        if(t != null){
            defaultNormalMap = t;
        }
    }

    public static final ITexture getDefaultNormalMap() {
        return defaultNormalMap;
    }

    private float reflectivity;
    private float shinedamper;
    
    
    private ITexture normalmap;
    private ITexture specularmap;
    private ITexture extrainfo;
    private boolean hasTransparency = false;
    private IRenderer renderer = RendererRegistration.DEF_ENTITY_RENDERER;

    
    
    public Material(){
        this(0);
    }

    public Material(float reflec) {
        this(null, null, reflec);
    }

    public Material(ITexture normalmap, ITexture specularmap, float reflec) {
        this.reflectivity = reflec;
        if(normalmap != null) {
            this.normalmap = normalmap;
        } else {
            this.normalmap = getDefaultNormalMap();
        }
        this.specularmap = specularmap;
    }

    public final ITexture getNormalmap() {
    	return normalmap;
    }

    public final ITexture getSpecularmap() {
        return specularmap;
    }

    public final float getReflectivity() {
        return reflectivity;
    }


    public final boolean hasTransparency() {
        return hasTransparency;
    }

    /**
     * if no specularmap is present this will be used
     * @param reflectivity
     * @return
     */
    public final Material setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
        return this;
    }

    public final Material setNormalmap(ITexture normalmap) {
        if(normalmap != null) {
            this.normalmap = normalmap;
        } else {
            this.normalmap = getDefaultNormalMap();
        }
        return this;
    }

    public final Material setSpecularmap(ITexture specularmap) {
        this.specularmap = specularmap;
        return this;
    }
    
    public final Material setExtraInfoMap(ITexture info){
    	this.extrainfo = info;
    	return this;
    }
    
    public final ITexture getExtraInfo(){
    	return extrainfo;
    }
    
    
    public final Material setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
        return this;
    }

    public final IRenderer getRenderer() {
        return renderer;
    }
    
    public final Material setRenderer(IRenderer renderer) {
    	if(!RendererRegistration.exists(renderer)){
    		Logger.logErr("This renderer is not registered!", new OmniKryptecException("Renderer is not registered: "+renderer));
    	}
    	this.renderer = renderer;
        return this;
    }
    
    public Material setShineDamper(float sh){
    	shinedamper = sh;
    	return this;
    }
    
	public float getShineDamper() {
		return shinedamper;
	}
    
 
	
}
