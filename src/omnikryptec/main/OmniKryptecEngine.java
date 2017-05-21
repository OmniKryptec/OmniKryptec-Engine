package omnikryptec.main;

import java.util.HashMap;
import java.util.Map;

import omnikryptec.display.DisplayManager;
import omnikryptec.event.EventSystem;
import omnikryptec.input.InputUtil;
import omnikryptec.logger.Commands;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.Render;
import omnikryptec.storing.Material;
import omnikryptec.texture.Texture;

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
    }
    
    public DisplayManager getDisplayManager(){
    	return manager;
    }
    
    public EventSystem getEventsystem(){
    	return eventsystem;
    }
    
    public void loop(ShutdownOption shutdownOption){
    	try{
    		state = State.Running;
    		
    		
    	}catch(Exception e){
    		state = State.Error;
    	}
    	close(shutdownOption);
    }
    
    public OmniKryptecEngine frame(){
    	if(sceneCurrent != null){
            sceneCurrent.frame(null, Render.All);
    	}
    	InputUtil.nextFrame();
        return this;
    }
    
    public OmniKryptecEngine close(ShutdownOption shutdownOption){
    	if(shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()){
    		state = State.Stopping;
            cleanup();
            manager.close();
            state = State.Stopped;
            if(shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()){
                Commands.COMMANDEXIT.run("java");
            }
            return null;
    	}else{
    		return this;
    	}
    }
    
    private void cleanup(){
    	RenderChunk.cleanup();
    	PostProcessing.cleanup();
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

    
}
