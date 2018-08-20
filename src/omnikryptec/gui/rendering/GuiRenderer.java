package omnikryptec.gui.rendering;

import omnikryptec.gameobject.Camera;
import omnikryptec.graphics.SpriteBatch;

public class GuiRenderer {

	private SpriteBatch batch;
	
	public GuiRenderer() {
		batch = new SpriteBatch(new Camera().setDefaultScreenSpaceProjection());
	}
	
}
