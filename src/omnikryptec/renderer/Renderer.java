package omnikryptec.renderer;

import java.util.List;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;

public interface Renderer {

	void render(Scene s, RenderMap<TexturedModel, List<Entity>> entities);

	void cleanup();

	float expensiveLevel();

}
