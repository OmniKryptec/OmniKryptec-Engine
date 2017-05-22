package omnikryptec.test;


import org.lwjgl.util.vector.Vector3f;

import omnikryptec.camera.Camera;
import omnikryptec.camera.MatrixMath;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GameSettings;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.main.Scene;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.storing.Entity;
import omnikryptec.storing.Model;
import omnikryptec.storing.TexturedModel;
import omnikryptec.texture.Texture;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.RenderUtil;

public class EngineTest{

	public static void main(String[] args) {
                Logger.showConsoleDirect();
		Logger.enableLoggerRedirection(true);
		Logger.setDebugMode(true);
                Logger.log("Loaded Natives: " + NativesLoader.loadNatives());
		
		DisplayManager.createDisplay("Test", new GameSettings("EngineTest", 1280, 720).setAnisotropicLevel(4));
		Model brunnen = new Model(ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
		//Model brunnen = Model.generateQuad();
		Texture brunnent = Texture.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
		TexturedModel tm = new TexturedModel(brunnen, brunnent);
		OmniKryptecEngine.instance().addAndSetScene("test", new Scene(new Camera(MatrixMath.perspectiveProjection(70, 1000, 0.1f))));
		Entity ent = new Entity(tm);
		Entity ent2 = new Entity(tm);
		Entity ent3 = new Entity(tm);
		OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent);
		OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent2);
		OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent3);
		OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().y += 3;
		OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativeRotation().x = 40;
		ent.setRelativePos(0, 0, -5);
		ent2.setRelativePos(5, 0, -5);
		ent3.setRelativePos(-5, 0, -5);
		//ent.setScale(new Vector3f(2, 2, 2));
		//RenderUtil.goWireframe(true);
		OmniKryptecEngine.instance().startLoop(ShutdownOption.JAVA);
	}
	
}
