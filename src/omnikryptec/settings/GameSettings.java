package omnikryptec.settings;

import java.util.HashMap;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.postprocessing.main.FBOFactory;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.RenderTarget;
import omnikryptec.util.EnumCollection.RenderType;
import omnikryptec.util.Returner;

/**
 * GameSettings
 *
 * @author pcfreak9000 &amp; Panzer1119
 */
public class GameSettings {

    /**
     * Standard value for disabling multisampling
     */
    public static final int NO_MULTISAMPLING = 0;

    /**
     * OpenGL int
     */
    public static final String COLORSPACE_SCENE_FBO = "COLORSPACE_SCENE_FBO";
    /**
     * OpenGL int
     */
    public static final String COLORSPACE_NORMAL_FBO = "COLORSPACE_NORMAL_FBO";
    /**
     * OpenGL int
     */
    public static final String COLORSPACE_SPECULAR_FBO = "COLORSPACE_SPECULAR_FBO";
    /**
     * OpenGL int
     */
    public static final String COLORSPACE_SHADER_INFO_FBO = "COLORSPACE_SHADER_INFO_FBO";

    /**
     * int
     */
    public static final String FPS_CAP = "FPS_CAP";

    /**
     * Global texture setting. If mipmapping should be used. boolean
     */
    public static final String MIPMAP = "MIPMAP";
    /**
     * Global texture setting. If edges should be clamped. boolean
     */
    public static final String CLAMP_EDGES = "CLAMP_EDGES";

    /**
     * Size of the EventSystem threadpool. int
     */
    public static final String THREADPOOLSIZE_EVENT = "THREADPOOLSIZE_EVENT";

    /**
     * Sets the radius around the camera where foliage will be rendered. float
     *
     * @see RenderType#FOLIAGE
     */
    public static final String RADIUS_FOLIAGE = "RADIUS_FOLIAGE";
    /**
     * Sets the radius around the camera where medium objetcs will be renderer.
     * float
     *
     * @see RenderType#MEDIUM
     */
    public static final String RADIUS_MEDIUM = "RADIUS_MEDIUM";
    /**
     * Sets the radius around the camera where big objetcs will be renderer.
     * float
     *
     * @see RenderType#BIG
     */
    public static final String RADIUS_BIG = "RADIUS_BIG";
    /**
     * int
     */
    public static final String HIGHEST_SHADER_LVL = "HIGHEST_SHADER_LVL";

    /**
     * set if the engine is not booted yet; otherwise it will not have any
     * effect.
     *
     * int
     */
    public static final String ANIMATION_MAX_JOINTS = "ANIMATION_MAX_JOINTS";
    /**
     * set if the engine is not booted yet; otherwise it will not have any
     * effect.
     *
     * int
     */
    public static final String ANIMATION_MAX_WEIGHTS = "ANIMATION_MAX_WEIGHTS";
    /**
     * set if the engine is not booted yet; otherwise it will not have any
     * effect.
     *
     * int
     */
    public static final String MAX_FORWARD_LIGHTS = "MAX_FORWARD_LIGHTS";

    /**
     * set if the engine is not booted yet; otherwise it will not have any
     * effect.
     *
     * float
     */
    public static final String Z_OFFSET_2D = "Z_OFFSET_2D";

    /**
     * set if the engine is not booted yet; otherwise it will not have any
     * effect.
     *
     * boolean
     */
    public static final String FASTMATH = "FASTMATH";

    /**
     * int
     */
    public static final String CHUNK_WIDTH_2D = "CHUNK_WIDTH_2D";

    /**
     * int
     */
    public static final String CHUNK_HEIGHT_2D = "CHUNK_HEIGHT_2D";

    /**
     * boolean
     */
    public static final String SET_CHUNK_SIZE_2D_AS_DISPLAYSIZE = "SET_CHUNK_SIZE_2D_AS_DISPLAYSIZE";

    /**
     * boolean
     */
    public static final String LIGHT_2D = "LIGHT_2D";

    /**
     * long
     */
    public static final String CHUNK_OFFSET_2D_X = "CHUNK_OFFSET_2D_X";

    /**
     * long
     */
    public static final String CHUNK_OFFSET_2D_Y = "CHUNK_OFFSET_2D_Y";

