package omnikryptec.main;

import java.util.List;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject;
import omnikryptec.gameobject.Light;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.physics.JBulletPhysicsWorld;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.RenderConfiguration;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.Color;
import omnikryptec.util.PhysicsUtil;

public abstract class AbstractScene implements DataMapSerializable{
	
	public static enum FrameState{
		NULL,RENDERING,LOGIC;
	}
	
	public static final AbstractScene byName(String name) {
        if (OmniKryptecEngine.rawInstance() != null) {
            for (AbstractScene scene : OmniKryptecEngine.rawInstance().getScenes()) {
                if (scene.getName() == null ? name == null : scene.getName().equals(name)) {
                    return scene;
                }
            }
            return null;
        } else {
            return null;
        }
    }
	
	private Camera camera;
	private FrameState state = FrameState.NULL;
	private double rendertime, logictime;
	private double tmptime;
	private String name;
	private PhysicsWorld physicsworld;
    private Color clearcolor = new Color(0, 0, 0, 0);
    private Color ambientcolor = new Color(0,0,0,1);
    
	protected AbstractScene(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}
	
	public final void publicLogic(boolean particles) {
		state = FrameState.LOGIC;
		tmptime = DisplayManager.instance().getCurrentTime();
		if(isUsingPhysics()){ 
	    	physicsworld.stepSimulation();
	    }
		logic();
		camera.doLogic();
		logictime = DisplayManager.instance().getCurrentTime() - tmptime;
		if(particles&&camera!=null) {
			ParticleMaster.instance().logic(camera);
		}
		state = FrameState.NULL;
	}
	
	final long mainRender() {
		ParticleMaster.instance().resetTimes();
		state = FrameState.RENDERING;
		tmptime = DisplayManager.instance().getCurrentTime();
		long l = publicRender(new RenderConfiguration());
		rendertime = DisplayManager.instance().getCurrentTime() - tmptime - ParticleMaster.instance().getRenderTimeMS();
		state = FrameState.NULL;
		return l;
	}
	
	public final long publicRender(RenderConfiguration config) {
		long l = render(config);
		if(config.renderParticles()&&camera!=null) {
			ParticleMaster.instance().render(camera);
		}
		return l;
	}
	
	public final FrameState getState() {
		return state;
	}
	
	public final Camera getCamera() {
		return camera;
	}
	
	public final AbstractScene setCamera(Camera c) {
		this.camera = c;
		return this;
	}
	
	public final double getRenderTimeMS(){
    	return rendertime;
    }
    
    public final double getLogicTimeMS(){
    	return logictime;
    }
	
    @Override
    public final String getName() {
    	return name;
    }
    
    protected final AbstractScene setName(String name) {
    	this.name = name;
    	return this;
    }
    
    public final PhysicsWorld getPhysicsWorld() {
        return physicsworld;
    }

    public final AbstractScene setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsworld = physicsWorld;
        return this;
    }

    public final AbstractScene useDefaultPhysics() {
        return setPhysicsWorld(new JBulletPhysicsWorld(PhysicsUtil.createDefaultDynamicsWorld()));
    }

    public final boolean isUsingPhysics() {
        return physicsworld != null;
    }
    
    public final AbstractScene setClearColor(float r, float g, float b) {
        return setClearColor(r, g, b, 1);
    }

    public final AbstractScene setClearColor(float r, float g, float b, float a) {
        clearcolor.set(r, g, b, a);
        return this;
    }

    public final AbstractScene setClearColor(Color f) {
        clearcolor = f;
        return this;
    }

    public final Color getClearColor() {
        return clearcolor;
    }
    
    public final AbstractScene setAmbientColor(float r, float g, float b) {
    	this.clearcolor.set(r, g, b);
    	return this;
    }
    
    public final AbstractScene setAmbientColor(Color f) {
    	this.ambientcolor = f;
    	return this;
    }
    
    public final Color getAmbientColor() {
    	return ambientcolor;
    }
    
    protected abstract void logic();
	
	protected abstract long render(RenderConfiguration config);
	
	public abstract boolean addGameObject(GameObject go);
	
	public GameObject removeGameObject(GameObject go) {
		return removeGameObject(go, true);
	}
	
	public abstract GameObject removeGameObject(GameObject go, boolean delete);
	
	public abstract List<Light> getLights();
}
