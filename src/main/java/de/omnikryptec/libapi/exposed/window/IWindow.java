package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;

public interface IWindow {
    
    boolean isActive();
    
    boolean isFullscreen();
    
    boolean isCloseRequested();
    
    int getWindowWidth();
    
    int getWindowHeight();
    
    FrameBuffer getDefaultFrameBuffer();
    
    void setVSync(boolean vsync);
    
    void setVisible(boolean visible);
    
    void dispose();
    
    void swapBuffers();
        
    long getID();
    
}
