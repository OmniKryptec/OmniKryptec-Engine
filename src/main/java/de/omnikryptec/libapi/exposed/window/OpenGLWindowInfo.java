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

package de.omnikryptec.libapi.exposed.window;

public class OpenGLWindowInfo extends WindowInfo<OpenGLWindowInfo> {

    private int majVersion = 1, minVersion = 0;
    private boolean vsync = true;

    public OpenGLWindowInfo() {
    }

    public int getMajVersion() {
        return majVersion;
    }

    public OpenGLWindowInfo setMajVersion(int majVersion) {
        this.majVersion = majVersion;
        return this;
    }

    public int getMinVersion() {
        return minVersion;
    }

    public OpenGLWindowInfo setMinVersion(int minVersion) {
        this.minVersion = minVersion;
        return this;
    }

    public boolean isVsync() {
        return vsync;
    }

    public OpenGLWindowInfo setVSync(boolean enabled) {
        this.vsync = enabled;
        return this;
    }

    @Override
    public OpenGLWindow createWindow() {
        return new OpenGLWindow(this);
    }

}
