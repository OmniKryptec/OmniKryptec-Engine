package omnikryptec.gui.rendering;

import omnikryptec.gameobject.Camera;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.gui.GuiContainer;

public class GuiRenderer {

	private SpriteBatch batch;
	private GuiContainer parent;
	
	public GuiRenderer() {
		batch = new SpriteBatch(new Camera().setOrthographicProjection2D(0, 0, 1, 1));
	}
	
	public void paint() {
		if(parent!=null) {
			batch.begin();
			parent.update(batch,true);
			batch.end();
		}
	}
	
	public void setGui(GuiContainer gui) {
		this.parent = gui;
	}
}
