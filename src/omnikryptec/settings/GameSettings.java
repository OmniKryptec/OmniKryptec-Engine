package omnikryptec.settings;

import omnikryptec.gameobject.gameobject.Rangeable;
import omnikryptec.gameobject.gameobject.RenderType;
import omnikryptec.postprocessing.main.FBOFactory;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.RenderTarget;
import omnikryptec.util.Instance;

/**
 * Game settings object
 *
 * @author pcfreak9000 &amp; Panzer1119
 */
public class GameSettings {

    private int initialFPSCap = Instance.DISPLAYMANAGER_DISABLE_FPS_CAP;
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

    private RenderTarget[] add_attachments = {};
    private FBOFactory fbo_factory;

    private float foliageRadius = 50;
    private float mediumRadius = 100;
    private float bigRadius = 200;

    private long chunkOffsetX = 1;
    private long chunkOffsetY = 1;
    private long chunkOffsetZ = 1;
    private int chunkWidth = 128;
    private int chunkHeight = 128;
    private int chunkDepth = 128;

    private boolean deferredlight = false;
    private boolean forwardlight = false;
    private int max_forward_lights = 4;
    
    
    private boolean enable_chunks=false;
    private int maxinstancespdc = 10000;
    
    private boolean frustrumCulling=true;
    
    /**
     * Standard value for disabling multisampling
     */
    public static final int NO_MULTISAMPLING = 0;

    /**
     * constructs a GameSettings object with default key settings.
     */
    public GameSettings() {
        this(KeySettings.STANDARDKEYSETTINGS);
    }

    /**
     * Constructs a GameSettings object with custom KeySettings
     *
     * @param keySettings KeySettings Key settings
     */
    public GameSettings(KeySettings keySettings) {
        this.keySettings = keySettings;
    }

    /**
     * Returns the added attachments
     *
     * @return Integer Array Added attachments
     */
    public final RenderTarget[] getAddAttachments() {
        return add_attachments;
    }

    /**
     * Sets additional attachments
     *
     * @param add_attachments Added attachments
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setAdditionalAttachments(RenderTarget... add_attachments) {
        this.add_attachments = add_attachments;
        return this;
    }

    /**
     * Sets the FPS cap
     *
     * @param initialFPSCap Integer FPS Cap
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setInitialFPSCap(int initialFPSCap) {
        this.initialFPSCap = initialFPSCap;
        return this;
    }

    /**
     * Returns the FPS cap
     *
     * @return Integer FPS cap
     */
    public final int getInitialFPSCap() {
        return initialFPSCap;
    }

    /**
     * Sets the number of multisamples, {@link #NO_MULTISAMPLING} means
     * multisampling is disabled
     *
     * @param multisamples Integer Number of multisamples
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setMultisamples(int multisamples) {
        this.multisamples = multisamples;
        return this;
    }

    /**
     * Returns the number of multisamples or <code>NO_MULTISAMPLING<code> if its disabled.
     *
     * @return Integer Number of multisamples
     */
    public final int getMultiSamples() {
        return multisamples;
    }

    /**
     * Returns if RenderChunking is enabled.
     * @see #setUseRenderChunking(boolean)
     * @return boolean
     */
    public final boolean usesRenderChunking(){
    	return enable_chunks;
    }
    
    /**
     * if activated, gameobjects will be sorted based on their position. only the chunks around the camera will be rendered.
     * can break the use of prioritys for the renderer.
     * @see #setChunkRenderOffsets(long, long, long)
     * @param b
     * @return this GameSettings instance
     */
    public final GameSettings setUseRenderChunking(boolean b){
    	this.enable_chunks = b;
    	return this;
    }
    
    
    /**
     * Returns the anisotropic filtering level
     *
     * @return Float Anisotropic filtering level
     */
    public final float getAnisotropicLevel() {
        return anisotropic;
    }

    /**
     * Sets the anisotropic filtering level
     *
     * @param anisotropic Float Anisotropic filtering level
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setAnisotropicLevel(float anisotropic) {
        this.anisotropic = anisotropic;
        if (anisotropic > 0) {
            this.mipmap = true;
        }
        return this;
    }

    /**
     * Global texture setting.
     * Returns if the edges should be clamped
     *
     * @return <tt>true</tt> if the edges should be clamped
     */
    public final boolean clampEdges() {
        return clampEdges;
    }

    /**
     * Global texture setting.
     * Sets if the edges should be clamped
     *
     * @param clampEdges Boolean If the edges should be clamped
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setClampEdges(boolean clampEdges) {
        this.clampEdges = clampEdges;
        return this;
    }

    /**
     * Returns if mipmapping should be used
     *
     * @return <tt>true</tt> if mipmapping is activated
     */
    public final boolean mipmap() {
        return mipmap;
    }

