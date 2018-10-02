package omnikryptec.test.ecs;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;

public class Test {

	public static void main(String[] args) {
		System.out.println("Started...");
		EntityManager manager = new EntityManager();
		manager.addSystem(new DoSomethingSystem(), false);
		int updt = 10000;
		int ents = 1_000_000;
		for (int i = 0; i < ents; i++) {
			manager.addEntity(new Entity().addComponent(new SomeDataComponent()));
		}
		System.out.println("Starting updates...");
		long time = System.currentTimeMillis();
		for (int i = 0; i < updt; i++) {
			manager.update(1);
		}
		long time2 = System.currentTimeMillis() - time;
		System.out.println("Time per update: " + time2 / (double) updt + "ms");
	}

}
