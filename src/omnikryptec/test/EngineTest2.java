package omnikryptec.test;

import java.util.Random;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.event.IEventHandler;
import omnikryptec.light.Light;
import omnikryptec.light.LightPrepare;
import omnikryptec.light.LightStage;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.main.Scene;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.settings.GameSettings;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;

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
            //Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("Test 2", new GameSettings("EngineTest2", 1280, 720).setAnisotropicLevel(32).setMultisamples(32).setInitialFpsCap(60).setChunkOffsets(10, 10, 10));
            PostProcessing.instance().addStage(new LightStage(LightPrepare.ATT_LIGHT_PREPARE));
            //PostProcessing.instance().addStage(new CompleteGaussianBlurStage(false,0.5f,0.5f));
            //PostProcessing.instance().addStage(new ColorSpaceStage(8,8,8));
            //PostProcessing.instance().addStage(new ContrastchangeStage(-0.25f));
            EventSystem.instance().addEventHandler(new EngineTest2(), EventType.RENDER_EVENT);
            Model brunnen = new Model(ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
            //Model brunnen = Model.generateQuad();
            Texture brunnent = Texture.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
            TexturedModel tm = new TexturedModel(brunnen, brunnent);
            tm.getMaterial().setHasTransparency(true);
            OmniKryptecEngine.instance().addAndSetScene("test", new Scene((Camera) new Camera() {
     
                @Override
                public void doLogic(){
                    //setRelativePos(getRelativePos().x, getRelativePos().y, getRelativePos().z + 0.1f * DisplayManager.instance().getDeltaTime());
                    doCameraLogic(this);
                }
                
            }.setPerspectiveProjection(90, 1000, 0.1f).setRelativePos(10, 10, 10)));
            Model pine = new Model(ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine.obj")));
			Texture pinet = Texture.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine2.png")).create();
			TexturedModel ptm = new TexturedModel(pine, pinet);
			ptm.getMaterial().setHasTransparency(true);
			ptm.getMaterial().setReflectivity(1);
			Random r = new Random();
			for(int i=0; i<200; i++){
				Entity e = new Entity(tm){
					@Override
	            	public void doLogic(){
	            		//setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), r.nextFloat());
						//InputUtil.doFirstPersonController(this, DisplayManager.instance().getSettings().getKeySettings(), 1, 1, 1);
						increaseRelativeRot(0, 1, 0);
	            	}
				};
				//e.setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
				e.setRelativePos(r.nextInt(100)-50, r.nextInt(100)-50, r.nextInt(100)-50);
				OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
			}
            //ent.setParent(OmniKryptecEngine.instance().getCurrentScene().getCamera());
            //OmniKryptecEngine.instance().getCurrentScene().addGameObject(new Light().setColor(1, 1, 0).setRadius(100));
            //OmniKryptecEngine.instance().getCurrentScene().addGameObject(new Light().setColor(1, 0, 1).setRadius(100).setRelativePos(50, 50, 50));
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(new Light().setColor(1, 1, 1).setRadius(100).setShader(LightPrepare.ATT_LIGHT_PREPARE).setAttenuation(1f, 0.01f, 0));
            //.instance().getCurrentScene().addGameObject(new Light().setColor(0, 0, 1).setRadius(100).setRelativePos(50, 50, 0));

            OmniKryptecEngine.instance().startLoop(ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Error: " + ex, ex);
        }
    }
        private static float v=20;
        private static void doCameraLogic(Camera camera) {
    		//v += DisplayManager.instance().getDeltaTime()*30;
            InputUtil.doFirstPersonController(camera, DisplayManager.instance().getSettings().getKeySettings(), v, v, 40);
            //camera.setPerspectiveProjection(Maths.alterFOV(10, 179f, v, 1000), 1000, 0.001f);
            Logger.CONSOLE.setTitle(camera.toString());
        }

		@Override
		public void onEvent(Event ev) {
			
			//System.out.println(DisplayManager.instance().getFPS());
			//System.out.println(DisplayManager.instance().getDeltaTime());
		}
    
}
