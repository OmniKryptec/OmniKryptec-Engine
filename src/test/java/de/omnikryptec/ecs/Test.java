package de.omnikryptec.ecs;

import de.omnikryptec.ecs.impl.ECSManager;
import de.omnikryptec.util.ExecutorsUtil;
import de.omnikryptec.util.updater.Time;

public class Test {

	public static void main(String[] args) {
		System.out.println("Started...");
		IECSManager manager = new ECSManager();
		DoSomethingSystem system = new DoSomethingSystem();
		manager.addSystem(system);
		//manager.addSystem(new AnotherSystem());
		//manager.addSystem(new SomeOtherSystem());
		int updt = 100;
		int ents = 1_000_000;
		System.out.println("Testing with "+ents+" entities and "+updt+" updates");
		long time = System.currentTimeMillis();
		for (int i = 0; i < ents; i++) {
			manager.addEntity(new Entity().addComponent(new SomeDataComponent()));
		}
		long time2 = System.currentTimeMillis();
		System.out.println("Initialization took "+(time2-time)*1000+" micro-s");
		System.out.println("Per Entity: "+(time2-time)/(double)ents*1000+" micro-s");
		System.out.println("Starting updates...");
		time = System.currentTimeMillis();
		for (int i = 0; i < updt; i++) {
			manager.update(new Time(i, 0, 1));
		}
		time2 = System.currentTimeMillis() - time;
		System.out.println("Time per update: " + time2 * 1000 / (double) updt + " micro-s");
		System.out.println("Time per entity: " + time2 * 1000 / (double) (updt * ents) + " micro-s");
		manager.removeSystem(system);
		ExecutorsUtil.shutdownNowAll();
	}
	
}
