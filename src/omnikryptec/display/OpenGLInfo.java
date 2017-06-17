package omnikryptec.display;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.PixelFormat;

/**
 * OpenGLInfo class
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public class OpenGLInfo {

	private int majVers = 3;
	private int minVers = 3;

	private ContextAttribs attribs = null;

	private PixelFormat format = null;

	
	public OpenGLInfo(int majVers, int minVers){
		this(majVers, minVers, new PixelFormat());
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
	public OpenGLInfo(int majVers, int minVers, PixelFormat format) {
		this.majVers = majVers;
		this.minVers = minVers;
		this.format = format;
	}

	/**
	 * Constructs an OpenGLInfo from ContextAttribs and a PixelFormat
	 * 
	 * @param attribs
	 *            ContextAttribs Attributes
	 * @param format
	 *            PixelFormat Pixel format
	 */
	public OpenGLInfo(ContextAttribs attribs, PixelFormat format) {
		this.attribs = attribs;
		this.format = format;
	}

	/**
	 * Constructs an empty OpenGLInfo
	 */
	public OpenGLInfo() {
		this(null, null);
	}

	/**
	 * Returns the context attributes
	 * 
	 * @return ContextAttribs Context attributes
	 */
	public final ContextAttribs getAttribs() {
		if (attribs == null) {
			ContextAttribs attr;
			if (majVers < 3 && minVers < 2) {
				attr = new ContextAttribs();
			} else {
				attr = new ContextAttribs(majVers, minVers);
				attr.withProfileCore(true);
			}
			return (attribs = attr.withForwardCompatible(true));
		} else {
			return attribs;
		}
	}

	/**
	 * Returns the PixelFormat
	 * 
	 * @return PixelFormat Pixel format
	 */
	public final PixelFormat getPixelFormat() {
		if (format == null) {
			return (format = new PixelFormat());
		} else {
			return format;
		}
	}

}
