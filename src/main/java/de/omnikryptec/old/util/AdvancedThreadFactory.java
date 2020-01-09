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

package de.omnikryptec.old.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AdvancedThreadFactory
 *
 * @author Panzer1119
 */
public class AdvancedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private String name;

    // TODO improve
    public AdvancedThreadFactory() {
	this("");
    }

    public AdvancedThreadFactory(String name) {
	SecurityManager s = System.getSecurityManager();
	group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	this.name = name;
    }

    @Override
    public final Thread newThread(Runnable r) {
	Thread t = new Thread(group, r, String.format(name, threadNumber.getAndIncrement()), 0);
	if (t.isDaemon()) {
	    t.setDaemon(false);
	}
	if (t.getPriority() != Thread.NORM_PRIORITY) {
	    t.setPriority(Thread.NORM_PRIORITY);
	}
	return t;
    }

    public final String getName() {
	return name;
    }

    public final AdvancedThreadFactory setName(String name) {
	this.name = name;
	return this;
    }

}
