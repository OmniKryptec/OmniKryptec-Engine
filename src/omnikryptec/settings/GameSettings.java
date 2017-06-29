package omnikryptec.settings;

import omnikryptec.postprocessing.FBOFactory;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.RenderTarget;

/**
 * Game settings object
 *
 * @author pcfreak9000 &amp; Panzer1119
 */
public class GameSettings {

    private int initialFPSCap = -1;
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
    
    private boolean enable_chunks=true;
    private int maxinstancespdc = 10000;
    
    
    /**
     * Standard value for disabling multisampling
     */
    public static final int NO_MULTISAMPLING = 0;

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
     * Sets the number of multisamples, {@value #NO_MULTISAMPLING} means
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
     * Returns the number of multisamples
     *
     * @return Integer Number of multisamples
     */
    public final int getMultiSamples() {
        return multisamples;
    }

    public final boolean usesRenderChunking(){
    	return enable_chunks;
    }
    
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
     * Returns if the edges should be clamped
     *
     * @return <tt>true</tt> if the edges should be clamped
     */
    public final boolean clampEdges() {
        return clampEdges;
    }

    /**
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
     * Returns if the nearest objects(?) should be filtered
     *
     * @return <tt>true</tt> if the nearest objects(?) should be filtered
     */
    public final boolean filterNearest() {
        return nearest;
    }

    /**
     * Sets if the nearest objects(?) should be filtered
     *
     * @param nearest Boolean if the nearest objects(?) should be filtered
     * @return GameSettings A reference to this GameSettings
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
     * Sets the FBOFactory
     *
     * @param fbo_factory FBOFactory FBOFactory
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setFBOFactory(FBOFactory fbo_factory) {
        this.fbo_factory = fbo_factory;
        return this;
    }

    /**
     * Returns added FBOs
     *
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
     * Sets the foliage radius
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
     * Sets the medium radius
     *
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
     *
     * @return Long X chunk offset
     */
    public final long getChunkRenderOffsetX() {
        return chunkOffsetX;
    }

    /**
     * Returns the y chunk offset
     *
     * @return Long Y chunk offset
     */
    public final long getChunkRenderOffsetY() {
        return chunkOffsetY;
    }

    /**
     * Returns the z chunk offset
     *
     * @return Long Z chunk offset
     */
    public final long getChunkRenderOffsetZ() {
        return chunkOffsetZ;
    }

    /**
     * Sets the x chunk offset
     *
     * @param o Long X chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetX(long o) {
        return setChunkRenderOffsets(o, chunkOffsetY, chunkOffsetZ);
    }

    /**
     * Sets the y chunk offset
     *
     * @param o Long Y chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetY(long o) {
        return setChunkRenderOffsets(chunkOffsetX, o, chunkOffsetZ);
    }

    /**
     * Sets the z chunk offset
     *
     * @param o Long Z chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetZ(long o) {
        return setChunkRenderOffsets(chunkOffsetX, chunkOffsetY, o);
    }

    /**
     * Sets the chunk offsets
     *
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
     *
     * @return Integer Chunk width
     */
    public final int getChunkWidth() {
        return chunkWidth;
    }

    /**
     * Returns the chunk height
     *
     * @return Integer Chunk height
     */
    public final int getChunkHeight() {
        return chunkHeight;
    }

    /**
     * Returns the chunk depth
     *
     * @return Integer Chunk depth
     */
    public final int getChunkDepth() {
        return chunkDepth;
    }

    /**
     * Sets the chunk size
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
     *
     * @param i Integer Width
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkWidth(int i) {
        return setChunkSize(i, chunkHeight, chunkDepth);
    }

    /**
     * Sets the chunk height
     *
     * @param i Integer Height
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkHeight(int i) {
        return setChunkSize(chunkWidth, i, chunkDepth);
    }

    /**
     * Sets the chunk depth
     *
     * @param i Integer Depth
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkDepth(int i) {
        return setChunkSize(chunkWidth, chunkHeight, i);
    }

    public float getRadiusBig() {
        return bigRadius;
    }

    public GameSettings setRadiusBig(float r) {
        this.bigRadius = r;
        return this;
    }

    public GameSettings setLightForward(boolean b) {
        this.forwardlight = b;
        return this;
    }

    public boolean isLightForwardAllowed() {
        return forwardlight;
    }

    public GameSettings setLightDeferred(boolean b) {
        this.deferredlight = b;
        return this;
    }

    public boolean isLightDeferredAllowed() {
        return deferredlight;
    }

    public GameSettings setLightMaxForward(int i) {
        this.max_forward_lights = i;
        return this;
    }

    public int getLightMaxForward() {
        return max_forward_lights;
    }

	public int getMaxInstancesPerDrawcall() {
		return maxinstancespdc;
	}
	
	public GameSettings setMaxInstancesPerDrawcall(int i){
		this.maxinstancespdc = i;
		return this;
	}
	
}
