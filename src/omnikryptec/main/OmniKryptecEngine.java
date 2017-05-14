package omnikryptec.main;

/**
 *
 * @author Panzer1119 & pcfreak9000
 */
public class OmniKryptecEngine {
	
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }

    
    public static enum State{
		Starting, Running, Error, Stopped;
	}
    
    private static State state = State.Stopped;
    
    public static State getState(){
    	return state;
    }
    
    
    
}
