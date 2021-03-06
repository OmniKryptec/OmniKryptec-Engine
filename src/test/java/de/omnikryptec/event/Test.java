/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.event;

public class Test {
    
    public static void main(final String[] args) {
        final EventBus bus = new EventBus(false);
        bus.register(new Test());
        bus.enqueue(new Event());
        bus.enqueue(new TestEvent());
        bus.enqueue(new TestEvent.TestEvent2());
        bus.processQueuedEvents();
        bus.post(new TestEvent());
    }
    
    @EventSubscription(priority = 10)
    public void test(final TestEvent e) {
        System.out.println("FIRST!");
        System.out.println(e);
    }
    
    @EventSubscription
    public void g(final TestEvent e) {
        System.out.println("SECOND?!");
        System.out.println(e);
    }
    
    public static class TestEvent extends Event {
        public static class TestEvent2 extends TestEvent {
            
        }
    }
    
}
