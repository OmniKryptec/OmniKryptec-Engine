/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.resource;

//TODO pcfreak9000 create a loader for this so the config can be loaded too
public class TextureConfig {
    
    public static enum WrappingMode {
        ClampToEdge, Repeat, MirroredRepeat
    }
    
    public static enum MagMinFilter {
        Nearest, Linear
    }
    
    private float anisotropic = 0.0f;
    private boolean mipmap = true;
    
    private WrappingMode wrappingMode = WrappingMode.ClampToEdge;
    private MagMinFilter minFilter = MagMinFilter.Linear;
    private MagMinFilter magFilter = MagMinFilter.Linear;
    
    public float anisotropicValue() {
        return this.anisotropic;
    }
    
    public boolean mipmap() {
        return this.mipmap;
    }
    
    public WrappingMode wrappingMode() {
        return this.wrappingMode;
    }
    
    public MagMinFilter minFilter() {
        return this.minFilter;
    }
    
    public MagMinFilter magFilter() {
        return this.magFilter;
    }
    
    public TextureConfig anisotropic(final float value) {
        this.anisotropic = value;
        return this;
    }
    
    public TextureConfig mipmap(final boolean value) {
        this.mipmap = value;
        return this;
    }
    
    public TextureConfig wrappingMode(final WrappingMode mode) {
        this.wrappingMode = mode;
        return this;
    }
    
    public TextureConfig minFilter(final MagMinFilter filter) {
        this.minFilter = filter;
        return this;
    }
    
    public TextureConfig magFilter(final MagMinFilter filter) {
        this.magFilter = filter;
        return this;
    }
}
