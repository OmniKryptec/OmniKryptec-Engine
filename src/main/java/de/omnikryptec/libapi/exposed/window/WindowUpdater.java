/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.updater.AbstractUpdater;

public class WindowUpdater extends AbstractUpdater {
    
    private final IWindow window;
    private double swaptime;
    
    public WindowUpdater(final IWindow window) {
        this.window = window;
    }
    
    @Override
    protected void operation() {
        final double time = LibAPIManager.instance().getTime();
        this.window.swapBuffers();
        this.swaptime = LibAPIManager.instance().getTime() - time;
        LibAPIManager.instance().pollEvents();
    }
    
    public double getSwapTime() {
        return this.swaptime;
    }
    
    public IWindow getWindow() {
        return this.window;
    }
    
}
