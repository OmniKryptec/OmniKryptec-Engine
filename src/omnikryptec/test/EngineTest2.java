package omnikryptec.test;

import omnikryptec.camera.Camera;
import omnikryptec.camera.MatrixMath;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GameSettings;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.storing.Entity;
import omnikryptec.storing.Model;
import omnikryptec.storing.TexturedModel;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author Panzer1119
 */
public class EngineTest2 {
    
	public static void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("Test 2", new GameSettings("EngineTest2", 1280, 720).setAnisotropicLevel(32).setMultisamples(32));
            Model brunnen = new Model(ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
            //Model brunnen = Model.generateQuad();
            Texture brunnent = Texture.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
            TexturedModel tm = new TexturedModel(brunnen, brunnent);
            OmniKryptecEngine.instance().addAndSetScene("test", new Scene(new Camera(MatrixMath.perspectiveProjection(75, 1000, 0.1f)) {
                
                @Override
                public void doLogic(){
                    //setRelativePos(getRelativePos().x, getRelativePos().y, getRelativePos().z + 0.1f * DisplayManager.instance().getDeltaTime());
                    doCameraLogic(this);
                }
                
            }));
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
            ent3.setRelativePos(-5, 0, 2);
            //ent.setScale(new Vector3f(1, 1, 1));
            OmniKryptecEngine.instance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Error: " + ex, ex);
        }
    }
        
        private static void doCameraLogic(Camera camera) {
            final float deltaPos = (0.4F * DisplayManager.instance().getDeltaTime());
            final float deltaRot = (10.0F * DisplayManager.instance().getDeltaTime());
            if(InputUtil.isKeyDown(Keyboard.KEY_W)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y, (float) ((camera.getRelativePos().z - deltaPos) * Math.sin(Math.toRadians(camera.getRelativeRotation().y))));
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_S)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y,              camera.getRelativePos().z + deltaPos);
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_A)) {
                camera.setRelativePos(camera.getRelativePos().x - deltaPos, camera.getRelativePos().y,              camera.getRelativePos().z);
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_D)) {
                camera.setRelativePos(camera.getRelativePos().x + deltaPos, camera.getRelativePos().y,              camera.getRelativePos().z);
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y - deltaPos,   camera.getRelativePos().z);
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y + deltaPos,   camera.getRelativePos().z);
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_LEFT)) {
                camera.getRelativeRotation().z += deltaRot;
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_RIGHT)) {
                camera.getRelativeRotation().z -= deltaRot;
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_UP)) {
                camera.getRelativeRotation().x -= deltaRot;
            }
            if(InputUtil.isKeyDown(Keyboard.KEY_DOWN)) {
                camera.getRelativeRotation().x += deltaRot;
            }
            Logger.CONSOLE.setTitle(camera.toString());
        }
    
}
