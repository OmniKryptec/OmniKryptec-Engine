package de.omnikryptec.libapi.exposed.window;

public interface IWindow {
    
    boolean isActive();
    
    boolean isFullscreen();
    
    boolean isCloseRequested();
    
    int getWindowWidth();
    
    int getWindowHeight();
    
    SurfaceBuffer getDefaultFrameBuffer();
    
    void setVSync(boolean vsync);
    
    void setVisible(boolean visible);
    
    void dispose();
    
    void swapBuffers();
        
    long getID();
    
}
