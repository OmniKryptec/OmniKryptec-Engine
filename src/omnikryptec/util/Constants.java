package omnikryptec.util;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.settings.GameSettings;
import omnikryptec.shader.Shader;

public class Constants {
	
	public static final int DISPLAYMANAGER_DISABLE_FPS_CAP=DisplayManager.DISABLE_FPS_CAP;
	public static final int GAMESETTINGS_NO_MULTISAMPLING=GameSettings.NO_MULTISAMPLING;
	
	public static final Vector3f MATHS_X_AXIS = Maths.X;
	public static final Vector3f MATHS_Y_AXIS = Maths.Y;
	public static final Vector3f MATHS_Z_AXIS = Maths.Z;
	public static final Vector3f MATHS_ZERO = Maths.ZERO;
	public static final Vector3f MATHS_ONE = Maths.ONE;
	
	public static final String SHADER_DEFAULT_PP_VERTEX_SHADER_LOC = Shader.DEFAULT_PP_VERTEX_SHADER_LOC;
        
    public static final javax.vecmath.Vector3f VECTOR_ZERO = new javax.vecmath.Vector3f(0, 0, 0);
    public static final javax.vecmath.Vector3f GRAVITY_EARTH = new javax.vecmath.Vector3f(0, -9.81F, 0);

}
