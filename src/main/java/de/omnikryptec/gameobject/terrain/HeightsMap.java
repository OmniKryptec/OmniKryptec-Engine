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

package de.omnikryptec.gameobject.terrain;

import de.pcfreak9000.noise.noises.Noise;

import java.awt.image.BufferedImage;

public class HeightsMap implements Noise {

	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	private final BufferedImage map;
	private final boolean allowModulo;
	private final int width, height;

	public HeightsMap(BufferedImage heightsmap, boolean allowModulo) {
		this.map = heightsmap;
		this.allowModulo = allowModulo;
		height = map.getHeight();
		width = map.getWidth();
	}

	private final double getHeightm(double x, double z) {
		if (!allowModulo && (x < 0 || x >= width || z < 0 || z >= height)) {
			return 0;
		} else if (allowModulo) {
			if (x < 0) {
				x = width + x % width;
			}
			x %= width;
			if (z < 0) {
				z = height + z % height;
			}
			z %= height;
		}
		double height = map.getRGB((int) x, (int) z);
		height += MAX_PIXEL_COLOR / 2.0;
		height /= MAX_PIXEL_COLOR / 2.0;
		return height;
	}

	@Override
	public double valueAt(double arg0, double arg1) {
		return getHeightm(arg0, arg1);
	}

	@Override
	public double valueAt(double arg0, double arg1, double arg2) {
		return getHeightm(arg0, arg1);
	}

	@Override
	public double valueAt(double arg0, double arg1, double arg2, double arg3) {
		return getHeightm(arg0, arg1);
	}

}
