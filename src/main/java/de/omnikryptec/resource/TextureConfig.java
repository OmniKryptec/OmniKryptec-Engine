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
        return this.anisotropic;
    }

    public boolean mipmap() {
        return this.mipmap;
    }

    public WrappingMode wrappingMode() {
        return this.wrappingMode;
    }

    public MagMinFilter minFilter() {
        return this.minFilter;
    }

    public MagMinFilter magFilter() {
        return this.magFilter;
    }

    public TextureConfig anisotropic(final float value) {
        this.anisotropic = value;
        return this;
    }

    public TextureConfig mipmap(final boolean value) {
        this.mipmap = value;
        return this;
    }

    public TextureConfig wrappingMode(final WrappingMode mode) {
        this.wrappingMode = mode;
        return this;
    }

    public TextureConfig minFilter(final MagMinFilter filter) {
        this.minFilter = filter;
        return this;
    }

    public TextureConfig magFilter(final MagMinFilter filter) {
        this.magFilter = filter;
        return this;
    }
}
