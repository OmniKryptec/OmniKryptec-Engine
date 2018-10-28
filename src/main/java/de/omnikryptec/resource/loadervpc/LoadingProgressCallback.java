package de.omnikryptec.resource.loadervpc;

public interface LoadingProgressCallback {
	
	void onProgressChange(int processed, int all);
	
}
