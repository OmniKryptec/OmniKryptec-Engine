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

import java.util.Collection;

import org.joml.FrustumIntersection;

import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.render.storage.RenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectType;

public class TestTest {
    
    public static final void main(final String[] args) throws Exception {
        //        DynamicArray<Object> array = new DynamicArray<>();
        //        int i1=0;
        //        for (int i = 0; i < 20; i++) {
        //            if (Math.random() < 0.5) {
        //                array.set(i, "1");
        //                i1++;
        //            }
        //        }
        //        System.out.println(i1);
        //        System.out.println(array);
        //        array.trimNulls();
        //        System.out.println(array);
        RenderedObjectManager mgr = new RenderedObjectManager();
        mgr.add(RenderedObjectType.of(ABC.class), new ABC());
        Collection<ABC> coll = mgr.getFor(RenderedObjectType.of(ABC.class));
        System.out.println(coll);
    }
    
    private static class ABC implements RenderedObject {
        
        @Override
        public boolean isVisible(FrustumIntersection frustum) {
            
            return false;
        }
        
    }
}
