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

package de.omnikryptec.old.animation.ColladaParser.dataStructures;

/**
 * Contains the extracted data for an animation, which includes the length of
 * the entire animation and the data for all the keyframes of the animation.
 * 
 * @author Karl
 *
 */
public class AnimationData {

    public final float lengthSeconds;
    public final KeyFrameData[] keyFrames;

    public AnimationData(float lengthSeconds, KeyFrameData[] keyFrames) {
	this.lengthSeconds = lengthSeconds;
	this.keyFrames = keyFrames;
    }

}