    private final HashMap<String, Object> settings_objects = new HashMap<>();
    private final HashMap<String, Float> settings_floats = new HashMap<>();
    private final HashMap<String, Integer> settings_integers = new HashMap<>();
    private final HashMap<String, Long> settings_longs = new HashMap<>();
    private final HashMap<String, Boolean> settings_booleans = new HashMap<>();

    // ************************************Not just
    // settings****************************************
    /**
     * 0 means no multisampling
     */
    private int multisamples = NO_MULTISAMPLING;
    private float anisotropic = 0;
    private KeySettings keySettings;
    private boolean nearest = false;

    // *********************************************************************************************
    private RenderTarget[] add_attachments = {};
    private FBOFactory fbo_factory;

    private long chunkOffsetX = 1;
    private long chunkOffsetY = 1;
    private long chunkOffsetZ = 1;
    private int chunkWidth = 128;
    private int chunkHeight = 128;
    private int chunkDepth = 128;

    private boolean deferredlight = false;
    private boolean forwardlight = false;

    private boolean enable_chunks = false;
    private int maxinstancespdc = 10000;

    private boolean frustrumCulling = true;

    private boolean multithreadedParticles = true;
    private long minmultithreadedparticles = 1000;
    private int partThrPSize = -1;

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
        fillDefaults();
    }

    public GameSettings fillDefaults() {
        /* COLORSPACE */
        setInteger(COLORSPACE_NORMAL_FBO, GL11.GL_RGBA8);
        setInteger(COLORSPACE_SCENE_FBO, GL11.GL_RGBA8);
        setInteger(COLORSPACE_SHADER_INFO_FBO, GL30.GL_RGBA32F);
        setInteger(COLORSPACE_SPECULAR_FBO, GL30.GL_RGBA32F);
        /**/
        setInteger(FPS_CAP, DisplayManager.DISABLE_FPS_CAP);
        setBoolean(CLAMP_EDGES, false);
        setBoolean(MIPMAP, false);
        setInteger(THREADPOOLSIZE_EVENT, 2);

        setFloat(RADIUS_FOLIAGE, 50);
        setFloat(RADIUS_MEDIUM, 100);
        setFloat(RADIUS_BIG, 200);
        setInteger(CHUNK_WIDTH_2D, 512);
        setInteger(CHUNK_HEIGHT_2D, 512);
        setBoolean(SET_CHUNK_SIZE_2D_AS_DISPLAYSIZE, false);

        setInteger(HIGHEST_SHADER_LVL, 10);
        setInteger(MAX_FORWARD_LIGHTS, 4);
        /* ANIMATION */
        setInteger(ANIMATION_MAX_JOINTS, 50);
        setInteger(ANIMATION_MAX_WEIGHTS, 3);
        /*SPECIAL*/
        setFloat(Z_OFFSET_2D, 1f);
        setBoolean(FASTMATH, true);
        setBoolean(LIGHT_2D, true);
        setLong(CHUNK_OFFSET_2D_X, 1);
        setLong(CHUNK_OFFSET_2D_Y, 1);
        return this;
    }

    public final void setChunksize2DasDisplaySize() {
        setInteger(CHUNK_WIDTH_2D, Display.getWidth());
        setInteger(CHUNK_HEIGHT_2D, Display.getHeight());
    }

    /**
     * Returns an Object or null for a key
     *
     * @param key Key
     * @return Object
     */
    public final Object getObject(String key) {
        return settings_objects.get(key);
    }

    /**
     * Returns an Float or 0F for a key
     *
     * @param key Key
     * @return Float
     */
    public final float getFloat(String key) {
        return Returner.of(settings_floats.get(key)).or(0F);
    }

    /**
     * Returns an Integer or 0 for a key
     *
     * @param key Key
     * @return Integer
     */
    public final int getInteger(String key) {
        return Returner.of(settings_integers.get(key)).or(0);
    }

    /**
     * Returns a Long or 0 for a key
     *
     * @param key Key
     * @return long
     */
    public final long getLong(String key) {
        return Returner.of(settings_longs.get(key)).or(0L);
    }

    /**
     * Returns a boolean or 0 for a key
     *
     * @param key Key
     * @return boolean
     */
    public final boolean getBoolean(String key) {
        return Returner.of(settings_booleans.get(key)).or(false);
    }

    /**
     * Sets an Object for a key
     *
     * @param key Key
     * @param value Value
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setObject(String key, Object value) {
        settings_objects.put(key, value);
        return this;
    }

    /**
     * Sets an Float for a key
     *
     * @param key Key
     * @param value Value
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setFloat(String key, float value) {
        settings_floats.put(key, value);
        return this;
    }

    /**
     * Sets an Integer for a key
     *
     * @param key Key
     * @param value Value
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setInteger(String key, int value) {
        settings_integers.put(key, value);
        return this;
    }

    /**
     * Sets a Long for a key
     *
     * @param key Key
     * @param value Value
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setLong(String key, long value) {
        settings_longs.put(key, value);
        return this;
    }

    /**
     * Sets a boolean for a key
     *
     * @param key Key
     * @param value Value
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setBoolean(String key, boolean value) {
        settings_booleans.put(key, value);
        return this;
    }

    /**
     * Returns if an Object for a key is present
     *
     * @param key Key
     * @return <tt>true</tt> if the given key is holding a value
     */
    public final boolean hasObject(String key) {
        return settings_objects.containsKey(key);
    }

    /**
     * Returns if an Float for a key is present
     *
     * @param key Key
     * @return <tt>true</tt> if the given key is holding a value
     */
    public final boolean hasFloat(String key) {
        return settings_floats.containsKey(key);
    }

    /**
     * Returns if an Integer for a key is present
     *
     * @param key Key
     * @return <tt>true</tt> if the given key is holding a value
     */
    public final boolean hasInteger(String key) {
        return settings_integers.containsKey(key);
    }

    /**
     * Returns if a Long for a key is present
     *
     * @param key Key
     * @return <tt>true</tt> if the given key is holding a value
     */
    public final boolean hasLong(String key) {
        return settings_longs.containsKey(key);
    }

    /**
     * Returns if a boolean for a key is present
     *
     * @param key Key
     * @return <tt>true</tt> if the given key is holding a value
     */
    public final boolean hasBoolean(String key) {
        return settings_booleans.containsKey(key);
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
     * Sets the number of multisamples, {@link #NO_MULTISAMPLING} means
     * multisampling is disabled
     *
     * @param multisamples Integer Number of multisamples
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setMultisamples(int multisamples) {
        this.multisamples = 0;
        return this;
    }

    /**
     * Returns the number of multisamples or NO_MULTISAMPLING if its disabled.
     *
     * @return Integer Number of multisamples
     */
    public final int getMultiSamples() {
        return OmniKryptecEngine.isCreated()
                ? (GL.getCapabilities().GL_EXT_framebuffer_multisample ? multisamples : NO_MULTISAMPLING)
                : multisamples;
    }

    /**
     * Returns if RenderChunking is enabled.
     *
     * @see #setUseRenderChunking(boolean)
     * @return boolean
     */
    public final boolean usesRenderChunking() {
        return enable_chunks;
    }

    /**
     * if activated, gameobjects will be sorted based on their position. only
     * the chunks around the camera will be rendered. can break the use of
     * prioritys for the renderer.
     *
     * @see #setChunkRenderOffsets(long, long, long)
     * @param b
     * @return this GameSettings instance
     */
    public final GameSettings setUseRenderChunking(boolean b) {
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
            setBoolean(MIPMAP, true);
        }
        return this;
    }

    /**
     * Global texture setting.
     *
     * @see #setFilterNearest(boolean)
     * @return
     */
    public final boolean filterNearest() {
        return nearest;
    }

    /**
     * Global texture setting. overrides global mipmapping to false if nearest
     * gets activated.
     *
     * @param nearest
     * @return this GameSettings
     */
    public final GameSettings setFilterNearest(boolean nearest) {
        if (nearest) {
            setBoolean(MIPMAP, false);
        }
        this.nearest = nearest;
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
     * Returns added FBOs. Called by the engine.
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
     * Returns the x chunk offset
     *
     * @see #setUseRenderChunking(boolean)
     * @return Long X chunk offset
     */
    public final long getChunkRenderOffsetX() {
        return chunkOffsetX;
    }

    /**
     * Returns the y chunk offset
     *
     * @see #setUseRenderChunking(boolean)
     * @return Long Y chunk offset
     */
    public final long getChunkRenderOffsetY() {
        return chunkOffsetY;
    }

    /**
     * Returns the z chunk offset
     *
     * @see #setUseRenderChunking(boolean)
     * @return Long Z chunk offset
     */
    public final long getChunkRenderOffsetZ() {
        return chunkOffsetZ;
    }

    /**
     * Sets the x chunk offset
     *
     * @see #setUseRenderChunking(boolean)
     * @param o Long X chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetX(long o) {
        return setChunkRenderOffsets(o, chunkOffsetY, chunkOffsetZ);
    }

    /**
     * Sets the y chunk offset
     *
     * @see #setUseRenderChunking(boolean)
     * @param o Long Y chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetY(long o) {
        return setChunkRenderOffsets(chunkOffsetX, o, chunkOffsetZ);
    }

    /**
     * Sets the z chunk offset
     *
     * @see #setUseRenderChunking(boolean)
     * @param o Long Z chunk offset
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkRenderOffsetZ(long o) {
        return setChunkRenderOffsets(chunkOffsetX, chunkOffsetY, o);
    }

    /**
     * Sets the ChunkRenderOffsets (max distance to the camera where a chunk
     * will be rendererd)
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
     *
     * @see #setChunkSize(int, int, int)
     * @return Integer Chunk width
     */
    public final int getChunkWidth() {
        return chunkWidth;
    }

    /**
     * Returns the chunk height
     *
     * @see #setChunkSize(int, int, int)
     * @return Integer Chunk height
     */
    public final int getChunkHeight() {
        return chunkHeight;
    }

    /**
     * Returns the chunk depth
     *
     * @see #setChunkSize(int, int, int)
     * @return Integer Chunk depth
     */
    public final int getChunkDepth() {
        return chunkDepth;
    }

    /**
     * Sets the chunk size of the RenderChunks.
     *
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
     *
     * @see #setChunkSize(int, int, int)
     * @param i Integer Width
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkWidth(int i) {
        return setChunkSize(i, chunkHeight, chunkDepth);
    }

    /**
     * Sets the chunk height
     *
     * @see #setChunkSize(int, int, int)
     * @param i Integer Height
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkHeight(int i) {
        return setChunkSize(chunkWidth, i, chunkDepth);
    }

    /**
     * Sets the chunk depth
     *
     * @see #setChunkSize(int, int, int)
     * @param i Integer Depth
     * @return GameSettings A reference to this GameSettings
     */
    public final GameSettings setChunkDepth(int i) {
        return setChunkSize(chunkWidth, chunkHeight, i);
    }

    /**
     * sets if forwrd light rendering is enabled.
     *
     * @param b
     * @return this GameSettings
     */
    public GameSettings setLightForward(boolean b) {
        this.forwardlight = b;
        return this;
    }

    /**
     * is forward light rendering enabled?
     *
     * @return
     */
    public boolean isLightForwardAllowed() {
        return forwardlight;
    }

    /**
     * sets if deferred light rendering is enabled.
     *
     * @param b
     * @return this GameSettings
     */
    public GameSettings setLightDeferred(boolean b) {
        this.deferredlight = b;
        return this;
    }

    /**
     * is deferred light rendering enabled?
     *
     * @return
     */
    public boolean isLightDeferredAllowed() {
        return deferredlight;
    }

    /**
     * max instances per drawcall for instanced rendering.
     *
     * @return int
     */
    public int getMaxInstancesPerDrawcall() {
        return maxinstancespdc;
    }

    /**
     * sets how many Entitys can be drawn in one rendercall then using instanced
     * rendering.
     *
     * @param i
     * @return this GameSettings
     */
    public GameSettings setMaxInstancesPerDrawcall(int i) {
        this.maxinstancespdc = i;
        return this;
    }

    /**
     * is frustrum culling enabled?
     *
     * @return
     */
    public boolean useFrustrumCulling() {
        return frustrumCulling;
    }

    /**
     * sets if frustrum culling is enabled.
     *
     * @param b
     * @return
     */
    public GameSettings setUseFrustrumCulling(boolean b) {
        this.frustrumCulling = b;
        return this;
    }

    public boolean isMultithreadedParticles() {
        return this.multithreadedParticles;
    }

    public long getMinMultithreadParticles() {
        return this.minmultithreadedparticles;
    }

    public GameSettings setMultithreadedParticles(boolean b) {
        this.multithreadedParticles = b;
        return this;
    }

    public GameSettings setMinMultithreadedParticles(long l) {
        this.minmultithreadedparticles = l;
        return this;
    }

    public int getParticleThreadpoolSize() {
        return partThrPSize < 0 ? Runtime.getRuntime().availableProcessors() : partThrPSize;
    }

}
