package de.omnikryptec.event;

public class Test {

    public static void main(final String[] args) {
        final EventBus bus = new EventBus();
        bus.register(new Test());
        bus.enqueue(new Event());
        bus.enqueue(new TestEvent());
        bus.enqueue(new TestEvent.TestEvent2());
        bus.processQueuedEvents();
    }

    @EventSubscription
    public void test(final TestEvent e) {
        System.out.println(e);
    }

    public static class TestEvent extends Event {
        public static class TestEvent2 extends TestEvent {

        }
    }

}
