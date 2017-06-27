package omnikryptec.util.profiler;

public class ProfileContainer {
	
	private long time;
	private String name;
	
	public ProfileContainer(String name, long time){
		this.time = time;
		this.name = name;
	}
	
	long getTime(){
		return time;
	}
	
	String getName(){
		return name;
	}
	
	
	String getPercentage(long maxtime){
		return new StringBuilder().append(String.format("%.1f", ((float)getTime()/maxtime)*100)).append("%").toString();
	}
	
	String getReletiveTo(long maxtime){
		return new StringBuilder().append(time).append("ms/").append(maxtime).append("ms").toString();
	}
	
	@Override
	public String toString(){
		return getName()+": "+getTime()+"ms";
	}
	
}
