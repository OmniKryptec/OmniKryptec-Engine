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

package de.omnikryptec.postprocessing.v2;

import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.resource.model.Model;
import de.omnikryptec.util.ModelUtil;

import java.util.HashMap;
import java.util.Map;

public class PostProcessingManager {
	
	private Model quad = ModelUtil.generateQuad();
	private Map<Integer, PostProcessor> extra = new HashMap<>();
	
	public void execute() {
		start();
		
		end();
	}
	
	private void start() {
		quad.getVao().bind(0, 1);
		GraphicsUtil.enableDepthTesting(false);
	}

	private void end() {
		GraphicsUtil.enableDepthTesting(true);
	}
}
