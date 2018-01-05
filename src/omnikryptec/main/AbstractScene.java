package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.FrameState;

public abstract class AbstractScene<T extends GameObject> implements GameObjectContainer<T> {

    Camera camera;
    FrameState state = FrameState.NULL;
    String name;
    PhysicsWorld physicsworld;
    Color ambientcolor = new Color(0.01f, 0.01f, 0.01f, 1);

    private double rendertime, logictime;
    private double tmptime;
    
    protected AbstractScene(){
    }
    
    public final FrameState getState() {
        return state;
    }

    public Camera getCamera() {
        return camera;
    }

    public final AbstractScene<T> setCamera(Camera c) {
        this.camera = c;
        return this;
    }

    public final double getRenderTimeMS() {
        return rendertime;
    }

    public final double getLogicTimeMS() {
        return logictime;
    }

    protected final AbstractScene<T> setName(String name) {
        this.name = name;
        return this;
    }

    public final PhysicsWorld getPhysicsWorld() {
        return physicsworld;
    }

    public final AbstractScene<T> setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsworld = physicsWorld;
        return this;
    }

    public final boolean isUsingPhysics() {
        return physicsworld != null;
    }

    public final AbstractScene<T> setAmbientColor(float r, float g, float b) {
        this.ambientcolor.set(r, g, b);
        return this;
    }

    public final AbstractScene<T> setAmbientColor(Color f) {
        this.ambientcolor = f;
        return this;
    }

    public final Color getAmbientColor() {
        return ambientcolor;
    }

    public final String getName() {
        return name;
    }

    final void timedLogic() {
		tmptime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
		publicLogic();
		logictime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime() - tmptime;
    }
    
    public final void publicLogic() {
		state = FrameState.LOGIC;
		if(isUsingPhysics()) {
			physicsworld.preLogic();
		}
		logic();
		camera.doLogic();
		if (isUsingPhysics()) {
			physicsworld.stepSimulation();
		}
		state = FrameState.NULL;
	}
    
    final void timedRender() {
    	tmptime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
    	publicRender();
		rendertime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime() - tmptime;
    }
    
    public final long publicRender() {
		state = FrameState.RENDERING;
		preRender();
		long l = render();
		postRender();
		state = FrameState.NULL;
		return l;
    }
    
    
    protected abstract void logic();
    
    protected abstract long render();

    protected void preRender() {}
    
    protected void postRender() {}
    
    protected abstract void addGameObject_(T g, boolean added);
    protected abstract T removeGameObject_(T g, boolean delete);
    
	public void addGameObject(T go, boolean added){
		addGameObject_(go, added);
	}

	public T removeGameObject(T go, boolean delete){
		removeGameObject_(go, delete);
		return go;
	}
    
	public final void realign(T g) {
		removeGameObject_(g, false);
		addGameObject_(g, false);
	}
	
    @Override
    public String toString() {
        return "Scene: " + name;
    }
}
