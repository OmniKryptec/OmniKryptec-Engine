package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;

public interface Renderer {

	long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean onlyRender);

	void cleanup();

	float expensiveLevel();
	
	float priority();
	
}
