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

package de.omnikryptec.old.util.error;

public class ThroweableItem implements ErrorItem {

    private Throwable t;

    public ThroweableItem(Throwable t) {
	this.t = t;
    }

    @Override
    public String getError() {
	StringBuilder builder = new StringBuilder();
	builder.append("ERROR: ").append(t.toString()).append("\n").append("\n");
	StackTraceElement[] st = t.getStackTrace();
	for (int i = 0; i < st.length; i++) {
	    builder.append(st[i].toString()).append("\n");
	}
	return builder.toString();
    }

}
