/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.test;

import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.joml.Vector3f;
import org.lwjgl.opencl.CL10;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.lang.LanguageManager;
import de.omnikryptec.display.Display;
import de.omnikryptec.display.DisplayManager;
import de.omnikryptec.display.GLFWInfo;
import de.omnikryptec.event.eventV2.EventSubscription;
import de.omnikryptec.event.eventV2.engineevents.FrameEvent;
import de.omnikryptec.event.eventV2.engineevents.FrameEvent.FrameType;
import de.omnikryptec.event.input.InputManager;
import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.gameobject.GameObject3D;
import de.omnikryptec.gameobject.Light3D;
import de.omnikryptec.gameobject.particles.AttractedPaticleSystem;
import de.omnikryptec.gameobject.particles.ParticleAttractor;
import de.omnikryptec.gui.ProgressBar;
import de.omnikryptec.gui.TexturedGuiContainer;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.main.Scene3D;
import de.omnikryptec.opencl.core.CLCommandQueue;
import de.omnikryptec.opencl.core.CLContext;
import de.omnikryptec.opencl.core.CLDevice;
import de.omnikryptec.opencl.core.CLKernel;
import de.omnikryptec.opencl.core.CLPlatform;
import de.omnikryptec.opencl.core.CLProgram;
import de.omnikryptec.opencl.core.OpenCL;
import de.omnikryptec.renderer.d3.FloorReflectionRenderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.resource.loader.ResourceLoader;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.resource.model.Material;
import de.omnikryptec.resource.model.Model;
import de.omnikryptec.resource.model.TexturedModel;
import de.omnikryptec.resource.texture.SimpleTexture;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.util.EnumCollection.UpdateType;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.NativesLoader;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;


/**
 *
 * @author Panzer1119
 */

public class EngineTest2 {

    static TestRenderer rend;
    static AdvancedModel testdings;
    static FloorReflectionRenderer testrend;

    static final String KERNEL = "kernel void sum(global const float* a, global const float* b, global float* result, int const size) {\r\n" +
    		"    const int itemId = get_global_id(0); \r\n" +
    		"    if(itemId < size) {\r\n" +
    		"        result[itemId] = a[itemId] + b[itemId];\r\n" +
    		"    }\r\n" +
    		"} ";
    
    public static Object someS(Object k) {
    	return k;
    }
    
