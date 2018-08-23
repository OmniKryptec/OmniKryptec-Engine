package de.omnikryptec.renderer.d3;

import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.base.ShaderPack;
import de.omnikryptec.util.FrustrumFilter;
import de.omnikryptec.util.Level;
import de.omnikryptec.util.Priority;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import java.util.List;

@Priority
@Level
public abstract class Renderer{
	
	
    private final float lvl;
    private final float prio;
    protected ShaderPack shaderpack;
	protected FrustrumFilter filter = new FrustrumFilter();
	protected boolean usesShader=true;
    
	/**
	 * Doesn't use any shaders ({@link #usesShader} is set to false)
	 */
	protected Renderer() {
		this(null);
		usesShader = false;
	}
	
    protected Renderer(ShaderPack myshader) {
        this.shaderpack = myshader;
        lvl = Util.extractLvl(getClass(), 0);
        prio = Util.extractPrio(getClass(), 0);
    }

    protected abstract long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter);

    private Shader tmps = null;
    private long tmplong;
    
    public final long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, RenderConfiguration config) {
    	cur = config;
    	if(usesShader) {
	        tmps = shaderpack.getShader(config.getShaderpackKey(), config.getShaderLvl());
	        if (tmps == null) {
	            if (Logger.isDebugMode()) {
	                Logger.log("Shader is null! (RenderPass \""+config.getShaderpackKey()+"\"", LogLevel.ERROR);
	            }
	            return 0;
	        }
	        tmps.start();
	        tmps.onRenderStart(s, config.getClipPlane());
    	}else {
    		tmps = null;
    	}
    	if (filter!=null) {
        	filter.setCamera(s.getCamera());
        }
        tmplong = render(s, entities, tmps, filter);
        if(usesShader) {
        	tmps.onRenderEnd(s);
        }
        cur = null;
        return tmplong;
    }
    private RenderConfiguration cur;
    
    protected final RenderConfiguration getCurrentRenderConfig() {
    	return cur;
    }
    
    public FrustrumFilter getFrustrumFilter(){
    	return filter;
    }
    
    public Renderer setFrustrumFilter(FrustrumFilter filter){
    	this.filter = filter;
    	return this;
    }
    
    public final float expensiveLevel() {
        return lvl;
    }

    public final float priority() {
        return prio;
    }

    
    public ShaderPack getShaderPack(){
    	return shaderpack;
    }

}
