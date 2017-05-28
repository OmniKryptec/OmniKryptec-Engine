package omnikryptec.terrain;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;

/**
 *
 * @author Panzer1119
 */
public class TerrainShader extends Shader {
    
    private static final String SHADER_FOLDER = "/omnikryptec/terrain/";

    /*VertexShader Uniforms*/
    public static final UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public static final UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    public static final UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    
    /*FragmentShader Uniforms*/
    public static final UniformSampler modelTexture = new UniformSampler("modelTexture");
    
    public TerrainShader() {
        super(TerrainShader.class.getResourceAsStream(SHADER_FOLDER + "terrainVertexShader.txt"), TerrainShader.class.getResourceAsStream(SHADER_FOLDER + "terrainFragmentShader.txt"), "position", "textureCoordinates", "normal", "tangents", transformationMatrix, projectionMatrix, viewMatrix, modelTexture);
        start();
        modelTexture.loadTexUnit(0);
    }
    
}
