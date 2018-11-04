package de.omnikryptec.event;

public class Test {

    public static void main(String[] args) {
        EventBus bus = new EventBus();
        bus.register(new Test());
        bus.enqueueOrPost(new Event(), false);
        bus.enqueueOrPost(new TestEvent(), false);
        bus.enqueueOrPost(new TestEvent.TestEvent2(), false);
        bus.processQueuedEvents();
    }

    @EventSubscription
    public void test(TestEvent e) {
        System.out.println(e);
    }

    public static class TestEvent extends Event {
        public static class TestEvent2 extends TestEvent {

        }
    }

}
