package omnikryptec.event;

/**
 * 
 * @author pcfreak9000
 *
 */
public final class EventType {
	
	public static final EventType BOOTING_COMPLETED = new EventType("booting_completed");



	public static final EventType ERROR = new EventType("error");



	public static final EventType RESIZED = new EventType("resized", true);



	public static final EventType FRAME_EVENT = new EventType("frame");
	
	/**
	 * like the frame event but in the renderthread for renderingpurposes
	 */
	public static final EventType RENDER_EVENT = new EventType("render_frame", true);
	
	private final String name;
	private final boolean needsCurrentThread;
	
	public EventType(String name, boolean needsCurrentThread){
		this.name = name;
		this.needsCurrentThread = needsCurrentThread;
	}
	
	public EventType(String name){
		this(name, false);
	}
	
	public String getName(){
		return name;
	}
	
	public boolean executeInCurrentThread(){
		return needsCurrentThread;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof EventType){
			if(((EventType)o).needsCurrentThread==needsCurrentThread){
				if(((EventType)o).name.equals(name)){
					return true;
				}
			}
		}
		return false;
	}
}
