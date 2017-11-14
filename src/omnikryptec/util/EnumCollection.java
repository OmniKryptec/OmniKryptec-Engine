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
	
	public static enum GameState {
		STARTING, RUNNING, ERROR, STOPPING, STOPPED;
	}
	
	public static enum GameLoopShutdown {
		ENGINE(2), LOOP(1), NOT_NOW(0);
		
		private final int lvl;
		
		private GameLoopShutdown(int l) {
			lvl = l;
		}
		
		public int getLvl() {
			return lvl;
		}
	}
}
