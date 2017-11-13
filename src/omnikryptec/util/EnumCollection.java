package omnikryptec.util;

public class EnumCollection {

	public static enum FrameState {
		NULL, RENDERING, LOGIC;
	}

	public static enum RendererTime {
		PRE, POST;
	}
	
	public static enum UpdateType {
	    DYNAMIC, SEMISTATIC, STATIC;
	}
	
	public static enum RenderType {
	    ALWAYS, MEDIUM, FOLIAGE, BIG;
	}
}
