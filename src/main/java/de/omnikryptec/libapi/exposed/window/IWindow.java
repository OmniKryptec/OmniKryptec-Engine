package de.omnikryptec.libapi.exposed.window;

public interface IWindow {
    
    void setTitle(String title);
    
    boolean isActive();
    
    boolean isFullscreen();
    
    boolean isCloseRequested();
    
    int getWindowWidth();
    
    int getWindowHeight();
    
    void setVSync(boolean vsync);
    
    void setVisible(boolean visible);
    
    void dispose();
    
    //TODO does this belong here or in the ScreenBuffer?
    void swapBuffers();
        
    long getID();
    
    void setWindowSize(int width, int height);
    
    void setFullscreen(boolean b);
}