    public static void main(String[] args) {
//       int hm = 30;
//    	HashMap<String, Object> test = new HashMap<>();
//    	for(int i=0; i<hm; i++) {
//    		test.put(i+"", "gurke"+i);
//    	}
//    	int ttt = 0;
//    	int rofl=0;
//    	double amount = 2_00_000_000;
//    	long time = System.currentTimeMillis();
//    	for(int i=0; i<amount; i++) {
//    		//Object p = someS(rofl);
//    		Object p = test.get(rofl+"");
//    		ttt += p.hashCode();
//    		rofl++;
//    		rofl%=hm;
//    	}
//    	long time2 = System.currentTimeMillis();
//    	System.out.println("All time: "+(time2-time)+"ms Each: "+(time2-time)/amount+"ms ttt: "+ttt);
//    	System.exit(0);
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
            Display.setAspectRatio(16.0 / 9);
            OpenCL.create();
            CLPlatform platform = OpenCL.getPlatform(0);
            CLDevice device = platform.createDeviceData(CL10.CL_DEVICE_TYPE_ALL).getDevice(0);
            CLContext context = new CLContext(device);
            CLCommandQueue queue = new CLCommandQueue(context, device, CL10.CL_QUEUE_PROFILING_ENABLE);
            CLProgram program = new CLProgram(context, KERNEL).build(device, 1024);
            CLKernel kernel = new CLKernel(program, "sum");
//            int size=100;
//            FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
//            float[] tempData = new float[size];
//            for(int i = 0; i < size; i++) {
//                tempData[i] = i;
//            }
//            aBuff.put(tempData);
//            aBuff.rewind();
//            // Create float array from size-1 to 0. This means that the result should be size-1 for each element.
//            FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
//            for(int j = 0, i = size-1; j < size; j++, i--) {
//                tempData[j] = i;
//            }
//            FloatBuffer result = BufferUtils.createFloatBuffer(size);
//            bBuff.put(tempData);
//            bBuff.rewind();
//            kernel.setArg(0, aBuff);
//            kernel.setArg(1, bBuff);
//            kernel.setArg(2, result);
//            kernel.setArg(3, size);
//            kernel.enqueue(queue, 1, size, 0);
//            queue.finish();
//            System.out.println(result);
            //   XMLUtil.save(OmniKryptecEngine.instance().getDisplayManager().getSettings().toXMLDocument(), Format.getPrettyFormat(), new AdvancedFile(false, "gamesettings.xml").createOutputstream(false));
            
            // new Thread(new Runnable() {
            //
            // @Override
            // public void run() {
            // LiveProfiler liveProfiler = new LiveProfiler(750, 750);
            // liveProfiler.startTimer(1000);
            // }
            // }).start();
            // rend = new TestRenderer();


          
            
            final AdvancedFile res = new AdvancedFile(true, "res"); //TODO Move this to the test
//            final AdvancedFile test = new AdvancedFile(true, "omnikryptec", "test");
//            //System.out.println(res);
            ResourceLoader.createInstanceDefault(true, false);
//            //ResourceLoader.currentInstance().stageAdvancedFiles(1, ResourceLoader.LOAD_XML_INFO, res);
//            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "jd.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(1, ResourceLoader.LOAD_XML_INFO, new AdvancedFile(true, res, "js.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "jn.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen.obj"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen_normal.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "brunnen_specular.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "final_tree_3.png"));
            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "final_tree_3.obj"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "block.obj"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(1, new AdvancedFile(true, res, "diffuse.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "pine.obj"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "pine2.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "pine2_normal.png"));
//            ResourceLoader.currentInstance().stageAdvancedFiles(0, new AdvancedFile(true, test, "cosmic.png"));
            ResourceLoader.currentInstance().loadStagedAdvancedFiles(true);
//            //System.out.println("Resources loaded: " + ResourceLoader.currentInstance().getLoadedData());
//            SimpleTexture jd = ResourceLoader.currentInstance().getTexture("res:jd.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "jd.png"))*/;
//            SimpleTexture js = ResourceLoader.currentInstance().getTexture("res:js.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "js.png"))*/;
//            //SimpleTexture jn = ResourceLoader.currentInstance().getTexture("res:jn.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "jn.png"))*/;
//            //OmniKryptecEngine.instance().getEventsystem().addEventHandler(new EngineTest2(), EventType.AFTER_FRAME, EventType.RENDER_FRAME_EVENT);
//            Model brunnen = ResourceLoader.currentInstance().getResource(Model.class, "res:brunnen.obj")/*new Model("", ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/brunnen.obj")))*/;
//            // Model brunnen = ModelUtil.generateQuad();
//            SimpleTexture brunnent = ResourceLoader.currentInstance().getTexture("omnikryptec:test:brunnen.png")/*SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/brunnen.png")).create()*/;
//            SimpleTexture brunnen_norm = ResourceLoader.currentInstance().getTexture("omnikryptec:test:brunnen_normal.png")/*SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/brunnen_normal.png")).create()*/;
//            SimpleTexture brunnen_specular = ResourceLoader.currentInstance().getTexture("omnikryptec:test:brunnen_specular.png")/*SimpleTexture.newTexture("/de/omnikryptec/test/brunnen_specular.png")*/;
            SimpleTexture baum = ResourceLoader.currentInstance().getTexture("res:final_tree_3.png")/*SimpleTexture.newTexture(new AdvancedFile(true, res, "final_tree_3.png"))*/;
//            //System.out.println(new AdvancedFile(true, res, "final_tree_3.obj"));
//            Model baumM = ResourceLoader.currentInstance().getResource(Model.class, "res:final_tree_3.obj")/*Model.newModel(new AdvancedFile(true, res, "final_tree_3.obj"))*/;
             //Model baumM = Model.newModel(new AdvancedFile(true, res, "final_tree_3."));
            Model baumM = ResourceLoader.currentInstance().getResource("res:final_tree_3.obj");
//            AtlasTexture rmvp = new AtlasTexture(brunnent, 0.25f, 0.25f, 0.5f, 0.5f);
//            Model BLOCK = ResourceLoader.currentInstance().getResource(Model.class, "res:block.obj")/*new Model("", ObjLoader.loadOBJ(new AdvancedFile(true, res, "block.obj")))*/;
            TexturedModel tm = new TexturedModel("brunnen", baumM, baum);
            tm.getMaterial().setRenderer(RendererRegistration.FORWARD_MESH_RENDERER);
//            testdings = tm;
//            // tm.getMaterial().setTexture(Material.NORMAL,
//            // brunnen_norm).setTexture(Material.SPECULAR, brunnen_specular);
//            // tm.getMaterial().setNormalmap(jn).setSpecularmap(js);
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
//            OmniKryptecEngine.instance().addAndSetScene(new Scene2D("test2d", new Camera().setOrthographicProjection2D(0, 0, 2000, 2000)).setAmbientColor(1, 1, 1));
//            Instance.getCurrent3DCamera().getTransform().setPosition(0, 0, 200);
//            // OmniKryptecEngine.instance().addAndSetScene(null);
//            // Instance.getCurrentCamera().getTransform().setPosition(0, 0, 0);
//            Model pine = ResourceLoader.currentInstance().getResource(Model.class, "omnikryptec:test:pine.obj")/*new Model("", ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/pine.obj")))*/;
//            //Model bauer = new Model("", ColladaLoader.loadColladaModel(new AdvancedFile(true, "res", "model.dae"), 50).getMeshData());
//            SimpleTexture bauert = ResourceLoader.currentInstance().getTexture("res:diffuse.png")/*SimpleTexture.newTexture("/res/diffuse.png")*/;
//            SimpleTexture pinet = ResourceLoader.currentInstance().getTexture("omnikryptec:test:pine2.png")/*SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/pine2.png")).create()*/;
//
//            SimpleAnimation animation = new SimpleAnimation(1, brunnent, pinet);
//            SimpleTexture pine_normal = ResourceLoader.currentInstance().getTexture("omnikryptec:test:pine2_normal.png")/*SimpleTexture.newTexture("/de/omnikryptec/test/pine2_normal.png")*/;
//
//            TexturedModel ptm = new TexturedModel("pine", pine, pinet);
//            ptm.getMaterial().setTexture(Material.NORMAL, pine_normal);
//            ptm.getMaterial().setHasTransparency(true).setRenderer(RendererRegistration.FORWARD_MESH_RENDERER);
//            ptm.getMaterial().setVector3f(Material.REFLECTIVITY, new Vector3f(1f)).setFloat(Material.DAMPER, 10)
//                    .setVector3f(Material.SHADERINFO, new Vector3f(1, 1, 0));
//
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
            int abstand = 10;
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

            l = new Light3D();
            OmniKryptecEngine.instance().getCurrent3DScene()
                    .addGameObject((GameObject3D) l.setAttenuation(1, 0, 0).setColor(1, 1, 1).setDirectional(true)
                            .setConeAttenuation(1, 0, 0).setConeDegrees(55).setDirection(0, -1, 0).setGlobal(true));

            //((DefaultGameLoop)OmniKryptecEngine.instance().getLoop()).setMode(DefaultGameLoop.MODE_GUI);

            ProgressBar bar = new ProgressBar(null, null, 0.1f, 0.25f, 0.8f, 0.01f);
            OmniKryptecEngine.instance().setGui(bar);
            bar.getColor().set(1, 0, 0);
            bar.getBarColor().set(0, 1, 0);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					bar.setValue(bar.getValue()+0.01f);
				}
			}, 100, 100);
            TexturedGuiContainer guic = new TexturedGuiContainer(ResourceLoader.MISSING_TEXTURE, 0, 0, 1, 1);
            guic.getColor().set(1, 1, 0);
           // bar.add(guic);
            OmniKryptecEngine.instance().ENGINE_BUS.registerEventHandler(new EngineTest2());
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
   // public static DynamicParticleSimulation sim;
    
    @EventSubscription
    public void onEvent(FrameEvent ev) {

        // system.generateParticles(1);
        if (ev.getType() == FrameType.ENDSCENE) {
        	System.out.println(InputManager.getMouseHandler().getPosInVP());
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
        if (ev.getType() == FrameType.POST) {
            // System.out.println(ParticleMaster.instance().getRenderedParticlesCount());
            // System.out.println(Instance.getEngine().getModelVertsCount());
            // System.out.println(ParticleMaster.instance().getUpdatedParticlesCount());
            // Logger.log(new Profiler().createTimesString(50, true, false));
        } // Logger.log(new Profiler().createTimesString(50, true, false));

        // System.out.println(DisplayManager.instance().getFPS());
        // System.out.println(DisplayManager.instance().getDeltaTime());
    }
    
    private static void printFloatBuffer(FloatBuffer buffer) {
    	String s="";
    	for(int i=0; i<buffer.capacity(); i++) {
    		s+=buffer.get(i)+" ";
    	}
    	System.out.println(s);
    }

}
