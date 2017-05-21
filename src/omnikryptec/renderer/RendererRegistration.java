package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.List;

public class RendererRegistration {
	
	private static final List<IRenderer> existingRenderers = new ArrayList<>();
	
	
	public static boolean exists(IRenderer r){
		return existingRenderers.contains(r);
	}
	
	public static void register(IRenderer r){
		existingRenderers.add(r);
	}
	
	public static void cleanup(){
		for(int i=0; i<existingRenderers.size(); i++){
			existingRenderers.get(i).cleanup();
		}
		existingRenderers.clear();
	}
}
