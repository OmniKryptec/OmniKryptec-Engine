package omnikryptec.main;

import omnikryptec.gameobject.Camera;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.FrameState;

abstract class AbstractScene {
	

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

	public final AbstractScene setCamera(Camera c) {
		this.camera = c;
		return this;
	}
	
	public final double getRenderTimeMS() {
		return rendertime;
	}

	public final double getLogicTimeMS() {
		return logictime;
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
	
	public final boolean isUsingPhysics() {
		return physicsworld != null;
	}
	
	public final AbstractScene setAmbientColor(float r, float g, float b) {
		this.ambientcolor.set(r, g, b);
		return this;
	}

	public final AbstractScene setAmbientColor(Color f) {
		this.ambientcolor = f;
		return this;
	}

	public final Color getAmbientColor() {
		return ambientcolor;
	}
	
	public final String getName() {
		return name;
	}
}
