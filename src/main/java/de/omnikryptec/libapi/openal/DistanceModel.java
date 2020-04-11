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

package de.omnikryptec.libapi.openal;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public enum DistanceModel {
    EXPONENT(AL11.AL_EXPONENT_DISTANCE, false), EXPONENT_CLAMPED(AL11.AL_EXPONENT_DISTANCE_CLAMPED, true),
    INVERSE(AL10.AL_INVERSE_DISTANCE, false), INVERSE_CLAMPED(AL10.AL_INVERSE_DISTANCE_CLAMPED, true),
    LINEAR(AL11.AL_LINEAR_DISTANCE, false), LINEAR_CLAMPED(AL11.AL_LINEAR_DISTANCE_CLAMPED, true),
    NONE(AL10.AL_NONE, false);

    private final int distanceModel;
    private final boolean clamped;

    /**
     * Creates the DistanceModel
     *
     * @param distanceModel Integer OpenAL DistanceModel
     * @param clamped       Boolean <tt>true</tt> if it is clamped
     */
    DistanceModel(int distanceModel, boolean clamped) {
        this.distanceModel = distanceModel;
        this.clamped = clamped;
    }

    /**
     * Returns the OpenAL-Equivalent for the DistanceModel
     *
     * @return Integer OpenAL Distancemodel
     */
    public final int getDistanceModelId() {
        return this.distanceModel;
    }

    /**
     * Returns if the DistanceModel is a clamped one
     *
     * @return <tt>true</tt> if it is clamped
     */
    public final boolean isClamped() {
        return this.clamped;
    }

    public final float getFade(float fadeTime, float fadeTimeComplete, float volumeStart, float volumeTarget) {
        float newVolume = 0;
        switch (this) {
        case EXPONENT:
        case EXPONENT_CLAMPED:
        case INVERSE:
        case INVERSE_CLAMPED:
            newVolume = (1.0F / (fadeTime + (1 / volumeStart))) + volumeTarget
                    - (1.0F / (fadeTimeComplete + (1.0F / volumeStart)));
            break;
        case LINEAR:
        case LINEAR_CLAMPED:
        case NONE:
            newVolume = fadeTime * ((volumeTarget - volumeStart) / fadeTimeComplete) + volumeStart;
            break;

        }
        return Math.max(newVolume, 0.0F);
    }
}
