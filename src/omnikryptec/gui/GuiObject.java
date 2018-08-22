package omnikryptec.gui;

import omnikryptec.graphics.SpriteBatch;

public abstract class GuiObject {

	private boolean enabled=true;
	
	public void paint(SpriteBatch batch) {
		if(isEnabled()) {
			draw(batch);
		}
	}
	
	protected abstract void draw(SpriteBatch batch);
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
