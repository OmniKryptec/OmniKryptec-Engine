package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RenderConfiguration {
	
	public static enum AllowedRenderer {
        All, EvElse, OnlThis;
    }
	
	private boolean renderParticle = true;
	private float maxExpensive = Float.POSITIVE_INFINITY;
	private float minExpensive = Float.NEGATIVE_INFINITY;
	private ArrayList<Renderer<?>> renderer = new ArrayList<>();
	private AllowedRenderer allowedRenderer = AllowedRenderer.All;
	
	private LinkedList<Renderer<?>> rendererCache;
	
	public RenderConfiguration() {
		calcCache();
	}
	
	public RenderConfiguration setRenderParticles(boolean b) {
		this.renderParticle = b;
		return this;
	}
	
	public RenderConfiguration setExpensiveMinMax(float mine, float maxe) {
		maxExpensive = maxe;
		minExpensive = mine;
		calcCache();
		return this;
	}
	
	public RenderConfiguration setHighestExpensiveRenderer(float e) {
		maxExpensive = e;
		calcCache();
		return this;
	}
	
	public RenderConfiguration setSmallestExpensiveRenderer(float e) {
		minExpensive = e;
		calcCache();
		return this;
	}
	
	public RenderConfiguration setRendererData(AllowedRenderer option, Renderer<?>...renderers) {
		renderer.clear();
		for(Renderer<?> r : renderers) {
			renderer.add(r);
		}
		this.allowedRenderer = option;
		calcCache();
		return this;
	}
	
	public LinkedList<Renderer<?>> getRenderer(){
		return rendererCache;
	}
	
	public boolean renderParticles() {
		return renderParticle;
	}
	
	private void calcCache() {
		List<Renderer<?>> all;
		if(allowedRenderer == AllowedRenderer.OnlThis) {
			all = renderer;
		}else if(allowedRenderer == AllowedRenderer.EvElse) {
			List<Renderer<?>> tmpall = RendererRegistration.getAllRenderer();
			all = new ArrayList<>(); 
			for(Renderer<?> r : tmpall) {
				if(!renderer.contains(r)) {
					all.add(r);
				}
			}
		}else {
			all = RendererRegistration.getAllRenderer();
		}
		LinkedList<Renderer<?>> tmp = new LinkedList<>();
		for(Renderer<?> r : all) {
			if(r.expensiveLevel()<=maxExpensive&&r.expensiveLevel()>=minExpensive) {
				tmp.add(r);
			}
		}
		rendererCache = tmp;
	}
}
