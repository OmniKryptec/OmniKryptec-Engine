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

package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public interface Postprocessor {
    
    public static Postprocessor constant(Texture texture) {
        return (t, v, r) -> texture;
    }
    
    Texture postprocess(final Time time, View view, Texture sceneRaw);
    
}
