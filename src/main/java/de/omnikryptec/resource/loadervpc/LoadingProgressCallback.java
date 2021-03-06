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

package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.util.Logger;

public interface LoadingProgressCallback {
    
    public static LoadingProgressCallback DEBUG_CALLBACK = new LoadingProgressCallback() {
        
        private final Logger logger = Logger.getLogger(this.getClass());
        
        private int maxs = 0;
        private int lrmax = 0;
        
        @Override
        public void onStageChange(final AdvancedFile superfile, final int stageResMax, final int stageNumber) {
            this.lrmax = stageResMax;
            this.logger.info("S: " + superfile + ", " + (stageNumber + 1) + "/" + this.maxs);
        }
        
        @Override
        public void onProgressChange(final AdvancedFile file, int i, final int stageResProcessedCount) {
            this.logger.info(" F: " + file + ", " + stageResProcessedCount + "/" + this.lrmax);
        }
        
        @Override
        public void onLoadingStart(final int globalResMax, final int globalMaxStages) {
            this.maxs = globalMaxStages;
            this.logger.info("Loading " + globalResMax + " resource(s) in " + globalMaxStages + " stage(s)");
        }
        
        @Override
        public void onLoadingDone() {
            this.logger.info("Finished loading");
        }
    };
    
    void onLoadingStart(int globalResMax, int globalMaxStages);
    
    void onStageChange(AdvancedFile superfile, int stageResMax, int stageNumber);
    
    void onProgressChange(AdvancedFile file, int globalResProcessedCount, int stageResProcessedCount);
    
    void onLoadingDone();
    
}
