package omnikryptec.display;

public class GameSettings {
	
	private int width, height;
	private boolean fullscreen=false;
	private boolean resizeable=true;
	private int initialFpsCap=-1;
	/**
	 * 0 means no multisampling
	 */
	private int multisamples=0;
	
	public static final int NO_MULTISAMPLING=0;
	
	public GameSettings(int width, int height){
		this.width = width;
		this.height = height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public GameSettings setFullscreen(boolean b){
		this.fullscreen = b;
		return this;
	}
	
	public boolean wantsFullscreen(){
		return fullscreen;
	}
	
	public GameSettings setResizeable(boolean b){
		this.resizeable = b;
		return this;
	}
	
	public boolean wantsResizeable(){
		return resizeable;
	}
	
	public GameSettings setInitialFpsCap(int cap){
		this.initialFpsCap = cap;
		return this;
	}
	
	public int getInitialFpsCap(){
		return initialFpsCap;
	}
	
	public GameSettings setMultisamples(int i){
		this.multisamples = i;
		return this;
	}
	
	public int getMultiSamples(){
		return multisamples;
	}
	
}
