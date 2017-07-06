package omnikryptec.test;

import java.util.Random;

import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GLFWInfo;
import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventSystem;
import omnikryptec.event.event.EventType;
import omnikryptec.event.event.IEventHandler;
import omnikryptec.event.input.InputManager;
import omnikryptec.gameobject.gameobject.Camera;
import omnikryptec.gameobject.gameobject.Entity;
import omnikryptec.gameobject.gameobject.GameObject;
import omnikryptec.gameobject.gameobject.Light;
import omnikryptec.gameobject.gameobject.RenderType;
import omnikryptec.gameobject.gameobject.UpdateType;
import omnikryptec.gameobject.particles.AttractedPaticleSystem;
import omnikryptec.gameobject.particles.ParticleAttractor;
import omnikryptec.gameobject.particles.AttractorMode;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.gameobject.particles.ParticleSpawnArea;
import omnikryptec.gameobject.particles.ParticleSpawnArea.ParticleSpawnAreaType;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.resource.objConverter.ModelData;
import omnikryptec.resource.objConverter.ObjLoader;
import omnikryptec.resource.texture.AtlasTexture;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.resource.texture.SimpleAnimation;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Color;
import omnikryptec.util.Instance;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.lang.LanguageManager;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.profiler.LiveProfiler;
import org.joml.Vector3f;

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
                    .setInitialFPSCap(-1).setChunkRenderOffsets(2, 2, 2).setLightForward(true).setUseRenderChunking(false).setUseFrustrumCulling(true),
                    new GLFWInfo(1280, 720));
            DisplayManager.instance().setSmoothedDeltatime(true);
            DisplayManager.instance().setSmoothedFrames(1000);
            new Thread(new Runnable() {
				
				@Override
				public void run() {
					LiveProfiler liveProfiler = new LiveProfiler(750, 750);
		            liveProfiler.startTimer(1000);					
				}
			}).start();
            
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
            EventSystem.instance().addEventHandler(new EngineTest2(), EventType.AFTER_FRAME, EventType.RENDER_FRAME_EVENT);

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
            Model BLOCK = new Model("", ObjLoader.loadOBJ(new AdvancedFile(res, "block.obj")));
            TexturedModel tm = new TexturedModel("brunnen", brunnen, brunnent);
            tm.getMaterial().setNormalmap(brunnen_norm).setSpecularmap(brunnen_specular);
            //tm.getMaterial().setNormalmap(jn).setSpecularmap(js);
            tm.getMaterial().setHasTransparency(true).setReflectivity(new Vector3f(10, 10, 10)).setShineDamper(100)
                    .setRenderer(RendererRegistration.DEF_ENTITY_RENDERER).setExtraInfoVec(new Vector3f(1, 1, 1));
            OmniKryptecEngine.instance().addAndSetScene(new Scene("test", (Camera) new Camera() {

                @Override
                public void update() {
                    // setRelativePos(getRelativePos().x, getRelativePos().y,
                    // getRelativePos().z + 0.1f *
                    // DisplayManager.instance().getDeltaTime());
                    doCameraLogic(this);
                }

            }.setPerspectiveProjection(90, 0.1f, 1000).setRelativePos(0, 0, 0)).setAmbientColor(0.5f, 0.5f, 0.5f));
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
            ptm.getMaterial().setHasTransparency(true).setRenderer(RendererRegistration.DEF_ENTITY_RENDERER);
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
            system = new AttractedPaticleSystem(0, 0, 0,
                    new ParticleAtlas(SimpleTexture.newTexture("/omnikryptec/test/cosmic.png"), 4, false),
                  250f,0, 1000f, 1f, RenderType.ALWAYS).setAverageMass(1E5F).setParticlesAttractingEachOther(true);

            System.out.println("Generating objs...");
            int cube = 100;
            int abstand = 10;
            float scale = 4;
            for (int x = -cube; x < cube; x += abstand) {
                for (int y = -cube; y < cube; y += abstand) {
                    for (int z = -cube; z < cube; z += abstand) {
                    	GameObject go;
                    	//go = new GameObject().setRelativePos(x, y, z);
                    	go = new Entity(tm).setScale(new Vector3f(scale, scale, scale)).setUpdateType(UpdateType.STATIC).setRelativePos(x, y, z);
                        Instance.getCurrentScene().addGameObject(go);
                        //system.addAttractor(new ParticleAttractor(go).setAcceleration(10).setMode(AttractorMode.KILL_ON_REACH).setTolerance(5));
                    }
                }
            }
            System.out.println("Done.");
            
           // Instance.getCurrentScene().addGameObject(new Entity(tm).setColor(0, 1, 0, 1).setScale(new Vector3f(scale,scale,scale)).setUpdateType(UpdateType.SEMISTATIC).setRelativePos(0, 0, 0));

            // ParticleSystem - unoptimisiert 70FPS - optimisiert 83 FPS
           
            //system.setParent(Instance.getCurrentCamera());
            system.setSpawnArea(new ParticleSpawnArea(ParticleSpawnAreaType.SHPERE, new Vector3f(0,1,0), 100));
            system.setTimeMultiplier(1F);
            //system.setAverageStartScale(0.01f);
            //system.setAverageEndScale(1);
            //system.setSystemLifeLength(20);
            //For AttractedPaticleSystem
            //system.addAttractor(0, 0, 0, 50F, 50F, AttractorMode.NOTHING, false);
            
                    //system.addAttractor(150,600,0, 35.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
        //system.addAttractor(-500,500,100, 40.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.getLastAddedAttractor().setAttenuation(0, 0.1f, 1); //Der nicht
        //system.addAttractor(-200,-150,0, 35.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
        
        
            system.addAttractor(150,600,0, 35.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.addAttractor(-500,500,100, 40.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.getLastAddedAttractor().setAttenuation(0, 0.1f, 1);
            system.addAttractor(-200,-150,0, 35.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.addAttractor(50,-200,-200, 35.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.getLastAddedAttractor().setAttenuation(0, 1, 1);
           // system.addAttractor(attractor=new ParticleAttractor(0,0,0).setAcceleration(11.0F).setTolerance(50F).setMode(AttractorMode.STOP_UNTIL_DISABLED_ON_REACH));
           // system.addAttractor(-250, 0, -250, 10.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.addAttractor(0, 0, 500, 10.0F, 50F, AttractorMode.STOP_FOREVER_ON_REACH);
            //system.addAttractor(attractor = new ParticleAttractor(system).setAcceleration(0).setTolerance(25).setMode(AttractorMode.NOTHING));
            //system.addAttractor(0, 300, 500, 200.0F, 100.0F, false);
            //system.addAttractor(-200, 300, 0, 75.0F, 50.0F, true);
            //system.addAttractor(200, 300, 0, 75.0F, 50.0F, true);
            
            system.setGlobal(true);
            system.setStartcolor(new Color(1, 0, 1));
            system.setEndcolor(new Color(1, 1, 0));
           // OmniKryptecEngine.instance().getCurrentScene().addGameObject(system);
            //ParticleMaster.instance().addParticle(new Particle(new ParticleTexture(SimpleTexture.newTexture("/omnikryptec/test/cosmic.png"), 4,true)));
            //OmniKryptecEngine.instance().getCurrentScene()
            //      .addGameObject(new Light().setAttenuation(0, 0.001f, 0).setCuttOffRange(50).setColor(3, 0, 0).setConeDegrees(35).setConeAttenuation(0.8f, 0.1f, 0).setConeDirection(0, -1, 0).setRelativePos(0,10, 0));
            OmniKryptecEngine.instance().getCurrentScene()
                    .addGameObject(new Light().setAttenuation(0, 0.001f, 0).setColor(1, 1, 1).setDirectional(true).setConeDegrees(5).setConeDirection(0, 1, 0).setRelativePos(0, 1, 0).setGlobal(true));

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

    private static float v = 100;
   private static ParticleAttractor attractor;
    
    private static void doCameraLogic(Camera camera) {
        // v += DisplayManager.instance().getDeltaTime()*30;
        InputManager.doFirstPersonController(camera, DisplayManager.instance().getSettings().getKeySettings(), v, v, 40, false);
        // camera.setPerspectiveProjection(Maths.alterFOV(10, 179f, v, 1000),
        // 1000, 0.001f);
        Logger.CONSOLE.setTitle(camera.toString());
    }

    private static AttractedPaticleSystem system; //For SimpleParticleSystem
    //private static AttractedPaticleSystem system; //For AttractedPaticleSystem
    private Random ra = new Random();
    private double d = 0;

    @Override
    public void onEvent(Event ev) {

        // system.generateParticles(1);
        if (ev.getType() == EventType.RENDER_FRAME_EVENT) {
        	if((Instance.getDisplayManager().getFramecount())%100==0){
        		//attractor.setEnabled(!attractor.isEnabled());
        	}
        	//attractor.setAcceleration((float) (100*Math.sin(DisplayManager.instance().getCurrentTime()*10)));
        	//system.setActive(ra.nextInt(100)<40);
        	//d += 0.025*system.getTimeMultiplier();
    		//system.setRelativePos((float)(50*Math.sin(d)), -25, (float) (50*Math.cos(d/2)));
            //Display.setTitle("FPS: " + DisplayManager.instance().getFPS()+" / SFPS: " + DisplayManager.instance().getSmoothedFPS()+" / Vertices: "+OmniKryptecEngine.instance().getModelVertsCount()+" / PPStages: "+PostProcessing.instance().getActiveStageCount()+ " / Renderer P.: "+ParticleMaster.instance().getRenderedParticlesCount()+"  (updated P.: "+ParticleMaster.instance().getUpdatedParticlesCount()+") ");
        }
        //System.out.println("(Rendertime: "+Instance.getEngine().getRenderTimeMS()+" Particletime: "+ParticleMaster.instance().getOverallParticleTimeMS()+" PPTime: "+PostProcessing.instance().getRenderTimeMS()+")/"+Instance.getEngine().getFrameTimeMS());
        if (ev.getType() == EventType.AFTER_FRAME) {
            //Logger.log(new Profiler().createTimesString(50, true, false));
        }        //Logger.log(new Profiler().createTimesString(50, true, false));

        //System.out.println(DisplayManager.instance().getFPS());
        // System.out.println(DisplayManager.instance().getDeltaTime());
    }

}
