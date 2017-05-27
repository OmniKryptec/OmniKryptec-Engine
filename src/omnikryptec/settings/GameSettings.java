package omnikryptec.settings;

import omnikryptec.postprocessing.FBOFactory;
import omnikryptec.postprocessing.FrameBufferObject;

public class GameSettings {
	
    private String name;
    private int width;
    private int height;
    private boolean fullscreen = false;
    private boolean resizeable = true;
    private int initialFpsCap = -1;
    /**
     * 0 means no multisampling
     */
    private int multisamples = 0;
    private float anisotropic = 4;
    private boolean mipmap = false;
    private boolean clampEdges = false;
    private boolean nearest = false;
    private KeySettings keySettings;

    private int eventThreadPoolSize = 2;

    private int[] add_attachments={};
    private FBOFactory fbo_factory;
    
    private float foliageRadius=50;
    
    public static final int NO_MULTISAMPLING = 0;

    public GameSettings(String name, int width, int height) {
        this(name, width, height, KeySettings.STANDARDKEYSETTINGS);
    }
    
    public GameSettings(String name, int width, int height, KeySettings keySettings) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.keySettings = keySettings;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public GameSettings setFullscreen(boolean b) {
        this.fullscreen = b;
        return this;
    }

    public boolean wantsFullscreen() {
        return fullscreen;
    }
    
    public int[] getAddAttachments(){
    	return add_attachments;
    }
    
    public void setAdditionalAttachments(int...is){
    	this.add_attachments = is;
    }
    
    public GameSettings setResizeable(boolean b) {
        this.resizeable = b;
        return this;
    }

    public boolean wantsResizeable() {
        return resizeable;
    }

    public GameSettings setInitialFpsCap(int cap) {
        this.initialFpsCap = cap;
        return this;
    }

    public int getInitialFpsCap() {
        return initialFpsCap;
    }

    public GameSettings setMultisamples(int i) {
        this.multisamples = i;
        return this;
    }

    public int getMultiSamples() {
        return multisamples;
    }

    public float getAnisotropicLvl() {
        return anisotropic;
    }

    public GameSettings setAnisotropicLevel(float f) {
        this.anisotropic = f;
        if(f > 0) {
            this.mipmap = true;
        }
        return this;
    }

    public boolean clampEdges() {
        return clampEdges;
    }

    public GameSettings setClampEdges(boolean b) {
        this.clampEdges = b;
        return this;
    }

    public boolean mipmap() {
        return mipmap;
    }

    public GameSettings setMipmap(boolean b) {
        this.mipmap = b;
        return this;
    }

    public boolean filterNearest() {
        return nearest;
    }

    public GameSettings setFilterNearest(boolean b) {
        if(b) {
            mipmap = false;
        }
        nearest = b;
        return this;
    }

    public int getEventThreadpoolSize() {
        return eventThreadPoolSize;
    }

    public GameSettings setEventThreadpoolSize(int i) {
        this.eventThreadPoolSize = i;
        return this;
    }

    public KeySettings getKeySettings() {
        return keySettings;
    }

    public GameSettings setKeySettings(KeySettings keySettings) {
        this.keySettings = keySettings;
        return this;
    }

    public GameSettings setFBOFactory(FBOFactory fac){
    	this.fbo_factory = fac;
    	return this;
    }
    
	public FrameBufferObject[] getAddFBOs() {
		if(fbo_factory==null){
			return new FrameBufferObject[]{};
		}
		FrameBufferObject[] fbos = fbo_factory.getAllFBOs();
		if(fbos==null||fbos.length==0){
			return new FrameBufferObject[]{};
		}else{
			return fbos;
		}
	}

	public GameSettings setFoliageRadius(float f){
		this.foliageRadius = f;
		return this;
	}
	
	public float getFoliageRadius() {
		return foliageRadius;
	}
	
}
