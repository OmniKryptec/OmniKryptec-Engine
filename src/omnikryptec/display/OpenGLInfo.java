package omnikryptec.display;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.PixelFormat;

/**
 * 
 * @author pcfreak9000
 */
public class OpenGLInfo {

	private int majVers = 3;
	private int minVers = 3;

	private ContextAttribs attribs = null;

	private PixelFormat format = null;

	public OpenGLInfo(int majVers, int minVers, PixelFormat format) {
		this.majVers = majVers;
		this.minVers = minVers;
		this.format = format;
	}

	public OpenGLInfo(ContextAttribs attribs, PixelFormat format) {
		this.attribs = attribs;
		this.format = format;
	}

	public OpenGLInfo() {
		this(null, null);
	}

	public ContextAttribs getAttribs() {
		if (attribs == null) {
			return new ContextAttribs(majVers, minVers).withForwardCompatible(true).withProfileCore(true);
		} else {
			return attribs;
		}
	}

	public PixelFormat getPixelFormat() {
		if (format == null) {
			return new PixelFormat();
		} else {
			return format;
		}
	}

}
