package omnikryptec.test;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.settings.GameSettings;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.event.IEventHandler;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.postprocessing.LightStage;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.Maths;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.RenderUtil;

import java.nio.charset.MalformedInputException;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author Panzer1119
 */
public class EngineTest2 implements IEventHandler{
    
	public static void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("Test 2", new GameSettings("EngineTest2", 1280, 720).setAnisotropicLevel(32).setMultisamples(32).setInitialFpsCap(DisplayManager.DISABLE_FPS_CAP));
           // PostProcessing.instance().addStage(new LightRenderer());
            EventSystem.instance().addEventHandler(new EngineTest2(), EventType.RENDER_EVENT);
            Model brunnen = new Model(ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
            //Model brunnen = Model.generateQuad();
            Texture brunnent = Texture.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
            TexturedModel tm = new TexturedModel(brunnen, brunnent);
            tm.getMaterial().setHasTransparency(true);
            OmniKryptecEngine.instance().addAndSetScene("test", new Scene(new Camera() {
     
                @Override
                public void doLogic(){
                    //setRelativePos(getRelativePos().x, getRelativePos().y, getRelativePos().z + 0.1f * DisplayManager.instance().getDeltaTime());
                    doCameraLogic(this);
                }
                
            }.setPerspectiveProjection(75, 1000, 0.1f)));
            Model pine = new Model(ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine.obj")));
			Texture pinet = Texture.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine2.png")).create();
			TexturedModel ptm = new TexturedModel(pine, pinet);
			ptm.getMaterial().setHasTransparency(true);
			Random r = new Random();
			for(int i=0; i<200; i++){
				Entity e = new Entity(ptm);
				e.setRelativePos(r.nextInt(100)-50, r.nextInt(100)-50, r.nextInt(100)-50);
				OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
			}
            Entity ent = new Entity(tm);
            Entity ent2 = new Entity(tm);
            Entity ent3 = new Entity(tm);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent2);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(ent3);
            //OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().y += 10;
           //OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativeRotation().z = 90;
            ent.setRelativePos(0, 0, 0);
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
            //camera.increaseRelativeRot(0, 3*deltaRot, 0);
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_W)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y, camera.getRelativePos().z - deltaPos);
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y, (float) ((camera.getRelativePos().z - deltaPos) * Math.sin(Math.toRadians(camera.getAbsoluteRotation().y))));
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_S)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y,              camera.getRelativePos().z + deltaPos);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_A)) {
                camera.setRelativePos(camera.getRelativePos().x - deltaPos, camera.getRelativePos().y,              camera.getRelativePos().z);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_D)) {
                camera.setRelativePos(camera.getRelativePos().x + deltaPos, camera.getRelativePos().y,              camera.getRelativePos().z);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y - deltaPos,   camera.getRelativePos().z);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_SPACE)) {
                camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y + deltaPos,   camera.getRelativePos().z);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_LEFT)) {
                camera.increaseRelativeRot(0, -deltaRot, 0);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_RIGHT)) {
                camera.increaseRelativeRot(0, deltaRot, 0);
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_UP)) {
                camera.getRelativeRotation().x -= deltaRot;
            }
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_DOWN)) {
                camera.getRelativeRotation().x += deltaRot;
            }
            //camera.moveSpace(forward, sideward, upward);
            Logger.CONSOLE.setTitle(camera.toString());
        }

		@Override
		public void onEvent(Event ev) {
			
			//System.out.println(DisplayManager.instance().getFPS());
			//System.out.println(DisplayManager.instance().getDeltaTime());
		}
    
}
