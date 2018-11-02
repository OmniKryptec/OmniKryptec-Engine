package de.omnikryptec.core;

public interface IEngineLoop {

    default void init(EngineLoader loader) {
    }

    void startLoop();

    void stopLoop();
    
    void update();

    void renderAndSwap();

    boolean isRunning();
}
