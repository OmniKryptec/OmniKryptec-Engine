package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;

public interface LoadingProgressCallback {

    void onLoadingStart(int globalResMax, int globalMaxStages);

    void onStageChange(AdvancedFile superfile, int stageResMax, int stageNumber);

    void onProgressChange(AdvancedFile file, int stageResProcessedCount);

    void onLoadingDone();

}
