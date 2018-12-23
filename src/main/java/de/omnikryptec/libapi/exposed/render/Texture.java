package de.omnikryptec.libapi.exposed.render;

public interface Texture {

    void bindTexture(int unit);

    int getWidth();
    int getHeight();
    
}