    /**
     * Global texture setting.
     * Sets if mipmapping should be used
     *
     * @param mipmap Boolean If mipmapping should be activated
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setMipmap(boolean mipmap) {
        this.mipmap = mipmap;
        return this;
    }

    /**
     * Global texture setting.
     * @see #setFilterNearest(boolean)
     * @return
     */
    public final boolean filterNearest() {
        return nearest;
    }

    /**
     * Global texture setting. overrides global mipmapping to false if nearest gets activated.
     * @see #setMipmap(boolean)
     * @param nearest
     * @return this GameSettings
     */
    public final GameSettings setFilterNearest(boolean nearest) {
        if (nearest) {
            mipmap = false;
        }
        this.nearest = nearest;
        return this;
    }

    /**
     * Returns the size of the EventSystem Threadpool
     *
     * @return Integer Threadpool size
     */
    public final int getEventThreadpoolSize() {
        return eventThreadPoolSize;
    }

    /**
     * Sets the size of the EventSystem Threadpool
     *
     * @param eventThreadPoolSize Integer Threadpool size
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setEventThreadpoolSize(int eventThreadPoolSize) {
        this.eventThreadPoolSize = eventThreadPoolSize;
        return this;
    }

    /**
     * Returns the KeySettings
     *
     * @return KeySettings Key settings
     */
    public final KeySettings getKeySettings() {
        return keySettings;
    }

    /**
     * Sets the KeySettings
     *
     * @param keySettings KeySettings Key settings
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setKeySettings(KeySettings keySettings) {
        this.keySettings = keySettings;
        return this;
    }

    /**
     * Sets the FBOFactory for additional rendertargets.
     *
     * @param fbo_factory FBOFactory FBOFactory
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setFBOFactory(FBOFactory fbo_factory) {
        this.fbo_factory = fbo_factory;
        return this;
    }

    /**
     * Returns added FBOs.
     * Called by the engine.
     * @return FrameBufferObject Array FBOs
     */
    public final FrameBufferObject[] getAddFBOs() {
        if (fbo_factory == null) {
            return new FrameBufferObject[]{};
        }
        FrameBufferObject[] fbos = fbo_factory.getAllFBOs();
        if (fbos == null || fbos.length == 0) {
            return new FrameBufferObject[]{};
        } else {
            return fbos;
        }
    }

    /**
     * Sets the radius around the camera where foliage will be rendered.
     * @see RenderType#FOLIAGE
     *
     * @param foliageRadius Float FoliageRadius
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setRadiusFoliage(float foliageRadius) {
        this.foliageRadius = foliageRadius;
        return this;
    }

    /**
     * Returns the foliage radius
     *
     * @return Float Foliage radius
     */
    public final float getRadiusFoliage() {
        return foliageRadius;
    }

    /**
     * Sets the radius around the camera where medium objects will be rendered.
     * @see RenderType#MEDIUM
     * @param mediumRadius Float Medium radius
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setRadiusMedium(float mediumRadius) {
        this.mediumRadius = mediumRadius;
        return this;
    }

    /**
     * Returns the medium radius
     *
     * @return Float Medium radius
     */
    public final float getRadiusMedium() {
        return mediumRadius;
    }

    /**
     * Returns the x chunk offset
     * @see #setUseRenderChunking(boolean)
     * @return Long X chunk offset
     */
    public final long getChunkRenderOffsetX() {
        return chunkOffsetX;
    }

    /**
     * Returns the y chunk offset
     * @see #setUseRenderChunking(boolean)
     * @return Long Y chunk offset
     */
    public final long getChunkRenderOffsetY() {
        return chunkOffsetY;
    }

    /**
     * Returns the z chunk offset
     * @see #setUseRenderChunking(boolean)
     * @return Long Z chunk offset
     */
    public final long getChunkRenderOffsetZ() {
        return chunkOffsetZ;
    }

