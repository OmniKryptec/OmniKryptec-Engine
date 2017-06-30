package omnikryptec.util.error;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.util.profiler.Profiler;

public class OmnikryptecError implements ErrorItem{
	
	private final ErrorItem[] info;
	private final int limiterlimit;
	
	public OmnikryptecError(ErrorItem...info){
		this(125, info);
	}
	
	public OmnikryptecError(Throwable t){
		this(new NameItem(), new ThroweableItem(t), new Profiler(), new SystemInfoItem());
	}
	
	public OmnikryptecError(int limiterlimit, ErrorItem...info){
		this.limiterlimit = limiterlimit;
		this.info = info;
	}
	
	public void print(){
		Logger.log(getString(true, false), LogLevel.ERROR);
	}
	
	
	
	public String getString(boolean startwithnewline, boolean endwithnewline){
		StringBuilder builder = new StringBuilder();
		if(startwithnewline){
			builder.append("\n");
		}
		limiter(builder, '#');
		builder.append("\n");
		for(int i=0; i<info.length; i++){
			builder.append(info[i].getError().trim()).append("\n");
			if(i<info.length-1){
				builder.append("\n");
			}
		}
		limiter(builder, '#');
		if(endwithnewline){
			builder.append("\n");
		}
		return builder.toString();
	}
	
	
	private void limiter(StringBuilder builder, char c){
		for(int i=0; i<limiterlimit; i++){
			builder.append(c);
		}
	}

	@Override
	public String getError() {
		return getString(false, false);
	}
	
}
