package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;

public abstract class AbstractScene2D extends AbstractScene<GameObject2D>{
	
	protected AbstractScene2D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}

}
