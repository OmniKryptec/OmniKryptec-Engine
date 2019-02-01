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

package de.omnikryptec.util;

import java.util.Arrays;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.graphics.render.ArrayDisplayList;
import de.omnikryptec.graphics.render.DisplayList;
import de.omnikryptec.graphics.render.IProjection;
import de.omnikryptec.graphics.render.RenderedObject;
import de.omnikryptec.graphics.render.Renderer;
import de.omnikryptec.graphics.render.RendererSet;
import de.omnikryptec.graphics.render.SimpleRenderCollection;
import de.omnikryptec.graphics.render.Viewport;
import de.omnikryptec.util.data.IterableCombiner;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class ARandomTest {
    
    public static void main(final String[] args) {
        List<String> kek1 = Arrays.asList("kjkjkjkjkjkjk".split("j"));
        List<String> kek2 = Arrays.asList("kjkjkjkjkjkjk".split("j"));
        
        IterableCombiner<String> comb = new IterableCombiner<>(kek1, kek2);
        for (String s : comb) {
            System.out.println("! "+s);
        }
        final Mapper<TestTest> mapper2 = new Mapper<>();
        test(mapper2.of(TestTest.class));
    }
    
    private static class Robj implements RenderedObject {
        
        @Override
        public boolean isVisible(final FrustumIntersection frustum) {
            
            return true;
        }
        
    }
    
    private static class Rend implements Renderer {
        
        @Override
        public void render(final Time time, final IProjection projection, final DisplayList objs,
                final Settings<?> renderSettings) {
        }
        
        @Override
        public DisplayList createRenderList() {
            
            return new ArrayDisplayList<>();
        }
        
    }
    
    public static void test(final Mapper<TestTest>.Mapping mapping) {
        System.out.println(mapping);
    }
}
