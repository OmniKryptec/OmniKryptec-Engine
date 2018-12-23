package de.omnikryptec.resource;

public class TextureConfig {
    
    public static enum WrappingMode {
        ClampToEdge, Repeat
    }
    
    public static enum MagMinFilter {
        Nearest, Linear
    }
    
    private float anisotropic;
    private boolean mipmap;
    
    private WrappingMode wrappingMode;
    private MagMinFilter minFilter;
    private MagMinFilter magFilter;
    
    public float anisotropicValue() {
        return anisotropic;
    }
    
    public boolean mipmap() {
        return mipmap;
    }
    
    public WrappingMode wrappingMode() {
        return wrappingMode;
    }
    
    public MagMinFilter minFilter() {
        return minFilter;
    }
    
    public MagMinFilter magFilter() {
        return magFilter;
    }
    
    public TextureConfig anisotropic(float value) {
        this.anisotropic = value;
        return this;
    }
    
    public TextureConfig mipmap(boolean value) {
        this.mipmap = value;
        return this;
    }
    
    public TextureConfig wrappingMode(WrappingMode mode) {
        this.wrappingMode = mode;
        return this;
    }
    
    public TextureConfig minFilter(MagMinFilter filter) {
        this.minFilter = filter;
        return this;
    }
    
    public TextureConfig magFilter(MagMinFilter filter) {
        this.magFilter = filter;
        return this;
    }
}
