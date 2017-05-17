package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import omnikryptec.storing.Entity;
import omnikryptec.storing.TexturedModel;

public interface IRenderer {
	
	void start();
	void render(Map<TexturedModel, List<Entity>> entities);
	void end();
}
