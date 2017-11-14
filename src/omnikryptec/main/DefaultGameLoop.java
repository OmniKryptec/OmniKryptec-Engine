package omnikryptec.main;

import omnikryptec.display.Display;

public class DefaultGameLoop extends GameLoop{

	@Override
	protected void runLoop() {
		while(!isStopRequested()) {
			renderOneFrame();
		}
	}

	@Override
	protected void renderOneFrame() {
		
	}

}
