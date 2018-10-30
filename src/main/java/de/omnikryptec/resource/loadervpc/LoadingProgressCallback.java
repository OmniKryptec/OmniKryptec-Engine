package de.omnikryptec.resource.loadervpc;

public interface LoadingProgressCallback {

    void onLoadingStart(int max);

    void onProgressChange(int processed);

    void onLoadingDone();
}
