package omnikryptec.renderer;

import omnikryptec.main.Scene;

public interface IRenderer {
	
	void render(Scene s, RenderMap entities);
	void cleanup();
	
	
}
