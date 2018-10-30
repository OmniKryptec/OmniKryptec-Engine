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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import de.omnikryptec.old.graphics.OpenGL;

public class Query {

    private static final List<Query> qs = new ArrayList<>();

    public static final void cleanup() {
	for (Query q : qs) {
	    q.delete();
	}
	qs.clear();
    }

    public final int id;
    public final int type;

    public Query(int type) {
	this.type = type;
	this.id = OpenGL.gl15genQueries();
	qs.add(this);
    }

    public Query start() {
	OpenGL.gl15beginQuery(type, id);
	return this;
    }

    public Query stop() {
	OpenGL.gl15endQuery(id);
	return this;
    }

    public boolean isResultReady() {
	return OpenGL.gl15getQueryObjecti(id, GL15.GL_QUERY_RESULT_AVAILABLE) == GL11.GL_TRUE;
    }

    private void delete() {
	OpenGL.gl15deleteQueries(id);
    }
}
