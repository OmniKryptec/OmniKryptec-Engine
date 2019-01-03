/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.event.eventV2.engineevents;

import de.omnikryptec.old.event.eventV2.Event;
import de.omnikryptec.old.main.OmniKryptecEngine;

public class ErrorEvent extends Event {

    private Exception myexcp;
    private Object source;

    public ErrorEvent(Exception e, Object source) {
	super(OmniKryptecEngine.instance().ENGINE_BUS);
	this.myexcp = e;
	this.source = source;
	this.consumeable = false;
    }

    public Exception getError() {
	return myexcp;
    }

    public Object getSource() {
	return source;
    }

}
