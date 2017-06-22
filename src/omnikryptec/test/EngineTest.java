package omnikryptec.test;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.main.Scene;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.settings.GameSettings;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.NativesLoader;

public class EngineTest {

	public static void main(String[] args) {
		try {
			NativesLoader.loadNatives();
			Logger.enableLoggerRedirection(true);
			Logger.setDebugMode(true);
			Logger.CONSOLE.setExitWhenLastOne(true);
			Logger.showConsoleDirect();

			DisplayManager.createDisplay("Test",
					new GameSettings("EngineTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32));
			Model brunnen = new Model(
					ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
			// Model brunnen = Model.generateQuad();
			SimpleTexture brunnent = SimpleTexture
					.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
			TexturedModel tm = new TexturedModel(brunnen, brunnent);
			tm.getMaterial().setReflectivity(0.5f);
			OmniKryptecEngine.instance().addAndSetScene("test", new Scene(new Camera() {
				@Override
				public void doLogic() {
					// setRelativePos(getRelativePos().x, getRelativePos().y,
					// getRelativePos().z+1*DisplayManager.instance().getDeltaTime());
					// increaseRelativeRot(10*DisplayManager.instance().getDeltaTime(),
					// 0,0);

				}

			}.setPerspectiveProjection(75, 1000, 0.1f)));

			Entity ent = new Entity(tm);
			Entity ent2 = new Entity(tm);
			Entity ent3 = new Entity(tm);

			OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent);
			OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent2);
			OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent3);
			// OmniKryptecEngine.instance().getCurrentScene().getCamera().setRelativePos(0,
			// 100, -5);
			OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().y += 10;
			OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativeRotation().x = 90;
			ent.setRelativePos(0, 0, -5);
			ent2.setRelativePos(5, 0, -5);
			ent3.setRelativePos(-5, 0, 2);
			// ent.setScale(new Vector3f(1, 1, 1));
			OmniKryptecEngine.instance().startLoop(ShutdownOption.JAVA);
			NativesLoader.unloadNatives();
		} catch (Exception ex) {
			Logger.logErr("Error: " + ex, ex);
		}
	}

}
