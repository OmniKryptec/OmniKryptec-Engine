package omnikryptec.renderer;

import java.util.List;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;

public interface Renderer {

	void render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities);

	void cleanup();

	float expensiveLevel();

}
