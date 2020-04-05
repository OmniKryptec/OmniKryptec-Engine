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

package de.omnikryptec.render;

import org.joml.Matrix4f;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.opengl.framebuffer.GLScreenBuffer.ScreenBufferResizedEvent;

public class AdaptiveCamera extends Camera {

    public static interface ProjectionCreationDelegate {
        Matrix4f createMatrix(int width, int height);
    }

    private final ProjectionCreationDelegate delegate;

    public AdaptiveCamera(final ProjectionCreationDelegate delegate) {
        super(null);
        this.delegate = delegate;
        final RenderAPI rapi = LibAPIManager.instance().getGLFW().getRenderAPI();
        setProjection(delegate.createMatrix(rapi.getSurface().getWidth(), rapi.getSurface().getHeight()));
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }

    @EventSubscription
    public void onChange(final ScreenBufferResizedEvent ev) {
        setProjection(this.delegate.createMatrix(ev.width, ev.height));
    }

}
