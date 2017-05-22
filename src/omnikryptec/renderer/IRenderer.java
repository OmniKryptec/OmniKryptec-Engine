package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import omnikryptec.main.Scene;
import omnikryptec.storing.Entity;
import omnikryptec.storing.TexturedModel;

public interface IRenderer {
	
	void render(Scene s, Map<TexturedModel, List<Entity>> entities);
	void cleanup();

}
