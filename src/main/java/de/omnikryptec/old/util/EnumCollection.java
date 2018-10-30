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

@Deprecated // As retarded as the Instance-class
public class EnumCollection {

    public static enum FrameState {
	NULL, RENDERING, LOGIC;
    }

    public static enum RendererTime {
	PRE, POST;
    }

    public static enum UpdateType {
	DYNAMIC, SEMISTATIC, STATIC;
    }

    public static enum RenderType {
	ALWAYS, MEDIUM, FOLIAGE, BIG;
    }

    public static enum GameState {
	STARTING, RUNNING, ERROR, STOPPING, STOPPED;
    }

    public static enum GameLoopShutdownOption {
	ENGINE(2), LOOP(1), NOT_NOW(0);

	private final int lvl;

	private GameLoopShutdownOption(int l) {
	    lvl = l;
	}

	public int getLvl() {
	    return lvl;
	}
    }

    public static enum Dimension {
	D2(2), D3(3);

	public final int bases;

	private Dimension(int d) {
	    bases = d;
	}
    }

    public static enum BlendMode {
	ADDITIVE, ALPHA, MULTIPLICATIVE, DISABLE;
    }

    public static enum DepthbufferType {
	NONE, DEPTH_TEXTURE, DEPTH_RENDER_BUFFER;
    }

    public static enum FixedSizeMode {
	OFF, ALLOW_SCALING, ON;
    }
}
