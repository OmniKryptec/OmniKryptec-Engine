package omnikryptec.main;

import omnikryptec.display.DisplayManager;
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
    
    private State state = State.Stopped;
    
    public State getState(){
    	return state;
    }
    
    public OmniKryptecEngine(DisplayManager manager){
    	if(manager==null){
    		throw new NullPointerException("DisplayManager is null");
    	}
    	state = State.Starting;
    	instance = this;
    	Material.setDefaultNormalMap(Texture.newTexture(OmniKryptecEngine.class.getResourceAsStream(DEFAULT_NORMALMAP)).create());
    }
    
    
    
}
