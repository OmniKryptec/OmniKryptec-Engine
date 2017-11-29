package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.renderer.RenderConfiguration;

public class Scene2D extends AbstractScene2D{

	public Scene2D() {
		this("", null);
	}
	
	public Scene2D(String name, Camera cam) {
		super(name, cam);
	}

	@Override
	protected void logic() {
		
	}

	@Override
	protected long render(RenderConfiguration config) {
		return 0;
	}

	@Override
	public boolean addGameObject(GameObject2D go) {
		return false;
	}

	@Override
	public GameObject2D removeGameObject(GameObject2D go, boolean delete) {
		return null;
	}

}