    /**
     * Sets the x chunk offset
     * @see #setUseRenderChunking(boolean)
     * @param o Long X chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetX(long o) {
        return setChunkRenderOffsets(o, chunkOffsetY, chunkOffsetZ);
    }

    /**
     * Sets the y chunk offset
     * @see #setUseRenderChunking(boolean)
     * @param o Long Y chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetY(long o) {
        return setChunkRenderOffsets(chunkOffsetX, o, chunkOffsetZ);
    }

    /**
     * Sets the z chunk offset
     * @see #setUseRenderChunking(boolean)
     * @param o Long Z chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetZ(long o) {
        return setChunkRenderOffsets(chunkOffsetX, chunkOffsetY, o);
    }

    /**
     * Sets the ChunkRenderOffsets (max distance to the camera where a chunk will be rendererd)
     * 
     * @see #setUseRenderChunking(boolean)
     * @param xo Long X chunk offset
     * @param yo Long Y chunk offset
     * @param zo Long Z chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsets(long xo, long yo, long zo) {
        this.chunkOffsetX = xo;
        this.chunkOffsetY = yo;
        this.chunkOffsetZ = zo;
        return this;
    }

    /**
     * Returns the chunk width
     * @see #setChunkSize(int, int, int)
     * @return Integer Chunk width
     */
    public final int getChunkWidth() {
        return chunkWidth;
    }

    /**
     * Returns the chunk height
     * @see #setChunkSize(int, int, int)
     * @return Integer Chunk height
     */
    public final int getChunkHeight() {
        return chunkHeight;
    }

    /**
     * Returns the chunk depth
     * @see #setChunkSize(int, int, int)
     * @return Integer Chunk depth
     */
    public final int getChunkDepth() {
        return chunkDepth;
    }

    /**
     * Sets the chunk size of the RenderChunks.
     * @see #setUseRenderChunking(boolean)
     *
     * @param w Integer Width
     * @param h Integer Height
     * @param d Integer Depth
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkSize(int w, int h, int d) {
        chunkWidth = w;
        chunkHeight = h;
        chunkDepth = d;
        return this;
    }

    /**
     * Sets the chunk width
     * @see #setChunkSize(int, int, int)
     * @param i Integer Width
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkWidth(int i) {
        return setChunkSize(i, chunkHeight, chunkDepth);
    }

    /**
     * Sets the chunk height
     * @see #setChunkSize(int, int, int)
     * @param i Integer Height
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkHeight(int i) {
        return setChunkSize(chunkWidth, i, chunkDepth);
    }

    /**
     * Sets the chunk depth
     * @see #setChunkSize(int, int, int)
     * @param i Integer Depth
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkDepth(int i) {
        return setChunkSize(chunkWidth, chunkHeight, i);
    }

    /**
     * returns the big radius.
     * @return
     */
    public float getRadiusBig() {
        return bigRadius;
    }

    /**
     * Sets the radius around the camera where medium objects will be rendered.
     * @see RenderType#BIG
     * @param Float the radius
     * @return this GameSettings
     */
    public GameSettings setRadiusBig(float r) {
        this.bigRadius = r;
        return this;
    }

    /**
     * sets if forwrd light rendering is enabled.
     * @param boolean
     * @return this GameSettings
     */
    public GameSettings setLightForward(boolean b) {
        this.forwardlight = b;
        return this;
    }

    /**
     * is forward light rendering enabled?
     * @return
     */
    public boolean isLightForwardAllowed() {
        return forwardlight;
    }

    /**
     * sets if deferred light rendering is enabled.
     * @param boolean
     * @return this GameSettings
     */
    public GameSettings setLightDeferred(boolean b) {
        this.deferredlight = b;
        return this;
    }

    /**
     * is deferred light rendering enabled?
     * @return
     */
    public boolean isLightDeferredAllowed() {
        return deferredlight;
    }

    /**
     * sets the amount of lights that can be rendered with forward light rendering in a frame. must be called before the engine boots.
     * @see #setLightForward(boolean)
     * @param int
     * @return this GameSettings
     */
    public GameSettings setLightMaxForward(int i) {
        this.max_forward_lights = i;
        return this;
    }

    /**
     * max allowed forward lights per frame.
     * @return int
     */
    public int getLightMaxForward() {
        return max_forward_lights;
    }

    /**
     * max instances per drawcall for instanced rendering.
     * @return int 
     */
	public int getMaxInstancesPerDrawcall() {
		return maxinstancespdc;
	}
	
	/**
	 * sets how many Entitys can be drawn in one rendercall then using instanced rendering.
	 * @param i
	 * @return this GameSettings
	 */
	public GameSettings setMaxInstancesPerDrawcall(int i){
		this.maxinstancespdc = i;
		return this;
	}

	/**
	 * is frustrum culling enabled?
	 * @return
	 */
	public boolean useFrustrumCulling() {
		return frustrumCulling;
	}
	
	/**
	 * sets if frustrum culling is enabled.
	 * @param b
	 * @return
	 */
	public GameSettings setUseFrustrumCulling(boolean b){
		this.frustrumCulling = b;
		return this;
	}
}
