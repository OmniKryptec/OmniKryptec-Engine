package de.omnikryptec.ecs;

import java.util.BitSet;

import de.omnikryptec.ecs.system.ComponentSystem;

public class AnotherSystem extends ComponentSystem{

	protected AnotherSystem() {
		super(new BitSet());
	}

	@Override
	public void update(IECSManager entityManager, float deltaTime) {
		//System.out.println("Another System!");
	}
	
	@Override
	public int priority() {
		return -100;
	}

}
