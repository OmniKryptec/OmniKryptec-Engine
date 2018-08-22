package omnikryptec.gui.rendering;

import omnikryptec.gameobject.Camera;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.gui.GuiObject;

public class GuiRenderer {

	private SpriteBatch batch;
	private GuiObject parent;
	
	public GuiRenderer() {
		batch = new SpriteBatch(new Camera().setOrthographicProjection2D(0, 0, 1, 1));
	}
	
	public void paint() {
		if(parent!=null) {
			batch.begin();
			parent.paint(batch);
			batch.end();
		}
	}
	
	public void setGui(GuiObject gui) {
		this.parent = gui;
	}
}
