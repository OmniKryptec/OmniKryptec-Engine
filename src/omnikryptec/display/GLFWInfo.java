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
	private int width=0,height=0;
	
	
	public GLFWInfo(){
		width = 800;
		height = 600;
		resizeable = false;
	}
	
	/**
	 * Constructs an OpenGLInfo from major/minor version and a PixelFormat
	 * 
	 * @param majVers
	 *            Integer Major version
	 * @param minVers
	 *            Integer Minor version
	 * @param format
	 *            PixelFormat Pixel format
	 */
	public GLFWInfo(int majVers, int minVers) {
		this.majVers = majVers;
		this.minVers = minVers;
	}

	int getMajorVersion(){
		return majVers;
	}
	
	int getMinorVersion(){
		return minVers;
	}
	
	boolean wantsResizeable(){
		return resizeable;
	}
	
	int getWidth(){
		return width;
	}
	
	int getHeight(){
		return height;
	}

}
