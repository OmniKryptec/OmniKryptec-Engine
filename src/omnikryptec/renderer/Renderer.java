package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class Renderer<T extends Shader> {

	private float lvl=0,prio=0;
	protected T shader;
	protected boolean setFrustrumFilter = true;
	
	
	protected Renderer(T myshader){
		this.shader = myshader;
	}
	
	protected abstract long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean ownshader);
	
	public final long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities){
		return render(s, entities, null);
	}
	
	private boolean tmp=false;
	public final long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader shader){
		if(tmp=(shader==null)){
			shader = this.shader;
			if(shader==null){
				if(Logger.isDebugMode()){
					Logger.log("Shader is null!", LogLevel.ERROR);
				}
				return 0;
			}
		}
        if(setFrustrumFilter){
        	FrustrumFilter.setProjViewMatrices(s.getCamera().getProjectionViewMatrix());
        }
		shader.start();
		shader.onRenderStart(s);
		return render(s, entities, tmp);
	}
	
	public final float expensiveLevel(){
		return lvl;
	}
	
	public final float priority(){
		return prio;
	}
	
	public Renderer<T> setExpensiveLevel(float f){
		this.lvl = f;
		return this;
	}
	
	public Renderer<T> setPriority(float f){
		this.prio = f;
		return this;
	}
	
}
