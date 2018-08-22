package omnikryptec.gui;

import java.util.ArrayList;
import java.util.List;

import omnikryptec.graphics.SpriteBatch;

public class GuiContainer extends GuiObject{

	private List<GuiObject> objs = new ArrayList<>();
	
	@Override
	public void draw(SpriteBatch batch) {
		for(GuiObject g : objs) {
			g.draw(batch);
		}
	}
	
	
	
}
