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
	
	public static final String SHADER_DEFAULT_PP_VERTEX_SHADER_LOC = Shader.DEFAULT_PP_VERTEX_SHADER_LOC;

}
