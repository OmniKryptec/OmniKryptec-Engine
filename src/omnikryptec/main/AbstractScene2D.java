package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.GameObject3D;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class AbstractScene2D extends AbstractScene<GameObject2D> {

	protected static int cox,coy;

	static {
		cox = OmniKryptecEngine.instance().getGameSettings().getInteger(GameSettings.CHUNK_OFFSET_2D_X);
		coy = OmniKryptecEngine.instance().getGameSettings().getInteger(GameSettings.CHUNK_OFFSET_2D_Y);
	}
	
	protected AbstractScene2D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}

	@Override
	public final void addGameObject(GameObject2D go, boolean added) {
		super.addGameObject(go, added);
		if (go.hasChilds()) {
			for (GameObject2D g : go.getChilds()) {
				addGameObject(g, added);
			}
		}
	}

	@Override
	public final GameObject2D removeGameObject(GameObject2D go, boolean delete) {
		super.removeGameObject(go, delete);
		if (go.hasChilds()) {
			for (GameObject2D g : go.getChilds()) {
				removeGameObject(g, delete);
			}
		}
		return go;
	}

	
	public static int getCox() {
		return cox;
	}
	
	public static int getCoy() {
		return coy;
	}
}
