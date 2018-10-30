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

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Panzer1119
 */
public class SystemInputStream {

    public static final char NEWLINECHAR = '\n';
    public static final int NEWLINEINT = (int) NEWLINECHAR;
    public static final byte NEWLINEBYTE = (byte) NEWLINEINT;

    private final InputStream inputStreamOriginal;
    private final InputStream inputStreamNew;
    private final Thread thread;
    private final LinkedList<Byte> buffer = new LinkedList<>();
    private final ArrayList<Byte> lineBuffer = new ArrayList<>();
    private boolean isActive = false;

    public SystemInputStream(InputStream inputStreamOriginal) {
	this.inputStreamOriginal = inputStreamOriginal;
	this.thread = new Thread(() -> {
	    Logger.log("Thread System-InputStream started", LogLevel.FINE);
	    try {
		while (true) {
		    processData((byte) inputStreamOriginal.read());
		}
	    } catch (Exception ex) {
		Logger.logErr("Error while reading the System-InputStream: " + ex, ex);
	    }
	    Logger.log("Thread System-InputStream stopped", LogLevel.WARNING);
	});
	/*
	 * this.inputStreamNew = new InputStream() {
	 * 
	 * @Override public int read() throws IOException { int read =
	 * inputStreamOriginal.read(); if(read == -1) { setActive(false); }
	 * processData((byte) read); return read; }
	 * 
	 * };
	 */
	this.inputStreamNew = new InputStream() {

	    @Override
	    public int read() throws IOException {
		// synchronized(buffer) {
		// Logger.log("Read-Try: " + buffer.size());
		while (buffer.isEmpty()) {
		    try {
			Thread.sleep(100);
		    } catch (Exception ex) {
		    }
		}
		byte temp = buffer.getFirst();
		buffer.pop();
		// Logger.log("Return: " + temp);
		// Logger.log("Can Read: " + buffer.size());
		return temp;
		// }
	    }

	    @Override
	    public int available() throws IOException {
		// synchronized(buffer) {
		if (buffer.isEmpty()) {
		    return 1; // -1 or 1???
		} else {
		    return buffer.size();
		}
		// }
	    }

	    @Override
	    public void close() throws IOException {
		throw new IllegalStateException("The SystemInputStream can not be closed");
	    }

	    @Override
	    public String toString() {
		return "Custom SystemInputStream";
	    }

	};
    }

    public InputStream getOriginalInputStream() {
	return inputStreamOriginal;
    }

    public InputStream getNewInputStream() {
	return inputStreamNew;
    }

    public SystemInputStream setActive(boolean isActive) {
	if (!this.isActive && isActive) {
	    thread.start();
	} else if (this.isActive && !isActive) {
	    thread.interrupt();
	    // thread.stop();
	}
	this.isActive = isActive;
	return this;
    }

    public boolean isActive() {
	return isActive;
    }

    private void processData(byte data) {
	synchronized (buffer) {
	    buffer.addLast(data);
	    if (data == NEWLINEBYTE) {
		final byte[] dataAll = new byte[lineBuffer.size()];
		for (int i = 0; i < dataAll.length; i++) {
		    dataAll[i] = lineBuffer.get(i);
		}
		lineBuffer.clear();
		final String temp = new String(dataAll);
		final LogEntry logEntry = new LogEntry(temp, Instant.now(),
			temp.startsWith(Command.COMMANDSTART) ? LogLevel.COMMAND : LogLevel.INPUT);
		Logger.log(logEntry);
	    } else {
		lineBuffer.add(data);
	    }
	}
    }

    public static String nextLine() {
	try {
	    byte[] buffer = new byte[0];
	    while (Logger.NEWSYSIN.getNewInputStream().available() > 0) {
		byte read = (byte) Logger.NEWSYSIN.getNewInputStream().read();
		buffer = Arrays.copyOf(buffer, buffer.length + 1);
		buffer[buffer.length - 1] = read;
		if (read == ((byte) 10)) {
		    return new String(buffer);
		}
	    }
	    return null;
	} catch (Exception ex) {
	    Logger.logErr("Error while reading next line: " + ex, ex);
	    return null;
	}
    }

}
