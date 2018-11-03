package de.omnikryptec.ecs;

import java.util.BitSet;

import de.omnikryptec.core.Time;
import de.omnikryptec.ecs.system.ComponentSystem;

public class SomeOtherSystem extends ComponentSystem{

	protected SomeOtherSystem() {
		super(new BitSet());
	}

	@Override
	public void update(IECSManager entityManager, Time deltaTime) {
		//System.out.println("ma friends!");
	}

	@Override
	public int priority() {
		return 100;
	}
	
}
