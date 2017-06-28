package omnikryptec.test;

import java.util.Random;

import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GLFWInfo;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.Entity.RenderType;
import omnikryptec.entity.GameObject.UpdateType;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.event.IEventHandler;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.main.Scene;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.particles.ParticleMaster;
import omnikryptec.particles.ParticleSystem;
import omnikryptec.particles.ParticleTexture;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.settings.GameSettings;
import omnikryptec.texture.AtlasTexture;
import omnikryptec.texture.SimpleAnimation;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.AdvancedFile;
import omnikryptec.input.InputManager;
import omnikryptec.lang.LanguageManager;
import omnikryptec.util.Instance;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.profiler.LiveProfiler;
import omnikryptec.util.profiler.Profiler;

/**
 *
 * @author Panzer1119
 */
public class EngineTest2 implements IEventHandler {

    public static void main(String[] args) {
    	try {
        	NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.setMinimumLogLevel(LogLevel.FINEST);
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            LanguageManager.setLanguage("DE");

            DisplayManager.createDisplay("Test 2",
                    new GameSettings().setAnisotropicLevel(32).setMultisamples(32)
                    .setInitialFPSCap(30).setChunkRenderOffsets(2, 2, 2).setLightForward(true),
                    new GLFWInfo(4,3,false,false,1280, 720));
            DisplayManager.instance().setSmoothedDeltatime(true);
            DisplayManager.instance().setSmoothedFrames(1000);
            LiveProfiler liveProfiler = new LiveProfiler(750, 750);
            liveProfiler.startTimer(1000);
            //PostProcessing.instance().addStage(new
            // DeferredLightStage(DeferredLightPrepare.ATT_LIGHT_PREPARE,
            // DeferredLightPrepare.DEFAULT_LIGHT_PREPARE));
//             PostProcessing.instance().addStage(new BloomStage(new
//             CompleteGaussianBlurStage(true, 0.4f, 0.4f), new Vector4f(1, 0,
//             0, 0), new Vector2f(1, 6)));
//             PostProcessing.instance().addStage(new
//             FogStage().setDensity(0.25f));
//             PostProcessing.instance().addStage(new
//             CompleteGaussianBlurStage(false,0.1f,0.1f));
//             PostProcessing.instance().addStage(new ColorSpaceStage(16,4,4));
//             PostProcessing.instance().addStage(new
//             CompleteGaussianBlurStage(true,0.5f,0.5f));
//             PostProcessing.instance().addStage(new
//             ContrastchangeStage(0.75f));
//
//            // PostProcessing.instance().addStage(new BrightnessfilterStage(new
//            // Vector4f(0, 0, 0, 0)));
//            // RenderUtil.goWireframe(true);
//             PostProcessing.instance().setEnabled(false);
//             PostProcessing.instance().addStage(new
//             FogStage().setDensity(0.05f).setFog(0, 0.5f, 0,
//             0.8f).setGradient(2));
//             PostProcessing.instance().addStage(new
//             CompleteGaussianBlurStage(false, 0.6f, 0.6f));
//             PostProcessing.instance().addStage(new
//             CompleteGaussianBlurStage(false, 0.3f, 0.3f));
//             PostProcessing.instance().addStage(new
//             CompleteGaussianBlurStage(false, 0.1f, 0.1f));
//             PostProcessing.instance().addStage(new
//             CompleteGaussianBlurStage(false, 0.05f, 0.05f));
//            PostProcessing.instance().addStage(new DebugRenderer());
    		
    		AdvancedFile res = new AdvancedFile("res");
            SimpleTexture jd = SimpleTexture.newTexture(new AdvancedFile(res, "jd.png"));
            SimpleTexture js = SimpleTexture.newTexture(new AdvancedFile(res, "js.png"));
            SimpleTexture jn = SimpleTexture.newTexture(new AdvancedFile(res, "jn.png"));
            EventSystem.instance().addEventHandler(new EngineTest2(), EventType.AFTER_FRAME, EventType.RENDER_EVENT);
            

            Model brunnen = new Model("",
                    ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
            // Model brunnen = ModelUtil.generateQuad();
            SimpleTexture brunnent = SimpleTexture
                    .newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
            SimpleTexture brunnen_norm = SimpleTexture
                    .newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen_normal.png")).create();
            SimpleTexture brunnen_specular = SimpleTexture.newTexture("/omnikryptec/test/brunnen_specular.png");
            SimpleTexture baum = SimpleTexture.newTexture(new AdvancedFile(res, "final_tree_1.png"));
            Model baumM = Model.newModel(new AdvancedFile(res, "final_tree_1.obj"));
            AtlasTexture rmvp = new AtlasTexture(brunnent, 0.25f, 0.25f, 0.5f, 0.5f);
            TexturedModel tm = new TexturedModel("brunnen", baumM, baum);
            //tm.getMaterial().setNormalmap(brunnen_norm).setSpecularmap(brunnen_specular);
            //tm.getMaterial().setNormalmap(jn).setSpecularmap(js);
            tm.getMaterial().setHasTransparency(false).setReflectivity(new Vector3f(0, 1, 1)).setShineDamper(10)
                    .setRenderer(RendererRegistration.DEF_FORWARD_ENTITY_RENDERER).setExtraInfoVec(new Vector3f(1, 1, 1));
            OmniKryptecEngine.instance().addAndSetScene(new Scene("test", (Camera) new Camera() {

                @Override
                public void doLogic() {
                    // setRelativePos(getRelativePos().x, getRelativePos().y,
                    // getRelativePos().z + 0.1f *
                    // DisplayManager.instance().getDeltaTime());
                    doCameraLogic(this);
                }

            }.setPerspectiveProjection(90, 0.1f, 1000).setRelativePos(0, 0, 0)).setAmbientColor(0.3f, 0.3f, 0.3f));
            Model pine = new Model("",
                    ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine.obj")));
            Model bauer = new Model("",
                    ColladaLoader.loadColladaModel(new AdvancedFile("res", "model.dae"), 50).getMeshData());
            SimpleTexture bauert = SimpleTexture.newTexture("/res/diffuse.png");
            SimpleTexture pinet = SimpleTexture
                    .newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine2.png")).create();

            SimpleAnimation animation = new SimpleAnimation(2, brunnent, pinet);
            SimpleTexture pine_normal = SimpleTexture.newTexture("/omnikryptec/test/pine2_normal.png");
            
            
            TexturedModel ptm = new TexturedModel("pine", pine, pinet);
            ptm.getMaterial().setNormalmap(pine_normal);
            ptm.getMaterial().setHasTransparency(true).setRenderer(RendererRegistration.DEF_FORWARD_ENTITY_RENDERER);
            ptm.getMaterial().setReflectivity(0.1f).setShineDamper(10).setExtraInfoVec(new Vector3f(1, 1, 0));
            Random r = new Random();
//            for (int i = 0; i < 250; i++) {
//                Entity e = new Entity(tm) {
//                    @Override
//                    public void doLogic() {
//                        // setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(),
//                        // r.nextFloat());
//                        // InputManager.doFirstPersonController(this,
//                        // DisplayManager.instance().getSettings().getKeySettings(),
//                        // 1, 1, 1);
//                        // increaseRelativeRot(0, 1, 0);
//                    }
//                }.setScale(new Vector3f(3, 3, 3));
//                //e.setRelativePos(5, -10, 10);
//                // e.setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
//               e.setRelativePos(r.nextInt(100) - 50, r.nextInt(100) - 50, r.nextInt(100) - 50);
//                OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
//            }
            int cube=1000;
            int abstand=20;
            float scale=2;
            for(int x=-cube; x<cube; x+=abstand){
            	for(int y=-cube; y<cube; y+=abstand){
            		for(int z=-cube; z<cube; z+=abstand){
            			Instance.getCurrentScene().addGameObject(new Entity(tm).setScale(new Vector3f(scale,scale,scale)).setUpdateType(UpdateType.STATIC).setRelativePos(x, y, z));
            		}
                }
            }
			//Instance.getCurrentScene().addGameObject(new Entity(tm).setColor(0, 1, 0, 1).setUpdateType(UpdateType.STATIC).setScale(new Vector3f(scale,scale,scale)).setRelativePos(0, 10, 0));

            // ParticleSystem - unoptimisiert 70FPS - optimisiert 83 FPS
            system = new ParticleSystem(0, 0, 0,
                    new ParticleTexture(SimpleTexture.newTexture("/omnikryptec/test/cosmic.png"), 4, false), 20000f, 2.5f,
                    new Vector3f(0, 0, 0), 2f, 1.25f, RenderType.ALWAYS);
            // system.setTimemultiplier(10);
            //OmniKryptecEngine.instance().getCurrentScene().addGameObject(system);
            //OmniKryptecEngine.instance().getCurrentScene()
              //      .addGameObject(new Light().setAttenuation(0, 0.001f, 0).setCuttOffRange(50).setColor(3, 0, 0).setConeDegrees(35).setConeAttenuation(0.8f, 0.1f, 0).setConeDirection(0, -1, 0).setRelativePos(0,10, 0));
           // OmniKryptecEngine.instance().getCurrentScene()
            //.addGameObject(new Light().setAttenuation(0, 0.001f, 0).setColor(1, 1, 1).setDirectional(true).setConeDegrees(5).setConeDirection(0, 1, 0).setRelativePos(0,1, 0));

            // ent.setParent(OmniKryptecEngine.instance().getCurrentScene().getCamera());
            // OmniKryptecEngine.instance().getCurrentScene().addGameObject(new
            // Light().setColor(1, 1, 0).setRadius(100));
            // OmniKryptecEngine.instance().getCurrentScene().addGameObject(new
            // Light().setColor(1, 0, 1).setRadius(100).setRelativePos(50, 50,
            // 50));
            // OmniKryptecEngine.instance().getCurrentScene().addGameObject(new
            // Light().setColor(1, 1,
            // 1).setRadius(-1).setShader(LightPrepare.DEFAULT_LIGHT_PREPARE));
            // .instance().getCurrentScene().addGameObject(new
            // Light().setColor(0, 0, 1).setRadius(100).setRelativePos(50, 50,
            // 0));

            OmniKryptecEngine.instance().startLoop(ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Error: " + ex, ex);
        }
    }

    private static float v =30;

    private static void doCameraLogic(Camera camera) {
        // v += DisplayManager.instance().getDeltaTime()*30;
        InputManager.doFirstPersonController(camera, DisplayManager.instance().getSettings().getKeySettings(), v, v, 40, false);
        // camera.setPerspectiveProjection(Maths.alterFOV(10, 179f, v, 1000),
        // 1000, 0.001f);
        Logger.CONSOLE.setTitle(camera.toString());
    }

    private static ParticleSystem system;
        
    @Override
    public void onEvent(Event ev) {
    	
        // system.generateParticles(1);
        if(ev.getType() == EventType.RENDER_EVENT){
        	//Display.setTitle("FPS: " + DisplayManager.instance().getFPS()+" / SFPS: " + DisplayManager.instance().getSmoothedFPS()+" / Vertices: "+OmniKryptecEngine.instance().getModelVertsCount()+" / PPStages: "+PostProcessing.instance().getActiveStageCount()+ " / Renderer P.: "+ParticleMaster.instance().getRenderedParticlesCount()+"  (updated P.: "+ParticleMaster.instance().getUpdatedParticlesCount()+") ");
        }
        //System.out.println("(Rendertime: "+Instance.getEngine().getRenderTimeMS()+" Particletime: "+ParticleMaster.instance().getOverallParticleTimeMS()+" PPTime: "+PostProcessing.instance().getRenderTimeMS()+")/"+Instance.getEngine().getFrameTimeMS());
        if(ev.getType() == EventType.AFTER_FRAME){
        	Logger.log(new Profiler().createTimesString(50, true, false));
        }
        //Logger.log(new Profiler().createTimesString(50, true, false));
        //System.out.println(DisplayManager.instance().getFPS());
        // System.out.println(DisplayManager.instance().getDeltaTime());
    }
    
    
}
