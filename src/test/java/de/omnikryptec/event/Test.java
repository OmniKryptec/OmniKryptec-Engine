package de.omnikryptec.event;

public class Test {

	public static void main(String[] args) {
		EventBus bus = new EventBus();
		bus.register(new Test());
		bus.enqueueOrPost(new Event(), false);
		bus.processQueuedEvents();
	}
	
	@EventSubscription
	public void test(Event e) {
		System.out.println(e);
	}

}
