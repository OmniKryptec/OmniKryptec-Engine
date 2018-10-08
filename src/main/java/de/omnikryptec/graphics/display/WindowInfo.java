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

package de.omnikryptec.graphics.display;

/**
 * OpenGLInfo class
 *
 * @author pcfreak9000 &amp; Panzer1119
 */
public class WindowInfo {

	//TODO version has nothing to do with the Window
	private int majVers = 3;
	private int minVers = 3;
	
	private boolean resizeable;
	private int width = 0, height = 0;
	private boolean fullscreen = false;
	private int[] lockWindowAspectRatio = { -1, -1 };

	public WindowInfo() {
		this(800, 600);
	}

	public WindowInfo(int width, int height) {
		this(false, width, height);
	}

	public WindowInfo(boolean fullscreen, int width, int height) {
		this(true, fullscreen, width, height);
	}

	public WindowInfo(boolean resizeable, boolean fullscreen, int width, int height) {
		this(3, 2, resizeable, fullscreen, width, height);
	}

	/**
	 * Constructs an OpenGLInfo from major/minor version and a PixelFormat
	 *
	 * @param majVers    Integer Major version
	 * @param minVers    Integer Minor version
	 * @param fullscreen Boolean Fullscreen
	 * @param height     Integer Height
	 * @param resizeable Boolean Resizeable
	 * @param width      Integer Width
	 */
	public WindowInfo(int majVers, int minVers, boolean resizeable, boolean fullscreen, int width, int height) {
		this.majVers = majVers;
		this.minVers = minVers;
		this.resizeable = resizeable;
		this.fullscreen = fullscreen;
		this.width = width;
		this.height = height;
	}

	public WindowInfo setLockWindowAspectRatio(int w, int h) {
		lockWindowAspectRatio[0] = w;
		lockWindowAspectRatio[1] = h;
		return this;
	}

	int getMajorVersion() {
		return majVers;
	}

	int getMinorVersion() {
		return minVers;
	}

	boolean wantsResizeable() {
		return resizeable;
	}

	boolean wantsFullscreen() {
		return fullscreen;
	}

	int getWidth() {
		return width;
	}

	int getHeight() {
		return height;
	}

	int[] lockWindowAspectRatio() {
		return lockWindowAspectRatio;
	}

}
