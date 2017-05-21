package omnikryptec.event;

/**
 * 
 * @author pcfreak9000
 *
 */
public final class EventType {
	
	
	
	private String name;
	private boolean needsCurrentThread=false;
	
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
