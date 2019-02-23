package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;

//TODO replace Window-class with this
public interface WindowInterfaceWIP {

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

    void refreshViewport();

    long getID();
    
}
