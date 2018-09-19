/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

/**
 *
 * @author Panzer1119
 */
public class Blocker {

	private int refreshTime = 1;
	private boolean isBlocked = false;

	public Blocker() {
		this(1);
	}

	public Blocker(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public final void waitFor() {
		while (isBlocked) {
			try {
				Thread.sleep(refreshTime);
			} catch (Exception ex) {
			}
		}
	}

	public final boolean isBlocked() {
		return isBlocked;
	}

	public final Blocker setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
		return this;
	}

	public final int getRefreshTime() {
		return refreshTime;
	}

	public final Blocker setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
		return this;
	}

}
