package omnikryptec.util.error;

public class ThroweableItem implements ErrorItem{
	
	private Throwable t;
	
	public ThroweableItem(Throwable t){
		this.t= t;
	}
	
	@Override
	public String getError() {
		StringBuilder builder = new StringBuilder();
		builder.append("ERROR: ").append(t.toString()).append(" AT").append("\n");
		StackTraceElement[] st = t.getStackTrace();
		for(int i=0; i<st.length; i++){
			builder.append(st[i].toString()).append("\n");
		}
		return builder.toString();
	}

}
