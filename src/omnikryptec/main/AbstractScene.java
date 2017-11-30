package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.d3.RenderConfiguration;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.FrameState;

abstract class AbstractScene<T extends GameObject> implements GameObjectContainer<T> {
	

	Camera camera;
	FrameState state = FrameState.NULL;
	double rendertime, logictime;
	double tmptime;
	String name;
	PhysicsWorld physicsworld;
	Color ambientcolor = new Color(0.01f, 0.01f, 0.01f, 1);
	
	
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
	
	protected abstract void logic();

	protected abstract long render(RenderConfiguration config);
	
	@Override
	public String toString() {
		return "Scene: "+name;
	}
}
