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

package de.omnikryptec.animation.ColladaParser.dataStructures;

import java.util.ArrayList;
import java.util.List;

public class VertexSkinData {

    public final List<Integer> jointIds = new ArrayList<>();
    public final List<Float> weights = new ArrayList<>();

    public void addJointEffect(int jointId, float weight) {
        for (int i = 0; i < weights.size(); i++) {
            if (weight > weights.get(i)) {
                jointIds.add(i, jointId);
                weights.add(i, weight);
                return;
            }
        }
        jointIds.add(jointId);
        weights.add(weight);
    }

    public void limitJointNumber(int max) {
        if (jointIds.size() > max) {
            float[] topWeights = new float[max];
            float total = saveTopWeights(topWeights);
            refillWeightList(topWeights, total);
            removeExcessJointIds(max);
        } else if (jointIds.size() < max) {
            fillEmptyWeights(max);
        }
    }

    private void fillEmptyWeights(int max) {
        while (jointIds.size() < max) {
            jointIds.add(0);
            weights.add(0f);
        }
    }

    private float saveTopWeights(float[] topWeightsArray) {
        float total = 0;
        for (int i = 0; i < topWeightsArray.length; i++) {
            topWeightsArray[i] = weights.get(i);
            total += topWeightsArray[i];
        }
        return total;
    }

    private void refillWeightList(float[] topWeights, float total) {
        weights.clear();
        for (int i = 0; i < topWeights.length; i++) {
            weights.add(Math.min(topWeights[i] / total, 1));
        }
    }

    private void removeExcessJointIds(int max) {
        while (jointIds.size() > max) {
            jointIds.remove(jointIds.size() - 1);
        }
    }

}
