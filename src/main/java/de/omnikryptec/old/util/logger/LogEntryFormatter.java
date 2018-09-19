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

package de.omnikryptec.old.util.logger;

import java.util.ArrayList;

/**
 *
 * @author Panzer1119
 */
public class LogEntryFormatter {

	public enum LogEntryFormatTile {
		DATETIME("dt"), CLASSLINE("cl"), THREAD("th"), LOGLEVEL("ll"), MESSAGE("me"), EXCEPTION("ex");

		private final String shortcut;

		LogEntryFormatTile(String shortcut) {
			this.shortcut = shortcut;
		}

		public String getShortcut() {
			return shortcut;
		}
	}

	public static String toggleFormat(String pattern, boolean dt, boolean cl, boolean th, boolean ll, boolean me,
			boolean ex) {
		if (!dt && !cl && !th && !ll && me) {
			return LogEntryFormatTile.MESSAGE.getShortcut() + (ex ? LogEntryFormatTile.EXCEPTION.getShortcut() : "");
		}
		if (!dt) {
			pattern = pattern.replaceAll(LogEntryFormatTile.DATETIME.getShortcut(), "");
		}
		if (!cl) {
			pattern = pattern.replaceAll(LogEntryFormatTile.CLASSLINE.getShortcut(), "");
		}
		if (!th) {
			pattern = pattern.replaceAll(LogEntryFormatTile.THREAD.getShortcut(), "");
		}
		if (!ll) {
			pattern = pattern.replaceAll(LogEntryFormatTile.LOGLEVEL.getShortcut(), "");
		}
		if (!me) {
			pattern = pattern.replaceAll(LogEntryFormatTile.MESSAGE.getShortcut(), "");
		}
		if (!ex) {
			pattern = pattern.replaceAll(LogEntryFormatTile.EXCEPTION.getShortcut(), "");
		}
		return pattern;
	}

	public static LogEntryFormatTile[] toLogEntryFormatTiles(String pattern) {
		final ArrayList<LogEntryFormatTile> logEntryFormatTiles = new ArrayList<>();
		for (LogEntryFormatTile left : LogEntryFormatTile.values()) {
			if (pattern.contains(left.getShortcut())) {
				logEntryFormatTiles.add(left);
			}
		}
		return logEntryFormatTiles.toArray(new LogEntryFormatTile[logEntryFormatTiles.size()]);
	}

	public static boolean isPrinting(String pattern, LogEntryFormatTile... logEntryFormatTiles) {
		boolean isPrinting = true;
		final LogEntryFormatTile[] logEntryFormatTiles_printing = toLogEntryFormatTiles(pattern);
		for (LogEntryFormatTile left : logEntryFormatTiles) {
			boolean found = false;
			for (LogEntryFormatTile left_2 : logEntryFormatTiles_printing) {
				if (left == left_2) {
					found = true;
					break;
				}
			}
			if (!found) {
				isPrinting = false;
				break;
			}
		}
		return isPrinting;
	}

}
