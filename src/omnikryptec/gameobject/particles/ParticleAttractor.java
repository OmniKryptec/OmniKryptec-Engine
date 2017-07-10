package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.gameobject.GameObject;

public class ParticleAttractor {

    protected float gravitation = 0, tolerance = 0;
    protected boolean infinite = false;
    protected GameObject positionable;
    protected AttractorMode mode = AttractorMode.NOTHING;
    protected boolean enabled = true;
    protected Vector3f attenuation = new Vector3f(1, 0, 0);

    public ParticleAttractor(GameObject p) {
        this.positionable = p;
    }

    public ParticleAttractor(float x, float y, float z) {
        this.positionable = new GameObject();
        positionable.getTransform().setPosition(x, y, z);
    }

    public float getGravitation() {
        return gravitation;
    }

    public ParticleAttractor setGravitation(float gravitation) {
        this.gravitation = gravitation;
        return this;
    }

    public float getTolerance() {
        return tolerance;
    }

    public ParticleAttractor setTolerance(float tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    public AttractorMode getMode() {
        return this.mode;
    }

    public ParticleAttractor setMode(AttractorMode mode) {
        this.mode = mode;
        return this;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public ParticleAttractor setInfinite(boolean infinite) {
        this.infinite = infinite;
        return this;
    }

    public GameObject getAttractor() {
        return positionable;
    }

    public ParticleAttractor setAttractor(GameObject go) {
        this.positionable = go;
        return this;
    }

    public Vector3f getAbsolutePos() {
        return positionable.getTransform().getPosition();
    }

    public ParticleAttractor setEnabled(boolean b) {
        this.enabled = b;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ParticleAttractor setAttenuation(float a, float b, float c) {
        attenuation.set(a, b, c);
        return this;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

}
