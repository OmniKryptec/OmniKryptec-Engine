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

}
