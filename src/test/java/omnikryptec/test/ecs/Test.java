package omnikryptec.test.ecs;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;

public class Test {
	
	public static void main(String[] args) {
		System.out.println("Started...");
		SomeDataComponent c = new SomeDataComponent();
		Entity e = new Entity().addComponent(c);
		EntityManager manager = new EntityManager();
		manager.addSystem(new DoSomethingSystem(), false);
		manager.addEntity(e);
		System.out.println("Starting updates...");
		for(int i=0; i<10; i++) {
			manager.update(1);
		}
		System.out.println(c.alonglong);
	}
	
}
