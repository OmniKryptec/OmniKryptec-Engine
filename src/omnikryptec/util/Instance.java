package omnikryptec.util;

import java.time.format.DateTimeFormatter;

import org.joml.Vector3f;

import omnikryptec.animation.renderer.AnimatedModelRenderer;
import omnikryptec.display.DisplayManager;
import omnikryptec.event.event.EventSystem;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.gameobject.terrain.TerrainRenderer;
import omnikryptec.main.AbstractScene2D;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.postprocessing.main.PostProcessingStage;
import omnikryptec.renderer.d3.ForwardMeshRenderer;
import omnikryptec.renderer.d3.RendererRegistration;
import omnikryptec.renderer.d3.SimpleMeshRenderer;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.loader.ResourceObject;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.KeySettings;

public class Instance {

	public static final int DISPLAYMANAGER_DISABLE_FPS_CAP = DisplayManager.DISABLE_FPS_CAP;
	public static final int GAMESETTINGS_NO_MULTISAMPLING = GameSettings.NO_MULTISAMPLING;

	public static final Vector3f MATHS_X_AXIS = Maths.X;
	public static final Vector3f MATHS_Y_AXIS = Maths.Y;
	public static final Vector3f MATHS_Z_AXIS = Maths.Z;
	public static final Vector3f MATHS_ZERO = Maths.ZERO;
	public static final Vector3f MATHS_ONE = Maths.ONE;

	//public static final String SHADER_DEFAULT_PP_VERTEX_SHADER_LOC = Shader.DEFAULT_PP_VERTEX_SHADER_LOC;

	public static final TerrainRenderer RENDERERREG_DEF_TERRAIN_RENDERER = RendererRegistration.DEF_TERRAIN_RENDERER;
	public static final SimpleMeshRenderer RENDERERREG_DEF_ENTITY_RENDERER = RendererRegistration.SIMPLE_MESH_RENDERER;
	public static final AnimatedModelRenderer RENDERERREG_DEF_ANIMATEDMODEL_RENDERER = RendererRegistration.DEF_ANIMATEDMODEL_RENDERER;

	public static final ForwardMeshRenderer RENDERERREG_FOR_ENTITY_RENDERER = RendererRegistration.FORWARD_MESH_RENDERER;
	
	public static final int PPS_INDEX_OPTION_USE_LAST_FBO = PostProcessingStage.INDEX_OPTION_USE_LAST_FBO;

	public static final javax.vecmath.Vector3f GRAVITY_EARTH = new javax.vecmath.Vector3f(0, -9.81F, 0);

	public static final javax.vecmath.Vector3f PHYSICS_X = PhysicsUtil.X;
	public static final javax.vecmath.Vector3f PHYSICS_Y = PhysicsUtil.Y;
	public static final javax.vecmath.Vector3f PHYSICS_Z = PhysicsUtil.Z;
	public static final javax.vecmath.Vector3f PHYSICS_ZERO = PhysicsUtil.ZERO;
	public static final javax.vecmath.Vector3f PHYSICS_ONE = PhysicsUtil.ONE;

	public static final DateTimeFormatter DATETIMEFORMAT_TIMESTAMP = DateTimeFormatter
			.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

	public static final int DIMENSIONS = 3;



	public static final OmniKryptecEngine getEngine() {
		return OmniKryptecEngine.instance();
	}

	public static final PostProcessing getPostProcessor() {
		return OmniKryptecEngine.instance().getPostprocessor();
	}

	public static final DisplayManager getDisplayManager() {
		return OmniKryptecEngine.instance().getDisplayManager();
	}

	public static final AbstractScene3D getCurrent3DScene() {
		return getEngine().getCurrent3DScene();
	}

	public static final Camera getCurrent3DCamera() {
		return getCurrent3DScene() == null ? null : getCurrent3DScene().getCamera();
	}
	
	public static final AbstractScene2D getCurrent2DScene() {
		return getEngine().getCurrent2DScene();
	}

	public static final Camera getCurrent2DCamera() {
		return getCurrent2DScene() == null ? null : getCurrent2DScene().getCamera();
	}

	public static final EventSystem getEventSystem() {
		return OmniKryptecEngine.instance().getEventsystem();
	}

	public static final GameSettings getGameSettings() {
		return getDisplayManager().getSettings();
	}

	public static final KeySettings getKeySettings() {
		return getGameSettings().getKeySettings();
	}

	public static final ResourceLoader getLoader() {
		return ResourceLoader.currentInstance();
	}

	public static final ParticleMaster getParticleController() {
		return ParticleMaster.instance();
	}

	public static final double getCurrentTime() {
		return getDisplayManager().getCurrentTime();
	}

	public static final float getDeltaTimef() {
		return OmniKryptecEngine.instance().getDeltaTimef();
	}

	public static final double getRenderDeltaTime() {
		return getDisplayManager().getDUDeltaTime();
	}

	public static final double getFPS() {
		return getDisplayManager().getFPS();
	}

	public static final long getFPSCounted() {
		return getDisplayManager().getFPSCounted();
	}
	
	public static final long getFramecount() {
		return getDisplayManager().getFramecount();
	}
	
	public static final <T extends ResourceObject> T getResource(Class<? extends T> clazz, String name) {
		return ResourceLoader.getResourceDefault(clazz, name);
	}

	public static final <T extends ResourceObject> T getResource(String name) {
		return ResourceLoader.getResourceDefault(name);
	}
}
