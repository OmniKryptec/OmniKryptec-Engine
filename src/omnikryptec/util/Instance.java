package omnikryptec.util;

import java.time.format.DateTimeFormatter;

import org.joml.Vector3f;

import omnikryptec.animation.renderer.AnimatedModelRenderer;
import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.event.EventSystem;
import omnikryptec.loader.ResourceLoader;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.particles.ParticleMaster;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.postprocessing.main.PostProcessingStage;
import omnikryptec.renderer.EntityRendererNoLight;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.KeySettings;
import omnikryptec.shader.base.Shader;

public class Instance {

    public static final int DISPLAYMANAGER_DISABLE_FPS_CAP = DisplayManager.DISABLE_FPS_CAP;
    public static final int GAMESETTINGS_NO_MULTISAMPLING = GameSettings.NO_MULTISAMPLING;

    public static final Vector3f MATHS_X_AXIS = Maths.X;
    public static final Vector3f MATHS_Y_AXIS = Maths.Y;
    public static final Vector3f MATHS_Z_AXIS = Maths.Z;
    public static final Vector3f MATHS_ZERO = Maths.ZERO;
    public static final Vector3f MATHS_ONE = Maths.ONE;

    public static final String SHADER_DEFAULT_PP_VERTEX_SHADER_LOC = Shader.DEFAULT_PP_VERTEX_SHADER_LOC;

    public static final EntityRendererNoLight RENDERERREG_DEF_ENTITY_RENDERER = RendererRegistration.DEF_ENTITY_RENDERER;
    public static final AnimatedModelRenderer RENDERERREG_DEF_ANIMATEDMODEL_RENDERER = RendererRegistration.DEF_ANIMATEDMODEL_RENDERER;

    public static final int PPS_INDEX_OPTION_USE_LAST_FBO = PostProcessingStage.INDEX_OPTION_USE_LAST_FBO;

    public static final javax.vecmath.Vector3f GRAVITY_EARTH = new javax.vecmath.Vector3f(0, -9.81F, 0);

    public static final javax.vecmath.Vector3f PHYSICS_X = PhysicsUtil.X;
    public static final javax.vecmath.Vector3f PHYSICS_Y = PhysicsUtil.Y;
    public static final javax.vecmath.Vector3f PHYSICS_Z = PhysicsUtil.Z;
    public static final javax.vecmath.Vector3f PHYSICS_ZERO = PhysicsUtil.ZERO;
    public static final javax.vecmath.Vector3f PHYSICS_ONE = PhysicsUtil.ONE;
    
    public static final DateTimeFormatter DATETIMEFORMAT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    public static final int DIMENSIONS = 3;

    public static final int MAX_JOINTS = 50;
    public static final int MAX_WEIGHTS = 3;

    public static final OmniKryptecEngine getEngine() {
        return OmniKryptecEngine.instance();
    }

    public static final PostProcessing getPostProcessing() {
        return PostProcessing.instance();
    }

    public static final DisplayManager getDisplayManager() {
        return DisplayManager.instance();
    }

    public static final Scene getCurrentScene() {
        return getEngine().getCurrentScene();
    }

    public static final Camera getCurrentCamera() {
        return getCurrentScene() == null ? null : getCurrentScene().getCamera();
    }

    public static final EventSystem getEventSystem() {
        return EventSystem.instance();
    }

    public static final GameSettings getGameSettings() {
        return getDisplayManager().getSettings();
    }

    public static final KeySettings getKeySettings() {
        return getGameSettings().getKeySettings();
    }

    public static final ResourceLoader getLoader() {
        return ResourceLoader.getInstance();
    }

	public static final ParticleMaster getParticleController() {
		return ParticleMaster.instance();
	}
    
}
