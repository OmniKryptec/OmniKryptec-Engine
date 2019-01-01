package de.omnikryptec.libapi.exposed.render;

import java.util.EnumMap;
import java.util.Map;

public class RenderState {
    
    public static enum BlendMode {
        ADDITIVE, ALPHA, MULTIPLICATIVE, OFF;
    }
    
    public static enum CullMode {
        BACK, FRONT, OFF;
    }
    
    public static enum DepthMode {
        LESS, EQUAL, GREATER, ALWAYS, NEVER;
    }
    
    public static enum RenderConfig {
        BLEND, DEPTH_TEST, CULL_FACES, MULTISAMPLE, WRITE_DEPTH, WRITE_COLOR;
    }
    
    public static enum PolyMode {
        FILL, LINE, POINT;
    }
    
    private BlendMode blendMode;
    private CullMode cullMode;
    private DepthMode depthMode;
    private PolyMode polyMode;
    private Map<RenderConfig, Boolean> renderConfig = new EnumMap<>(RenderConfig.class);
    
    public boolean isEnable(RenderConfig opt) {
        //Null-values should be false
        return renderConfig.get(opt);
    }
    
    public BlendMode getBlendMode() {
        return blendMode;
    }
    
    public CullMode getCullMode() {
        return cullMode;
    }
    
    public DepthMode getDepthMode() {
        return depthMode;
    }
    
    public PolyMode getPolyMode() {
        return polyMode;
    }
    
}
