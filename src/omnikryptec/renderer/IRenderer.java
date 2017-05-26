package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;

public interface IRenderer {
	
	void render(Scene s, Map<TexturedModel, List<Entity>> entities);
	void cleanup();

}
