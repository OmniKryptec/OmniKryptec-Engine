package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.Entity.RenderType;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;

public interface IRenderer {
	
	void render(Scene s, Map<TexturedModel, List<Entity>> entities);
	void cleanup();
	
	
}
