package omnikryptec.main;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL30;

import omnikryptec.display.DisplayManager;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Commands;
import omnikryptec.logger.Logger;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.Render;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.storing.Material;
import omnikryptec.storing.VertexArrayObject;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.RenderUtil;

/**
 *
 * @author Panzer1119 & pcfreak9000
 */
public class OmniKryptecEngine {
	
    private static final String DEFAULT_NORMALMAP = "/omnikryptec/storing/normal.png";

    private static OmniKryptecEngine instance;

    public static OmniKryptecEngine instance(){
    	if(instance==null){
    		if(DisplayManager.instance()==null){
    			throw new IllegalStateException("Cant create the Engine because the DisplayManager is not created yet!");
    		}
    		new OmniKryptecEngine(DisplayManager.instance());
    	}
    	return instance;
    }
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
    }

    public static enum State{
        Starting,
        Running,
        Error,
        Stopping,
        Stopped;
    }
    
    public static enum ShutdownOption{
    	JAVA(2),
        ENGINE(1),
        NOTHING(0);
    	
    	private final int level;
        
    	private ShutdownOption(int level){
            this.level = level;
    	}
    	
    	public int getLevel(){
            return level;
    	}
    }
    
    private State state = State.Stopped;
    
    public State getState(){
    	return state;
    }
    
    private DisplayManager manager;
    private EventSystem eventsystem; 
    private final Map<String, Scene> scenes = new HashMap<>();
    private String sceneCurrentName;
    private Scene sceneCurrent;
    
    private FrameBufferObject scenefbo;
    private FrameBufferObject unsampledfbo;
    
    private ShutdownOption shutdownOption = ShutdownOption.NOTHING;
    private boolean requestclose = false;
    
    public OmniKryptecEngine(DisplayManager manager){
    	if(manager == null){
            throw new NullPointerException("DisplayManager is null");
    	}
    	if(instance!=null){
    		throw new IllegalStateException("OmniKryptec-Engine is already made!");
    	}
    	this.manager = manager;
    	state = State.Starting;
    	instance = this;
    	eventsystem = EventSystem.instance();
    	Material.setDefaultNormalMap(Texture.newTexture(OmniKryptecEngine.class.getResourceAsStream(DEFAULT_NORMALMAP)).create());
    	RenderUtil.cullBackFaces(true);
    	RenderUtil.enableDepthTesting(true);
    	RendererRegistration.init();
    	createFbos();
    	eventsystem.fireEvent(new Event(), EventType.BOOTING_COMPLETED);
    }
    
    private void createFbos(){
    	scenefbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), manager.getSettings().getMultiSamples(), GL30.GL_COLOR_ATTACHMENT0);
    	unsampledfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE, GL30.GL_COLOR_ATTACHMENT0);
    }
    
    private void resizeFbos(){
    	scenefbo.clear();
    	unsampledfbo.clear();
    	createFbos();
    }
    
    public DisplayManager getDisplayManager(){
    	return manager;
    }
    
    public EventSystem getEventsystem(){
    	return eventsystem;
    }
    
    public void startLoop(ShutdownOption shutdownOption){
        setShutdownOption(shutdownOption);
    	try{
    		state = State.Running;
    		while(!Display.isCloseRequested()&&!requestclose){
    			frame(true);
    		}
    		
    	}catch(Exception e){
    		state = State.Error;
    		eventsystem.fireEvent(new Event(e), EventType.ERROR);
                Logger.logErr("Error while looping: " + e, e);
    	}
    	close(this.shutdownOption);
    }
    
    public OmniKryptecEngine requestClose() {
        return requestClose(shutdownOption);
    }
    
    public OmniKryptecEngine requestClose(ShutdownOption shutdownOption) {
        setShutdownOption(shutdownOption);
    	requestclose = true;
        return this;
    }
    
    public OmniKryptecEngine frame(boolean clear){
    	if(Display.wasResized()){
    		resizeFbos();
    		eventsystem.fireEvent(new Event(manager), EventType.RESIZED);
    	}
    	scenefbo.bindFrameBuffer();
    	if(clear){
    		RenderUtil.clear(0, 0, 0, 0);
    	}
    	if(sceneCurrent != null){
            sceneCurrent.frame(Render.All);
    	}
    	scenefbo.unbindFrameBuffer();
    	scenefbo.resolveToFbo(unsampledfbo, GL30.GL_COLOR_ATTACHMENT0);
    	PostProcessing.doPostProcessing(unsampledfbo);
    	InputUtil.nextFrame();
    	DisplayManager.instance().updateDisplay();
    	eventsystem.fireEvent(new Event(), EventType.FRAME_EVENT);
    	eventsystem.fireEvent(new Event(), EventType.RENDER_EVENT);
    	return this;
    }
    
    public OmniKryptecEngine close(ShutdownOption shutdownOption){
    	if(shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()){
    		state = State.Stopping;
            cleanup();
            manager.close();
            state = State.Stopped;
            if(shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()){
                Commands.COMMANDEXIT.run("-java");
            }
            return null;
    	}else{
    		return this;
    	}
    }
    
    private void cleanup(){
    	RenderChunk.cleanup();
    	PostProcessing.cleanup();
    	VertexArrayObject.cleanup();
    	FrameBufferObject.cleanup();
    	RendererRegistration.cleanup();
    }
    
    public OmniKryptecEngine addAndSetScene(String name, Scene scene){
    	addScene(name, scene);
    	setScene(name);
        return this;
    }
    
    public OmniKryptecEngine addScene(String name, Scene scene){
    	if(name != null && scene != null){
            scenes.put(name, scene);
    	}
        return this;
    }
    
    public OmniKryptecEngine setScene(String name){
    	sceneCurrent = scenes.get(name);
    	if(sceneCurrent != null){
            sceneCurrentName = name;
    	}
        return this;
    }
    
    public Scene getCurrentScene(){
    	return sceneCurrent;
    }
    
    public String getCurrentSceneName(){
    	return sceneCurrentName;
    }

    public ShutdownOption getShutdownOption() {
        return shutdownOption;
    }

    public OmniKryptecEngine setShutdownOption(ShutdownOption shutdownOption) {
        this.shutdownOption = shutdownOption;
        return this;
    }
    
}
