package omnikryptec.display;

/**
 * OpenGLInfo class
 *
 * @author pcfreak9000 &amp; Panzer1119
 */
public class GLFWInfo {

    private int majVers = 3;
    private int minVers = 3;
    private boolean resizeable;
    private int width = 0, height = 0;
    private boolean fullscreen = false;

    public GLFWInfo() {
        this(800, 600);
    }

    public GLFWInfo(int width, int height) {
        this(false, width, height);
    }

    public GLFWInfo(boolean fullscreen, int width, int height) {
        this(true, fullscreen, width, height);
    }

    public GLFWInfo(boolean resizeable, boolean fullscreen, int width, int height) {
        this(3, 3, resizeable, fullscreen, width, height);
    }

    /**
     * Constructs an OpenGLInfo from major/minor version and a PixelFormat
     *
     * @param majVers Integer Major version
     * @param minVers Integer Minor version
     * @param fullscreen Boolean Fullscreen
     * @param height Integer Height
     * @param resizeable Boolean Resizeable
     * @param width Integer Width
     */
    public GLFWInfo(int majVers, int minVers, boolean resizeable, boolean fullscreen, int width, int height) {
        this.majVers = majVers;
        this.minVers = minVers;
        this.resizeable = resizeable;
        this.fullscreen = fullscreen;
        this.width = width;
        this.height = height;
    }

    int getMajorVersion() {
        return majVers;
    }

    int getMinorVersion() {
        return minVers;
    }

    boolean wantsResizeable() {
        return resizeable;
    }

    boolean wantsFullscreen() {
        return fullscreen;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

}
