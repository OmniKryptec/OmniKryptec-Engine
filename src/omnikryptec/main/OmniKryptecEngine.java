package omnikryptec.main;

import java.util.HashMap;
import java.util.Map;

import omnikryptec.display.DisplayManager;
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
		return instance;
	}
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
    }

    
    public static enum State{
		Starting, Running, Error, Stopped;
	}
    
    public static enum ShutdownOptions{
        
    	JAVA(2),
        ENGINE(1),
        NOTHING(0);
    	
    	private int level=0;
        
    	private ShutdownOptions(int lvl){
    		this.level = lvl;
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
    private Map<String, Scene> scenes = new HashMap<>();
    private String curscenename;
    private Scene curscene;
    
    public OmniKryptecEngine(DisplayManager manager){
    	if(manager == null){
    		throw new NullPointerException("DisplayManager is null");
    	}
    	this.manager = manager;
    	state = State.Starting;
    	instance = this;
    	Material.setDefaultNormalMap(Texture.newTexture(OmniKryptecEngine.class.getResourceAsStream(DEFAULT_NORMALMAP)).create());
    }
    
    public DisplayManager getDisplayManager(){
    	return manager;
    }
    
    public void loop(ShutdownOptions opt){
    	
    	close(opt);
    }
    
    public void frame(){
    	if(curscene!=null){
    		curscene.frame(null, Render.All);
    	}
    }
    
    public void close(ShutdownOptions opt){
    	if(opt.getLevel() >= ShutdownOptions.ENGINE.getLevel()){
    		cleanup();
    		manager.close();
    		if(opt.getLevel() >= ShutdownOptions.JAVA.getLevel()){
                    shutdownCompletely();
    		}
    	}
    }
    
    private void cleanup(){
    	RenderChunk.cleanup();
    	PostProcessing.cleanup();
    }
    
    public void addAndSetScene(String name, Scene scene){
    	addScene(name, scene);
    	setScene(name);
    }
    
    public void addScene(String name, Scene scene){
    	if(name!=null&&scene!=null){
    		scenes.put(name, scene);
    	}
    }
    
    public void setScene(String name){
    	curscene = scenes.get(name);
    	if(curscene!=null){
    		curscenename = name;
    	}
    }
    
    public Scene getCurrentScene(){
    	return curscene;
    }
    
    public String getCurrentSceneName(){
    	return curscenename;
    }
    
    private static final void shutdownCompletely() {
        while(true) {
            try {
                System.exit(0);
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
    }
    
}
