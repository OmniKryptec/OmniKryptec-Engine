package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class Renderer<T extends Shader> {
	
	
    private float lvl = 0, prio = 0;
    protected ShaderPack<T> shaderpack;
	protected FrustrumFilter filter = new FrustrumFilter();

    
    protected Renderer(ShaderPack<T> myshader) {
        this.shaderpack = myshader;
    }

    protected abstract long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter);

    private Shader tmps = null;

    public final long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, String renderPassName) {
        tmps = shaderpack.getShader(renderPassName);
        if (tmps == null) {
            if (Logger.isDebugMode()) {
                Logger.log("Shader is null! (RenderPass \""+renderPassName+"\"", LogLevel.ERROR);
            }
            return 0;
        }

        if (filter!=null) {
        	filter.setCamera(s.getCamera());
        }
        tmps.start();
        tmps.onRenderStart(s);
        return render(s, entities, tmps, filter);
    }

    public FrustrumFilter getFrustrumFilter(){
    	return filter;
    }
    
    public Renderer<T> setFrustrumFilter(FrustrumFilter filter){
    	this.filter = filter;
    	return this;
    }
    
    public final float expensiveLevel() {
        return lvl;
    }

    public final float priority() {
        return prio;
    }

    public Renderer<T> setExpensiveLevel(float f) {
        this.lvl = f;
        return this;
    }

    public Renderer<T> setPriority(float f) {
        this.prio = f;
        return this;
    }
    
    public ShaderPack<T> getShaderPack(){
    	return shaderpack;
    }

}
