package omnikryptec.main;

import omnikryptec.gameobject.Camera;

public class Abstract2DEnv extends Environment{
	
	protected Abstract2DEnv(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}
}
