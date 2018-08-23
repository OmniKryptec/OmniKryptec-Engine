package de.omnikryptec.util.error;

import java.time.Instant;

public class NameItem implements ErrorItem{

	private Instant instant = Instant.now();
	private String name;
	
	public NameItem(){
		this("ERROR-REPORT");
	}
	
	public NameItem(String s){
		this.name = s;
	}
	
	@Override
	public String getError() {
		return name+"\n"+instant.toString();
	}

}
