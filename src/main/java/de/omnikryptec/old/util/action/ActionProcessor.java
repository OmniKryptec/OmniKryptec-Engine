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

package de.omnikryptec.old.util.action;

import de.omnikryptec.old.util.logger.Logger;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * ActionProcessor
 *
 * @author Panzer1119
 */
public class ActionProcessor extends Thread {

    public static final ActionProcessor PROCESSOR = new ActionProcessor();

    private final LinkedList<Action> actions = new LinkedList<>();
    private boolean running = false;

    public ActionProcessor(Action... actions) {
	addActions(actions);
	start();
    }

    public final synchronized ActionProcessor addActions(Action... actions) {
	final boolean isPaused = !hasActions();
	this.actions.addAll(Arrays.asList(actions));
	if (isPaused) {
	    notify();
	}
	return this;
    }

    @Override
    public final synchronized void run() {
	while (running || hasActions()) {
	    if (hasActions()) {
		acceptNextAction().doAction();
	    } else {
		try {
		    wait();
		} catch (Exception ex) {
		    if (Logger.isDebugMode()) {
			Logger.logErr("Error while waiting for new Actions: " + ex, ex);
		    }
		}
	    }
	}
    }

    public final synchronized LinkedList<Action> killNow() {
	final LinkedList<Action> actions_left = new LinkedList<>(actions);
	actions.clear();
	return actions_left;
    }

    public final synchronized ActionProcessor kill() {
	running = false;
	notify();
	return this;
    }

    public final synchronized Action acceptNextAction() {
	return actions.pollFirst();
    }

    public final synchronized boolean hasActions() {
	return !actions.isEmpty();
    }

    public static final void addActionsToProcessor(Action... actions) {
	PROCESSOR.addActions(actions);
    }

}
