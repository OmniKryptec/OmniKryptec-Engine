package omnikryptec.gameobject.water;

import omnikryptec.shader.base.Shader;

/**
 * WaterShader
 *
 * @author Panzer1119
 */
public class WaterShader extends Shader {

    private static final String SHADER_FOLDER = "/omnikryptec/gameobject/water/";

    /* VertexShader Uniforms */
    /*    
    public static final UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public static final UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    public static final UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    */
    
    /* FragmentShader Uniforms */
    /*
    public static final UniformSampler backgroundTexture = new UniformSampler("backgroundTexture");
    public static final UniformSampler rTexture = new UniformSampler("rTexture");
    public static final UniformSampler gTexture = new UniformSampler("gTexture");
    public static final UniformSampler bTexture = new UniformSampler("bTexture");
    public static final UniformSampler blendMap = new UniformSampler("blendMap");
    */

    public WaterShader() {
        super(WaterShader.class.getResourceAsStream(SHADER_FOLDER + "waterVertexShader.txt"),
                WaterShader.class.getResourceAsStream(SHADER_FOLDER + "waterFragmentShader.txt")/*, "position",
                "textureCoordinates", "normal", "tangents", transformationMatrix, projectionMatrix, viewMatrix,
                backgroundTexture, rTexture, gTexture, bTexture, blendMap*/);
        start();
        /*
        backgroundTexture.loadTexUnit(0);
        rTexture.loadTexUnit(1);
        gTexture.loadTexUnit(2);
        bTexture.loadTexUnit(3);
        blendMap.loadTexUnit(4);
        */
    }

}
