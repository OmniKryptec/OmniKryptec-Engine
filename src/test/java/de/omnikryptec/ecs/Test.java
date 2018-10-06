package de.omnikryptec.ecs;

import de.omnikryptec.ecs.entity.Entity;

public class Test {

	public static void main(String[] args) {
		System.out.println("Started...");
		IECSManager manager = new ECSManager();
		DoSomethingSystem system = new DoSomethingSystem();
		manager.addSystem(system);
		int updt = 100;
		int ents = 1_000_00;
		for (int i = 0; i < ents; i++) {
			manager.addEntity(new Entity().addComponent(new SomeDataComponent()));
		}
		System.out.println("Starting updates...");
		long time = System.currentTimeMillis();
		for (int i = 0; i < updt; i++) {
			manager.update(1);
		}
		long time2 = System.currentTimeMillis() - time;
		System.out.println("Time per update: " + time2 * 1000 / (double) updt + " mikro-s");
		System.out.println("Time per entity: " + time2 * 1000 / (double) (updt * ents) + " mikro-s");
		manager.removeSystem(system);
	}

}
