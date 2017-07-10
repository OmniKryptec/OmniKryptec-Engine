package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;

public interface Renderer {

	long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities);
	
	float expensiveLevel();
	
	float priority();
	
}
