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

package de.omnikryptec.old.swing;

import de.omnikryptec.util.data.Color;

/**
 *
 * @author Panzer1119
 */
public class ChartData {

    private final String name;
    private double value = 0.0F;
    private Color color = null;
    private double percentage = -1.0F;

    public ChartData(String name, double value) {
	this.name = name;
	this.value = value;
    }

    public final String getName() {
	return name;
    }

    public final double getValue() {
	return value;
    }

    public final ChartData setValue(double value) {
	this.value = value;
	return this;
    }

    public final Color getColor() {
	return color;
    }

    public final ChartData setColor(Color color) {
	this.color = color;
	return this;
    }

    public final double getPercentage() {
	return percentage;
    }

    public final ChartData setPercentage(double percentage) {
	this.percentage = percentage;
	return this;
    }

    @Override
    public final String toString() {
	return String.format("ChartData \"%s\" = %f", name, value);
    }

}
