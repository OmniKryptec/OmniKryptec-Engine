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

package de.omnikryptec.util.profiler;

import de.omnikryptec.swing.ChartData;

public class ProfileContainer {

    private double time;
    private String name;

    public ProfileContainer(String name, double time) {
        this.time = time;
        this.name = name;
    }

    double getTime() {
        return time;
    }

    String getName() {
        return name;
    }

    String getPercentage(double maxtime) {
        return new StringBuilder().append(String.format("%.1f", (getTime() / maxtime) * 100)).append("%").toString();
    }

    String getReletiveTo(double maxtime) {
        return new StringBuilder().append(String.format("%.1f", getTime())).append("ms/").append(String.format("%.1f", maxtime)).append("ms").toString();
    }

    @Override
    public String toString() {
        return getName() + ": " + String.format("%.1f", getTime()) + "ms";
    }
    
    public ChartData toChartData() {
        return new ChartData(name, time);
    }

}
