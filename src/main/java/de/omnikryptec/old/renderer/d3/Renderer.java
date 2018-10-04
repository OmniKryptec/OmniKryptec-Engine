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

package de.omnikryptec.old.renderer.d3;

import java.util.List;

import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.base.ShaderPack;
import de.omnikryptec.old.util.FrustrumFilter;
import de.omnikryptec.old.util.KeyArrayHashMap;
import de.omnikryptec.old.util.Level;
import de.omnikryptec.old.util.Priority;
import de.omnikryptec.old.util.Util;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

@Priority
@Level
public abstract class Renderer{
	
	
    private final float lvl;
    private final float prio;
    protected ShaderPack shaderpack;
	protected FrustrumFilter filter = new FrustrumFilter();
	protected boolean usesShader=true;
    
	/**
	 * Doesn't use any shaders ({@link #usesShader} is set to false)
	 */
	protected Renderer() {
		this(null);
		usesShader = false;
	}
	
    protected Renderer(ShaderPack myshader) {
        this.shaderpack = myshader;
        lvl = Util.extractLvl(getClass(), 0);
        prio = Util.extractPrio(getClass(), 0);
    }

    protected abstract long render(AbstractScene3D s, KeyArrayHashMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter);

    private Shader tmps = null;
    private long tmplong;
    
    public final long render(AbstractScene3D s, KeyArrayHashMap<AdvancedModel, List<Entity>> entities, RenderConfiguration config) {
    	cur = config;
    	if(usesShader) {
	        tmps = shaderpack.getShader(config.getShaderpackKey(), config.getShaderLvl());
	        if (tmps == null) {
	            if (Logger.isDebugMode()) {
	                Logger.log("Shader is null! (RenderPass \""+config.getShaderpackKey()+"\"", LogLevel.ERROR);
	            }
	            return 0;
	        }
	        tmps.start();
	        tmps.onRenderStart(s, config.getClipPlane());
    	}else {
    		tmps = null;
    	}
    	if (filter!=null) {
        	filter.setCamera(s.getCamera());
        }
        tmplong = render(s, entities, tmps, filter);
        if(usesShader) {
        	tmps.onRenderEnd(s);
        }
        cur = null;
        return tmplong;
    }
    private RenderConfiguration cur;
    
    protected final RenderConfiguration getCurrentRenderConfig() {
    	return cur;
    }
    
    public FrustrumFilter getFrustrumFilter(){
    	return filter;
    }
    
    public Renderer setFrustrumFilter(FrustrumFilter filter){
    	this.filter = filter;
    	return this;
    }
    
    public final float expensiveLevel() {
        return lvl;
    }

    public final float priority() {
        return prio;
    }

    
    public ShaderPack getShaderPack(){
    	return shaderpack;
    }

}
