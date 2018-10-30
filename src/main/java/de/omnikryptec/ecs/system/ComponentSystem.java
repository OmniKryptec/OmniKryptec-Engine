package de.omnikryptec.ecs.system;

import java.util.BitSet;
import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.util.Util;

public abstract class ComponentSystem {

    private BitSet family;
    protected List<Entity> entities;

    protected boolean enabled = true;

    protected ComponentSystem(BitSet required) {
	Util.ensureNonNull(required, "BitSet must not be null (but can be empty)");
	this.family = required;
    }

    public BitSet getFamily() {
	return family;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public void addedToIECSManager(IECSManager iecsManager) {
	entities = iecsManager.getEntitesFor(family);
    }

    public void removedFromIECSManager(IECSManager iecsManager) {
	entities = null;
    }

    public int priority() {
	return 0;
    }

    public abstract void update(IECSManager iecsManager, float deltaTime);

}
