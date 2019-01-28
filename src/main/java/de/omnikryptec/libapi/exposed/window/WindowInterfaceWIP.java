package de.omnikryptec.libapi.exposed.window;

//TODO replace Window-class
public interface WindowInterfaceWIP {
    
    boolean isActive();
    
    boolean isFullscreen();
    
    boolean isCloseRequested();
    
    int getSurfaceWidth();
    
    int getSurfaceHeight();
    
    int getFrameWidth();
    
    int getFrameHeight();
    
    void setVSync(boolean vsync);
    
    void setVisible(boolean visible);
    
    void dispose();
    
    void swapBuffers();
    
    void refreshViewport();
    
}
