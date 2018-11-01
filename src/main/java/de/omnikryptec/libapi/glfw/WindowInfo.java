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

package de.omnikryptec.libapi.glfw;

public abstract class WindowInfo<T extends WindowInfo<?>> {
    
    private int width = 800;
    private int height = 600;
    private boolean fullscreen = false;
    private boolean resizeable = true;
    private boolean lockAspectRatio = false;
    private String name = "Display";
    
    protected WindowInfo() {
    }
    
    public abstract Window<T> createWindow();
    
    public int getWidth() {
        return width;
    }
    
    public T setWidth(int width) {
        this.width = width;
        return (T) this;
    }
    
    public int getHeight() {
        return height;
    }
    
    public T setHeight(int height) {
        this.height = height;
        return (T) this;
    }
    
    public boolean isFullscreen() {
        return fullscreen;
    }
    
    public T setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        return (T) this;
    }
    
    public boolean isResizeable() {
        return resizeable;
    }
    
    public T setResizeable(boolean resizeable) {
        this.resizeable = resizeable;
        return (T) this;
    }
    
    public boolean isLockAspectRatio() {
        return lockAspectRatio;
    }
    
    public T setLockAspectRatio(boolean lockAspectRatio) {
        this.lockAspectRatio = lockAspectRatio;
        return (T) this;
    }
    
    public String getName() {
        return name;
    }
    
    public T setName(String name) {
        this.name = name;
        return (T) this;
    }
    
}
