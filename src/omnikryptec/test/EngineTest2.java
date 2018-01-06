package omnikryptec.test;

import java.util.Random;

import org.joml.Vector3f;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.lang.LanguageManager;
import omnikryptec.collision.d2.Rectangle;
import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GLFWInfo;
import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventType;
import omnikryptec.event.event.IEventHandler;
import omnikryptec.event.input.InputManager;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Entity;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.GameObject3D;
import omnikryptec.gameobject.Light2D;
import omnikryptec.gameobject.Light3D;
import omnikryptec.gameobject.Sprite;
import omnikryptec.gameobject.particles.AttractedPaticleSystem;
import omnikryptec.gameobject.particles.AttractorMode;
import omnikryptec.gameobject.particles.ParticleAttractor;
import omnikryptec.gameobject.particles.ParticleSpawnArea;
import omnikryptec.gameobject.particles.ParticleSpawnArea.ParticleSpawnAreaType;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene2D;
import omnikryptec.main.Scene3D;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.renderer.d3.FloorReflectionRenderer;
import omnikryptec.renderer.d3.RenderConfiguration;
import omnikryptec.renderer.d3.RenderConfiguration.AllowedRenderer;
import omnikryptec.renderer.d3.RendererRegistration;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Material;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.resource.texture.AtlasTexture;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.resource.texture.SimpleAnimation;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.EnumCollection.DepthbufferType;
import omnikryptec.util.EnumCollection.RenderType;
import omnikryptec.util.EnumCollection.UpdateType;
import omnikryptec.util.Instance;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class EngineTest2 implements IEventHandler {

    static TestRenderer rend;
    static AdvancedModel testdings;
    static FloorReflectionRenderer testrend;

    public static void main(String[] args) {
        try {

            // int abc = 1000000;
            // long time = System.currentTimeMillis();
            // for(int i=0; i<abc; i++) {
            // org.joml.Math.sin(43);
            // }
            // long time2 = System.currentTimeMillis();
            // System.out.println((time2-time)*1000000/(double)abc);
            // System.out.println((int) (Math.ceil(size/10.0)*10));
            NativesLoader.loadNatives(null, new AdvancedFile(false, false, "H:/natives/"));
            // NativesLoader.loadNatives((folder) -> Logger.log(String.format("Loaded
            // natives from normal \"%s\"", folder), LogLevel.FINE), (throwable) -> {
            // NativesLoader.setNativesFolder(new AdvancedFile(false, (Object) null,
            // "H:/natives/"));
            // NativesLoader.loadNatives((folder) -> Logger.log(String.format("Loaded
            // natives from school \"%s\"", folder), LogLevel.FINE));
            // });
            //OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.showConsoleDirect();
            Logger.setMinimumLogLevel(LogLevel.FINEST);

            LanguageManager.setLanguage("DE");

            DisplayManager.createDisplay("Test 2",
                    new GameSettings().setAnisotropicLevel(16).setMultisamples(16).setChunkRenderOffsets(2, 2, 2)
                    .setLightForward(true).setUseRenderChunking(false).setUseFrustrumCulling(true)
                    .setInteger(GameSettings.HIGHEST_SHADER_LVL, 1000000)
                    .setBoolean(GameSettings.LIGHT_2D, false),
                    new GLFWInfo(3, 2, true, false, 1280, 720));
            Display.setAspectRatio(4 / 3.0);
            // new Thread(new Runnable() {
            //
            // @Override
            // public void run() {
            // LiveProfiler liveProfiler = new LiveProfiler(750, 750);
            // liveProfiler.startTimer(1000);
            // }
            // }).start();
            // rend = new TestRenderer();
            // PostProcessing.instance().addStage(new
            // DeferredLightStage(DeferredLightPrepare.ATT_LIGHT_PREPARE,
            // DeferredLightPrepare.DEFAULT_LIGHT_PREPARE));
            // PostProcessing.instance().addStage(new BloomStage(new
            // CompleteGaussianBlurStage(true, 0.4f, 0.4f), new Vector4f(1, 0,
            // 0, 0), new Vector2f(1, 6)));
            //OmniKryptecEngine.instance().getPostprocessor().addStage(new FogStage().setDensity(0.25f));
            //OmniKryptecEngine.instance().getPostprocessor().addStage(new
            //CompleteGaussianBlurStage(false,0.1f,0.1f));
            // PostProcessing.instance().addStage(new ColorSpaceStage(2,2,2));
            // PostProcessing.instance().addStage(new
            // CompleteGaussianBlurStage(true,0.5f,0.5f));
            // PostProcessing.instance().addStage(new
            // ContrastchangeStage(0.75f));
            //
            // // PostProcessing.instance().addStage(new BrightnessfilterStage(new
            // // Vector4f(0, 0, 0, 0)));
            // RenderUtil.goWireframe(true);
            // PostProcessing.instance().setEnabled(false);
            // PostProcessing.instance().addStage(new
            // FogStage().setDensity(0.05f).setFog(0, 0.5f, 0,
            // 0.8f).setGradient(2));
            // PostProcessing.instance().addStage(new
            // CompleteGaussianBlurStage(false, 0.6f, 0.6f));
            // PostProcessing.instance().addStage(new
            // CompleteGaussianBlurStage(false, 0.3f, 0.3f));
            // PostProcessing.instance().addStage(new
            // CompleteGaussianBlurStage(false, 0.1f, 0.1f));
            // PostProcessing.instance().addStage(new
            // CompleteGaussianBlurStage(false, 0.05f, 0.05f));
            // PostProcessing.instance().addStage(new DebugRenderer());

            final AdvancedFile res = new AdvancedFile(true, "", "res");
            final AdvancedFile test = new AdvancedFile(true, "", "omnikryptec", "test");
            //System.out.println(res);
            ResourceLoader.createInstanceDefault(true, false);
            //ResourceLoader.currentInstance().stageAdvancedFiles(1, ResourceLoader.LOAD_XML_INFO, res);
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "jd.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, ResourceLoader.LOAD_XML_INFO, new AdvancedFile(true, res, "js.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "jn.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen.obj"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen_normal.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen_specular.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "final_tree_3.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "final_tree_3.obj"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "block.obj"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "diffuse.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "pine.obj"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "pine2.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "pine2_normal.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "cosmic.png"));
            ResourceLoader.currentInstance().loadStagedAdvancedFiles(true);
            //System.out.println("Resources loaded: " + ResourceLoader.currentInstance().getLoadedData());
            SimpleTexture jd = ResourceLoader.currentInstance().getTexture("res:jd.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "jd.png"))*/;
            SimpleTexture js = ResourceLoader.currentInstance().getTexture("res:js.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "js.png"))*/;
            SimpleTexture jn = ResourceLoader.currentInstance().getTexture("res:jn.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "jn.png"))*/;
            OmniKryptecEngine.instance().getEventsystem().addEventHandler(new EngineTest2(), EventType.AFTER_FRAME, EventType.RENDER_FRAME_EVENT);
            Model brunnen = ResourceLoader.currentInstance().getResource(Model.class, "res:brunnen.obj")/*new Model("", ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")))*/;
            // Model brunnen = ModelUtil.generateQuad();
            SimpleTexture brunnent = ResourceLoader.currentInstance().getTexture("omnikryptec:test:brunnen.png")/*SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create()*/;
            SimpleTexture brunnen_norm = ResourceLoader.currentInstance().getTexture("omnikryptec:test:brunnen_normal.png")/*SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen_normal.png")).create()*/;
            SimpleTexture brunnen_specular = ResourceLoader.currentInstance().getTexture("omnikryptec:test:brunnen_specular.png")/*SimpleTexture.newTexture("/omnikryptec/test/brunnen_specular.png")*/;
            SimpleTexture baum = ResourceLoader.currentInstance().getTexture("res:final_tree_3.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "final_tree_3.png"))*/;
            //System.out.println(new AdvancedFile(true, res, "final_tree_3.obj"));
            Model baumM = ResourceLoader.currentInstance().getResource(Model.class, "res:final_tree_3.obj")/*Model.newModel(new AdvancedFile(true, res, "final_tree_3.obj"))*/;
            // Model baumM = Model.newModel(new AdvancedFile(res, "cube.obj"));
            AtlasTexture rmvp = new AtlasTexture(brunnent, 0.25f, 0.25f, 0.5f, 0.5f);
            Model BLOCK = ResourceLoader.currentInstance().getResource(Model.class, "res:block.obj")/*new Model("", ObjLoader.loadOBJ(new AdvancedFile(true, res, "block.obj")))*/;
            TexturedModel tm = new TexturedModel("brunnen", baumM, baum);
            tm.getMaterial().setRenderer(RendererRegistration.FORWARD_MESH_RENDERER);
            testdings = tm;
            // tm.getMaterial().setTexture(Material.NORMAL,
            // brunnen_norm).setTexture(Material.SPECULAR, brunnen_specular);
            // tm.getMaterial().setNormalmap(jn).setSpecularmap(js);
            // tm.getMaterial().setTexture(Material.SPECULAR, );
            tm.getMaterial().setHasTransparency(false).setVector3f(Material.REFLECTIVITY, new Vector3f(0.6f))
                    .setFloat(Material.DAMPER, 1.01f).setVector3f(Material.SHADERINFO, new Vector3f(1));
            OmniKryptecEngine.instance().addAndSetScene(new Scene3D("test", (Camera) new Camera() {

                @Override
                public void update() {
                    // setRelativePos(getRelativePos().x, getRelativePos().y,
                    // getRelativePos().z + 0.1f *
                    // DisplayManager.instance().getDeltaTime());
                    doCameraLogic(this);
                }

            }.setPerspectiveProjection(90, 0.1f, 1000)).setAmbientColor(0.1f, 0.1f, 0.1f));
            OmniKryptecEngine.instance().addAndSetScene(new Scene2D("test2d", new Camera().setOrthographicProjection2D(0, 0, 2000, 2000)).setAmbientColor(1, 1, 1));
            Instance.getCurrent3DCamera().getTransform().setPosition(0, 0, 200);
            // OmniKryptecEngine.instance().addAndSetScene(null);
            // Instance.getCurrentCamera().getTransform().setPosition(0, 0, 0);
            Model pine = ResourceLoader.currentInstance().getResource(Model.class, "omnikryptec:test:pine.obj")/*new Model("", ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine.obj")))*/;
            //Model bauer = new Model("", ColladaLoader.loadColladaModel(new AdvancedFile(true, "res", "model.dae"), 50).getMeshData());
            SimpleTexture bauert = ResourceLoader.currentInstance().getTexture("res:diffuse.png")/*SimpleTexture.newTexture("/res/diffuse.png")*/;
            SimpleTexture pinet = ResourceLoader.currentInstance().getTexture("omnikryptec:test:pine2.png")/*SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine2.png")).create()*/;

            SimpleAnimation animation = new SimpleAnimation(1, brunnent, pinet);
            SimpleTexture pine_normal = ResourceLoader.currentInstance().getTexture("omnikryptec:test:pine2_normal.png")/*SimpleTexture.newTexture("/omnikryptec/test/pine2_normal.png")*/;

            TexturedModel ptm = new TexturedModel("pine", pine, pinet);
            ptm.getMaterial().setTexture(Material.NORMAL, pine_normal);
            ptm.getMaterial().setHasTransparency(true).setRenderer(RendererRegistration.FORWARD_MESH_RENDERER);
            ptm.getMaterial().setVector3f(Material.REFLECTIVITY, new Vector3f(1f)).setFloat(Material.DAMPER, 10)
                    .setVector3f(Material.SHADERINFO, new Vector3f(1, 1, 0));

            Random r = new Random();
            // for (int i = 0; i < 250; i++) {
            // Entity e = new Entity(tm) {
            // @Override
            // public void doLogic() {
            // // setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(),
            // // r.nextFloat());
            // // InputManager.doFirstPersonController(this,
            // // DisplayManager.instance().getSettings().getKeySettings(),
            // // 1, 1, 1);
            // // increaseRelativeRot(0, 1, 0);
            // }
            // }.setScale(new Vector3f(3, 3, 3));
            // //e.setRelativePos(5, -10, 10);
            // // e.setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
            // e.setRelativePos(r.nextInt(100) - 50, r.nextInt(100) - 50, r.nextInt(100) -
            // 50);
            // OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
            // }
            System.out.println("Generating objs...");
            int cube = 20;
            int abstand = 5;
            float scale = 1;
            int objcount = 0;
            for (int x = -cube; x < cube; x += abstand) {
                for (int y = -cube; y < cube; y += abstand) {
                    for (int z = -cube; z < cube; z += abstand) {
                        GameObject3D go;
                        // go = new GameObject().setRelativePos(x, y, z);
                        go = (GameObject3D) new Entity(tm).setUpdateType(UpdateType.STATIC);
                        go.getTransform().setDirty().setScale(scale).setPosition(x, y, z).getRotationSimple().rotate(0,
                                0, 0);
                        Instance.getCurrent3DScene().addGameObject(go);
                        // system.addAttractor(new
                        // ParticleAttractor(go).setAcceleration(10).setMode(AttractorMode.KILL_ON_REACH).setTolerance(5));
                        objcount++;
                    }
                }
            }
            GameObject3D go;
            // go = new GameObject().setRelativePos(x, y, z);
            go = (GameObject3D) new Entity(tm).setColor(1, 0, 0, 1).setUpdateType(UpdateType.STATIC);
            go.getTransform().setDirty().setScale(scale).setPosition(100, -100, 0).getRotationSimple().rotate(0,
                    0, 0);
            Instance.getCurrent3DScene().addGameObject(go);
            // system.addAttractor(new
            // ParticleAttractor(go).setAcceleration(10).setMode(AttractorMode.KILL_ON_REACH).setTolerance(5));
            objcount++;
            //	System.out.println(Arrays.toString(Display.getViewportData()));
            for (int i = 0; i < 100; i++) {
                GameObject2D gom;
                gom = new Sprite("dumm" + i, i % 2 == 0 ? js : jn, null) {
//					 @Override
//					 public void paint(SpriteBatch s) {
//						 s.color().set(1, 1, 1, 1);
//						 s.fillRect(0, 0, 1000, 1000);
//					 }

                }.setColor(new Color(1, 1, 1, 0.5f));
                gom.getTransform().setScale(1.5f).setPosition(i * 100, i * 70);
                Instance.getCurrent2DScene().addGameObject(gom);
            }
            Light2D lllll = new Light2D("dickes fettes licht2d", animation, null);
            Instance.getCurrent2DScene().addGameObject(lllll);
            System.out.println("Done: " + objcount + " Objects.");
            // Instance.getCurrentScene().addGameObject(new Entity(tm).setColor(0, 1, 0,
            // 1).setScale(new
            // Vector3f(scale,scale,scale)).setUpdateType(UpdateType.SEMISTATIC).setRelativePos(0,
            // 0, 0));
            Instance.getCurrent2DScene().setCamera(Instance.getCurrent3DCamera());
            // ParticleSystem - unoptimisiert 70FPS - optimisiert 83 FPS
            Instance.getGameSettings().setMultithreadedParticles(true);
            system = new AttractedPaticleSystem(0, 0, 0,
                    new ParticleAtlas(ResourceLoader.currentInstance().getTexture("omnikryptec:test:cosmic.png")/*SimpleTexture.newTexture("/omnikryptec/test/cosmic.png")*/, 4, BlendMode.ADDITIVE),
                    1000, 0, 1000, 1f, RenderType.ALWAYS).setParticlesAttractingEachOther(false).setAverageMass(100)
                    .setMassError(0.75f);
            // system.setParent(Instance.getCurrentCamera());
            system.setSpawnArea(new ParticleSpawnArea(ParticleSpawnAreaType.SHPERE, new Vector3f(0, 1, 0), 20));
            // system.setTimeMultiplier(3);
            // system.setAverageStartScale(0.01f);
            // system.setAverageEndScale(1);
            // system.setSystemLifeLength(20);
            // For AttractedPaticleSystem
            // system.addAttractor(0, 0, 0, 50F, 50F, AttractorMode.NOTHING, false);

            // system.addAttractor(150,600,0, 35.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.addAttractor(-500,500,100, 40.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.getLastAddedAttractor().setAttenuation(0, 0.1f, 1); //Der nicht
            // system.addAttractor(-200,-150,0, 35.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            testrend = new FloorReflectionRenderer(new RenderConfiguration(),
                    new FrameBufferObject(320, 180, DepthbufferType.NONE), 0);
            testrend.getRenderConfig().setRendererData(AllowedRenderer.EvElse, testrend);
            testrend.registerAndAddToCurrentScene();

            // Instance.getCurrentScene().getRenderConfig().setShaderLvl(0);
            attractor = new ParticleAttractor(0, -10, 0).setGravitation(100f).setDistanceTolerance(10)
                    .setMode(AttractorMode.STOP_UNTIL_DISABLED_ON_REACH);
            system.addAttractor(attractor);
            system.setSpawnOffsets(new Vector3f[]{new Vector3f(0, 0, 200)});
            // system.addAttractor(150,600,0, 350000.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.addAttractor(-500,500,100, 40.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.getLastAddedAttractor().setAttenuation(0, 0.1f, 1);
            // system.addAttractor(-200,-150,0, 35.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.addAttractor(50,-200,-200, 35.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.getLastAddedAttractor().setAttenuation(0, 1, 1);
            // system.addAttractor(attractor=new
            // ParticleAttractor(0,0,0).setAcceleration(11.0F).setTolerance(50F).setMode(AttractorMode.STOP_UNTIL_DISABLED_ON_REACH));
            // system.addAttractor(-250, 0, -250, 10.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.addAttractor(0, 0, 500, 10.0F, 50F,
            // AttractorMode.STOP_FOREVER_ON_REACH);
            // system.addAttractor(attractor = new
            // ParticleAttractor(system).setAcceleration(0).setTolerance(25).setMode(AttractorMode.NOTHING));
            // system.addAttractor(0, 300, 500, 200.0F, 100.0F, false);
            // system.addAttractor(-200, 300, 0, 75.0F, 50.0F, true);
            // system.addAttractor(200, 300, 0, 75.0F, 50.0F, true);
            // system.setSystemLifeLength(ParticleSystem.LIFELENGTH_SYSTEM_ONETICKBURST);
            system.setGlobal(true);
            system.setStartcolor(new Color(1, 0, 1));
            system.setEndcolor(new Color(1, 1, 0));
            // OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(system);
            // ParticleMaster.instance().addParticle(new Particle(new
            // ParticleTexture(SimpleTexture.newTexture("/omnikryptec/test/cosmic.png"),
            // 4,true)));

            // l.getTransform().setPosition(0, 200, 0);
            // OmniKryptecEngine.instance().getCurrentScene()
            // .addGameObject(l.setAttenuation(1, 0f, 0).setCuttOffRange(50).setColor(1, 0f,
            // 0f).setConeDegrees(35).setConeAttenuation(0.8f, 0.1f, 0).setConeDirection(0,
            // -1, 0));
            l = new Light3D();
            OmniKryptecEngine.instance().getCurrent3DScene()
                    .addGameObject((GameObject3D) l.setAttenuation(1, 0, 0).setColor(1, 1, 1).setDirectional(true)
                            .setConeAttenuation(1, 0, 0).setConeDegrees(55).setDirection(0, -1, 0).setGlobal(true));
            l.getTransform().setPosition(0, 0, 0);
            // Light l2 = new Light();
            // OmniKryptecEngine.instance().getCurrentScene()
            // .addGameObject(l2.setAttenuation(1, 0, 0).setColor(1, 0,
            // 1).setDirectional(false)
            // .setConeAttenuation(1, 0, 0).setConeDegrees(30).setDirection(0, -1,
            // 0).setGlobal(true));
            // l2.getTransform().setPosition(0, 100, 0);
            // Light l3 = new Light();
            // OmniKryptecEngine.instance().getCurrentScene()
            // .addGameObject(l3.setAttenuation(1, 0, 0).setColor(0, 0,
            // 1).setDirectional(false)
            // .setConeAttenuation(1, 0, 0).setConeDegrees(30).setDirection(0, -0.75f,
            // 0.25f).setGlobal(true));
            // l3.getTransform().setPosition(100, 100, 0);
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

            
            OmniKryptecEngine.instance().startLoop();
        } catch (Exception ex) {
            Logger.logErr("Error: " + ex, ex);
        }
    }

    static Light3D l;
    private static float v = 50;
    private static ParticleAttractor attractor;

    private static void doCameraLogic(Camera camera) {
        // v += DisplayManager.instance().getDeltaTime()*30;
        InputManager.doFirstPersonController(camera,
                OmniKryptecEngine.instance().getDisplayManager().getSettings().getKeySettings(), v, v,
                (float) Math.toRadians(20), false);
        // camera.setPerspectiveProjection(Maths.alterFOV(10, 179f, v, 1000),
        // 1000, 0.001f);
        Logger.CONSOLE.setTitle(camera.toString());
    }

    private static AttractedPaticleSystem system; // For SimpleParticleSystem
    // private static AttractedPaticleSystem system; //For AttractedPaticleSystem
    private Random ra = new Random();
    private double d = 0;

    @Override
    public void onEvent(Event ev) {

        // system.generateParticles(1);
        if (ev.getType() == EventType.RENDER_FRAME_EVENT) {

            // SpriteBatch testb = new SpriteBatch(new
            // Camera().setDefaultScreenSpaceProjection(), new Shader2D(), 100);
            // //testb.getCamera().getTransform().setPosition(0, 0, 10);
            // testb.begin();
            // testb.drawTest();
            // float[] f = testb.getData();
            // //testb.drawPolygon(f, 3);
            // testb.end();
            // System.out.println(OmniKryptecEngine.instance().getDisplayManager().getFPSCounted());
            // if(Instance.getFramecount()>1000) {
            // system.setTimeMultiplier(0.01f);
            // attractor.setEnabled(false);
            // }
            // if(Instance.getFramecount()>3000) {
            // system.setTimeMultiplier(0.05f);
            // }
            // if(Instance.getFramecount()>3300) {
            // system.setTimeMultiplier(0.1f);
            // }
            // if(Instance.getFramecount()>3600) {
            // system.setTimeMultiplier(0.5f);
            // }
            // l.setColor(Color.blend(new Color(0, 0, 0, 1), new Color(1, 0, 0, 1),
            // (DisplayManager.instance().getFramecount()/100.0f)%1f));
            // if (Math.random() < 0.095) {
            // l.setColor(Color.randomRGB());
            // }
            // if (Math.random() < 0.055) {
            // Vector3f vec = Maths.generateRandomUnitVectorWithinCone(ra, new Vector3f(0,
            // -1, 0),
            // Math.toRadians(200));
            // l.setDirection(vec);
            // //ev.getScene().getCamera().reflect(0);
            // }
            // rend.render(Instance.getCurrentScene(), null, null);
            // if((Instance.getDisplayManager().getFramecount())%100==0){
            // attractor.setEnabled(!attractor.isEnabled());
            // }
            // attractor.setAcceleration((float)
            // (100*Math.sin(DisplayManager.instance().getCurrentTime()*10)));
            // system.setActive(ra.nextInt(100)<40);
            // d += 0.025*system.getTimeMultiplier();
            // system.setRelativePos((float)(50*Math.sin(d)), -25, (float)
            // (50*Math.cos(d/2)));
            // Display.setTitle("FPS: " + DisplayManager.instance().getFPS()+" / SFPS: " +
            // DisplayManager.instance().getSmoothedFPS()+" / Vertices:
            // "+OmniKryptecEngine.instance().getModelVertsCount()+" / PPStages:
            // "+PostProcessing.instance().getActiveStageCount()+ " / Renderer P.:
            // "+ParticleMaster.instance().getRenderedParticlesCount()+" (updated P.:
            // "+ParticleMaster.instance().getUpdatedParticlesCount()+") ");
        }
        // System.out.println("(Rendertime: "+Instance.getEngine().getRenderTimeMS()+"
        // Particletime: "+ParticleMaster.instance().getOverallParticleTimeMS()+"
        // PPTime:
        // "+PostProcessing.instance().getRenderTimeMS()+")/"+Instance.getEngine().getFrameTimeMS());
        if (ev.getType() == EventType.AFTER_FRAME) {
            // System.out.println(ParticleMaster.instance().getRenderedParticlesCount());
            // System.out.println(Instance.getEngine().getModelVertsCount());
            // System.out.println(ParticleMaster.instance().getUpdatedParticlesCount());
            // Logger.log(new Profiler().createTimesString(50, true, false));
        } // Logger.log(new Profiler().createTimesString(50, true, false));

        // System.out.println(DisplayManager.instance().getFPS());
        // System.out.println(DisplayManager.instance().getDeltaTime());
    }

}
