package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.renderer.d3.RenderConfiguration;
import omnikryptec.util.EnumCollection.FrameState;

public abstract class AbstractScene2D extends AbstractScene<GameObject2D>{
	
	protected AbstractScene2D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}
	
	public final void publicLogic() {
		state = FrameState.LOGIC;
		tmptime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
		if (isUsingPhysics()) {
			physicsworld.stepSimulation();
		}
		logic();
		camera.doLogic();
		logictime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime() - tmptime;
		state = FrameState.NULL;
	}
	
	final long mainPassRender() {
		return 0;
	}

	public final void publicRender(RenderConfiguration config) {
		
	}
	
}
