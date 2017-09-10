package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene;
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
    
    protected Renderer(ShaderPack myshader) {
        this.shaderpack = myshader;
    }

    protected abstract long render(AbstractScene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter);

    private Shader tmps = null;
    private long tmplong;
    
    public final long render(AbstractScene s, RenderMap<AdvancedModel, List<Entity>> entities, String renderPassName) {
    	if(usesShader) {
	        tmps = shaderpack.getShader(renderPassName);
	        if (tmps == null) {
	            if (Logger.isDebugMode()) {
	                Logger.log("Shader is null! (RenderPass \""+renderPassName+"\"", LogLevel.ERROR);
	            }
	            return 0;
	        }
	        tmps.start();
	        tmps.onRenderStart(s);
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
        return tmplong;
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
