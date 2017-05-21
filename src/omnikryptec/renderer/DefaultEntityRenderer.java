package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import omnikryptec.main.Scene;
import omnikryptec.storing.Entity;
import omnikryptec.storing.TexturedModel;

public class DefaultEntityRenderer implements IRenderer{

	public DefaultEntityRenderer() {
		RendererRegistration.register(this);
	}
	
	@Override
	public void start() {		
	}

	@Override
	public void render(Scene s, Map<TexturedModel, List<Entity>> entities) {
		
	}

	@Override
	public void end() {		
	}

	@Override
	public void cleanup() {		
	}

}
