package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class Renderer{
	
	
    private float lvl = 0, prio = 0;
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

    public Renderer setExpensiveLevel(float f) {
        this.lvl = f;
        return this;
    }

    public Renderer setPriority(float f) {
        this.prio = f;
        return this;
    }
    
    public ShaderPack getShaderPack(){
    	return shaderpack;
    }

}
