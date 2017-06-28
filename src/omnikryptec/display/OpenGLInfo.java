package omnikryptec.display;

/**
 * OpenGLInfo class
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public class OpenGLInfo {

	private int majVers = 3;
	private int minVers = 3;
	private boolean resizeable;

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
	public OpenGLInfo(int majVers, int minVers) {
		this.majVers = majVers;
		this.minVers = minVers;
	}

	int getMajorVersion(){
		return majVers;
	}
	
	int getMinorVersion(){
		return minVers;
	}
	

}
