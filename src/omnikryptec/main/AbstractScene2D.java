package omnikryptec.main;

import omnikryptec.gameobject.Camera;

public class AbstractScene2D extends AbstractScene{
	
	protected AbstractScene2D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}
}
