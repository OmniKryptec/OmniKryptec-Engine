package omnikryptec.main;

import java.util.HashMap;
import omnikryptec.audio.AudioManager;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL30;

import omnikryptec.display.DisplayManager;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Commands;
import omnikryptec.logger.Logger;
import omnikryptec.model.Material;
import omnikryptec.model.VertexArrayObject;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.RenderUtil;

/**
 *
 * @author Panzer1119 & pcfreak9000
 */
public class OmniKryptecEngine {
	
    private static final String DEFAULT_NORMALMAP = "/omnikryptec/model/normal.png";

    private static OmniKryptecEngine instance;

    public static OmniKryptecEngine instance() {
    	if(instance == null) {
            if(DisplayManager.instance() == null) {
                throw new IllegalStateException("Cant create the Engine because the DisplayManager is not created yet!");
            }
            new OmniKryptecEngine(DisplayManager.instance());
    	}
    	return instance;
    }
    
    public static OmniKryptecEngine getInstance() {
        return instance;
    }
    
    public static void addShutdownHook(Runnable run) {
        if(run == null) {
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(run));
    }
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
    }

    public static enum State {
        Starting,
        Running,
        Error,
        Stopping,
        Stopped;
    }
    
    public static enum ShutdownOption {
    	JAVA(2),
        ENGINE(1),
        NOTHING(0);
    	
    	private final int level;
        
    	private ShutdownOption(int level) {
            this.level = level;
    	}
    	
    	public int getLevel() {
            return level;
    	}
    }
    
    private State state = State.Stopped;
    
    public State getState() {
    	return state;
    }
    
    private DisplayManager manager;
    private EventSystem eventsystem;
    private PostProcessing postpro;
    private final HashMap<String, Scene> scenes = new HashMap<>();
    private String sceneCurrentName;
    private Scene sceneCurrent;

    
    private ShutdownOption shutdownOption = ShutdownOption.NOTHING;
    private boolean requestclose = false;
    
    public OmniKryptecEngine(DisplayManager manager) {
    	if(manager == null){
            throw new NullPointerException("DisplayManager is null");
    	}
    	if(instance != null){
            throw new IllegalStateException("OmniKryptec-Engine was already created!");
    	}
    	this.manager = manager;
    	state = State.Starting;
    	instance = this;
    	eventsystem = EventSystem.instance();
    	postpro = PostProcessing.instance();
    	Material.setDefaultNormalMap(Texture.newTexture(OmniKryptecEngine.class.getResourceAsStream(DEFAULT_NORMALMAP)).create());
    	RenderUtil.cullBackFaces(true);
    	RenderUtil.enableDepthTesting(true);
    	RendererRegistration.init();
    	createFbos();
    	eventsystem.fireEvent(new Event(), EventType.BOOTING_COMPLETED);
    }
    
    private FrameBufferObject scenefbo;
    private FrameBufferObject unsampledfbo,normalfbo,specularfbo;
    private FrameBufferObject[] add;
    
    private void createFbos() {
    	scenefbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), manager.getSettings().getMultiSamples(), manager.getSettings().getAddAttachments(), GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2);
    	unsampledfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE, GL30.GL_COLOR_ATTACHMENT0);
    	normalfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE, GL30.GL_COLOR_ATTACHMENT0);
    	specularfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE, GL30.GL_COLOR_ATTACHMENT0);
    	add = manager.getSettings().getAddFBOs();
    }
    
    private void resizeFbos() {
    	scenefbo.delete();
    	unsampledfbo.delete();
    	normalfbo.delete();
    	specularfbo.delete();
    	createFbos();
    }
    
    public final DisplayManager getDisplayManager() {
    	return manager;
    }
    
    public final EventSystem getEventsystem() {
    	return eventsystem;
    }
    
    public final PostProcessing getPostprocessor(){
    	return postpro;
    }
    
    public final void startLoop(ShutdownOption shutdownOption) {
        setShutdownOption(shutdownOption);
        state = State.Running;
        while(!Display.isCloseRequested() && !requestclose) {
        	frame(true);
        }	
    	close(this.shutdownOption);
    }
    
    public final OmniKryptecEngine requestClose() {
        return requestClose(shutdownOption);
    }
    
    public final OmniKryptecEngine requestClose(ShutdownOption shutdownOption) {
        setShutdownOption(shutdownOption);
    	requestclose = true;
        return this;
    }
        
    public final OmniKryptecEngine frame(boolean clear) {
        final long currentTime = manager.getCurrentTime();
    	try{
	    	if(!Display.isActive()){
	    		Display.update();
        		try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					errorOccured(e, "Error occured while sleeping: ");
				}
				return this;
	    	}
	    	InputUtil.nextFrame();
                AudioManager.update(currentTime);
    		if(Display.wasResized()) {
	            resizeFbos();
	            PostProcessing.instance().resize();
	            eventsystem.fireEvent(new Event(manager), EventType.RESIZED);
	    	}
	    	scenefbo.bindFrameBuffer();
	    	if(sceneCurrent != null) {
	    		if(clear) {
		            RenderUtil.clear(sceneCurrent.getClearColor());
		    	}
	            sceneCurrent.frame(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, AllowedRenderer.All);
	    	}
	    	
	    	scenefbo.unbindFrameBuffer();
	    	scenefbo.resolveToFbo(unsampledfbo, GL30.GL_COLOR_ATTACHMENT0);
	    	scenefbo.resolveToFbo(normalfbo, GL30.GL_COLOR_ATTACHMENT1);
	    	scenefbo.resolveToFbo(specularfbo, GL30.GL_COLOR_ATTACHMENT2);
	    	if(scenefbo.getTargets().length>3){
	    		for(int i=3; i<scenefbo.getTargets().length; i++){
	    			scenefbo.resolveToFbo(add[i], manager.getSettings().getAddAttachments()[i-3]);
	    		}
	    	}
	    	PostProcessing.instance().doPostProcessing(add, unsampledfbo, normalfbo, specularfbo);
	    	eventsystem.fireEvent(new Event(), EventType.FRAME_EVENT);
	    	eventsystem.fireEvent(new Event(), EventType.RENDER_EVENT);
	    	DisplayManager.instance().updateDisplay();
    	}catch(Exception e){
    		errorOccured(e, "Error occured in frame: ");
    	}
    	return this;
    }
    
    private void errorOccured(Exception e, String text){
    	state = State.Error;
        Logger.logErr(text + e, e);
        eventsystem.fireEvent(new Event(e), EventType.ERROR);
    }
    
    public final OmniKryptecEngine close(ShutdownOption shutdownOption) {
    	if(shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()) {
            state = State.Stopping;
            cleanup();
            manager.close();
            state = State.Stopped;
            if(shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()) {
                Commands.COMMANDEXIT.run("-java");
            }
            return null;
    	} else {
            return this;
    	}
    }
    
    private void cleanup() {
    	RenderChunk.cleanup();
    	PostProcessing.cleanup();
    	VertexArrayObject.cleanup();
    	FrameBufferObject.cleanup();
    	RendererRegistration.cleanup();
    }
    
    public final OmniKryptecEngine addAndSetScene(String name, Scene scene) {
    	addScene(name, scene);
    	setScene(name);
        return this;
    }
    
    public final OmniKryptecEngine addScene(String name, Scene scene) {
    	if(name != null && scene != null) {
            scenes.put(name, scene);
    	}
        return this;
    }
    
    public final OmniKryptecEngine setScene(String name) {
    	sceneCurrent = scenes.get(name);
    	if(sceneCurrent != null) {
            sceneCurrentName = name;
    	}
        return this;
    }
    
    public final Scene getCurrentScene() {
    	return sceneCurrent;
    }
    
    public final String getCurrentSceneName() {
    	return sceneCurrentName;
    }

    public final ShutdownOption getShutdownOption() {
        return shutdownOption;
    }

    public final OmniKryptecEngine setShutdownOption(ShutdownOption shutdownOption) {
        this.shutdownOption = shutdownOption;
        return this;
    }
    
}
