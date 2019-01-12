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

package de.omnikryptec.resource.loader;

import java.util.stream.Collectors;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.resource.loadervpc.ResourceLoader;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;

public class Test {
    
    public static void main(final String[] args) {
        final ResourceProcessor p = new ResourceProcessor();
        p.addLoader(new ResourceLoader<String>() {
            
            @Override
            public String load(final AdvancedFile file) throws Exception {
                return file.createBufferedReader().lines().collect(Collectors.joining("\n"));
            }
            
            @Override
            public String getFileNameRegex() {
                return ".*java";
            }
            
            @Override
            public boolean requiresMainThread() {
                return false;
            }
        });
        p.addCallback(new LoadingProgressCallback() {
            
            @Override
            public void onLoadingStart(final int max, final int maxstages) {
                System.out.println("Max: " + max + " MaxS: " + maxstages);
            }
            
            @Override
            public void onStageChange(final AdvancedFile file, final int localmax, final int stagenumber) {
                System.out.println("Stagechange, lmax: " + localmax + " S#: " + stagenumber);
            }
            
            @Override
            public void onProgressChange(final AdvancedFile f, final int localprocessed) {
                System.out.println("P:" + localprocessed);
            }
            
            @Override
            public void onLoadingDone() {
                System.out.println("Done!!!");
            }
            
        });
        p.stage(new AdvancedFile("src/main/java"));
        p.stage(new AdvancedFile("src/test/java"));
        p.processStaged(true);
        //System.out.println(p.getProvider().getAll(String.class).size());
        // System.out.println(p.getProvider().get(String.class,
        // "de:omnikryptec:core:Updateable.java"));
    }
    
}
