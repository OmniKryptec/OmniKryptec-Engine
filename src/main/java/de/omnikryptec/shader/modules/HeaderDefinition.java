package de.omnikryptec.shader.modules;

public class HeaderDefinition {
	final String start,end,inserthere;
	
	public HeaderDefinition(String headersstart, String headersend, String insertheadersafterthisline) {
		this.start = headersstart;
		this.end = headersend;
		this.inserthere = insertheadersafterthisline;
	}
	
}
