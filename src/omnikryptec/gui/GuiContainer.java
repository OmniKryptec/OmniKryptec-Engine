package omnikryptec.gui;

import java.util.ArrayList;
import java.util.List;

import omnikryptec.graphics.SpriteBatch;

public abstract class GuiContainer {

	private boolean enabled=true;
	private List<GuiContainer> objs = new ArrayList<>();

	
	public void update(SpriteBatch batch, boolean checkMouse) {
		if(isEnabled()) {
			draw(batch);
			for(GuiContainer g : objs) {
				g.update(batch, checkMouse);
			}
		}
	}
	
	public void draw(SpriteBatch batch) {
		
	}
	
	public GuiContainer add(GuiContainer g) {
		objs.add(g);
		return this;
	}
	
	public GuiContainer setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
