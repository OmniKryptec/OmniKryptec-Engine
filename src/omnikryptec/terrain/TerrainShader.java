package omnikryptec.terrain;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;

/**
 *
 * @author Panzer1119
 */
public class TerrainShader extends Shader {
    
    private static final String SHADER_FOLDER = "/omnikryptec/terrain/"; //TODO Move?

    /*VertexShader*/
    public static final UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public static final UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    public static final UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public static final UniformVec3 lightPosition = new UniformVec3("lightPosition");
    
    /*FragmentShader*/
    public static final UniformSampler modelTexture = new UniformSampler("modelTexture");
    public static final UniformVec3 lightColour = new UniformVec3("lightColour");
    public static final UniformFloat shineDamper = new UniformFloat("shineDamper");
    public static final UniformFloat reflectivity = new UniformFloat("reflectivity");
    
    //public static final UniformBoolean hasspecular = new UniformBoolean("hasspecular");
    //public static final UniformSampler normalmap = new UniformSampler("normaltex");
    //public static final UniformSampler specularmap = new UniformSampler("speculartex");
    
    public TerrainShader() {
        super(TerrainShader.class.getResourceAsStream(SHADER_FOLDER + "terrainVertexShader.txt"), TerrainShader.class.getResourceAsStream(SHADER_FOLDER + "terrainFragmentShader.txt"), "pos", "texcoords", "normal", "tangent", transformationMatrix, projectionMatrix, viewMatrix, lightPosition, modelTexture, lightColour, shineDamper, reflectivity);
        start();
        modelTexture.loadTexUnit(0);
        //normalmap.loadTexUnit(1);
        //specularmap.loadTexUnit(2);
    }
    
}
