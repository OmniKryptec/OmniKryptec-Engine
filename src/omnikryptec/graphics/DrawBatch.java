/**
 * Copyright (c) 2012, Matt DesLauriers All rights reserved.
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice, this
 *	  list of conditions and the following disclaimer.
 *
 *	* Redistributions in binary
 *	  form must reproduce the above copyright notice, this list of conditions and
 *	  the following disclaimer in the documentation and/or other materials provided
 *	  with the distribution.
 *
 *	* Neither the name of the Matt DesLauriers nor the names
 *	  of his contributors may be used to endorse or promote products derived from
 *	  this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *	POSSIBILITY OF SUCH DAMAGE.
 */
package omnikryptec.graphics;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.linearmath.Transform;

import omnikryptec.gameobject.Camera;
import omnikryptec.resource.model.VertexArrayObject;
import omnikryptec.resource.texture.Texture;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.render.Shader2D;
import omnikryptec.util.Color;
import omnikryptec.util.Instance;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.exceptions.OmniKryptecException;

public class DrawBatch {
	private static final int floatsPerVertex = 8;
	private static final int[] elementLengths = { 2, 4, 2 };
	private static final int vertices = 6;

	private static Shader2D defShaderInst;

	private static Shader2D defaultShader() {
		if (defShaderInst == null) {
			defShaderInst = new Shader2D();
		}
		return defShaderInst;
	}

	private FloatBuffer buffer;
	private Shader program;

	private VertexArrayObject vao;
	private int vertexcount = 0;
	private int drawcalls = 0;
	private int idx = 0;
	private int max = 0;
	private Texture cur;
	private boolean drawing = false;
	private Color color = new Color(1, 1, 1, 1);
	private Camera camera;
	
	public DrawBatch(Camera cam) {
		this(cam, 1000);
	}

	public DrawBatch(Camera cam, int size) {
		this(cam, defaultShader(), size);
	}

	public DrawBatch(Camera cam, Shader program, int size) {
		this.program = program;
		this.camera = cam;
		vao = VertexArrayObject.create();
		max = size * vertices;
		buffer = BufferUtils.createFloatBuffer(floatsPerVertex * max);
	}

	public Camera getCamera() {
		return camera;
	}
	
	public DrawBatch setCamera(Camera cam) {
		if (drawing) {
			throw new OmniKryptecException("Can't change the camera while rendering!");
		}else {
			this.camera = cam;
		}
		return this;
	}
	
	public void drawTest() {
		vertex(0, 0, 1, 1, 1, 1, 0, 0);
		vertex(1, 0, 1, 1, 1, 1, 1, 0);
		vertex(1, 1, 1, 1, 1, 1, 1, 1);
	}

	private void checkFlush(Texture t) {
		if (t != cur || idx >= max) {
			Texture.unbindActive();
			cur = t;
			if (cur != null) {
				cur.bindToUnitOptimized(0);
			}
			flush();
		}
	}

	public void begin() {
		if (drawing) {
			throw new OmniKryptecException("Can't start started rendering!");
		}
		RenderUtil.enableAlphaBlending();
		idx = 0;
		vertexcount = 0;
		drawcalls = 0;
		program.start();
		program.onDrawBatchStart(this);
		drawing = true;
	}

	public void end() {
		if (!drawing) {
			throw new OmniKryptecException("Can't stop stopped rendering!");
		}
		flush();
		program.onDrawBatchEnd(this);
		drawing = false;
	}

	public void changeShader(Shader sh) {
		flush();
		program = sh;
		if (drawing) {
			program.start();
		}
	}

	public Color color() {
		return color;
	}

	private void flush() {
		if (idx > 0 && drawing) {
			buffer.flip();
			vao.bind();
			vao.storeBufferf(vertexcount, elementLengths, buffer);
			render();
			idx = 0;
			buffer.clear();
		}
	}

	private void render() {
		drawcalls++;
		vao.bind(0, 1, 2);
		OpenGL.gl11drawArrays(GL11.GL_TRIANGLES, 0, idx);
	}

	private void vertex(float x, float y, float r, float g, float b, float a, float u, float v) {
		if (!drawing) {
			throw new OmniKryptecException("Rendering is not active!");
		}
		vertexcount++;
		idx++;
		buffer.put(x).put(y).put(r).put(g).put(b).put(a).put(u).put(v);
	}

	public void draw(Texture tex, float x, float y) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight());
	}

	public void draw(Texture tex, float x, float y, float originX, float originY, float rotationRadians) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight(), originX, originY, rotationRadians);
	}

	private float x1, y1, x2, y2, x3, y3, x4, y4, scaleX, scaleY, cx, cy, p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y, r, g,
			b, a, u, v, u2, v2;

	public void draw(Texture tex, float x, float y, float width, float height) {
		draw(tex, x, y, width, height, x, y, 0f);
	}

	public void draw(Texture tex, float x, float y, float u, float v, float u2, float v2) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight(), u, v, u2, v2);
	}
	
	public void draw(Texture tex, float x, float y, float width, float height, float u, float v, float u2, float v2) {
		draw(tex, x, y, width, height, x, y, 0, u, v, u2, v2);
	}
	
	public void draw(Texture tex, float x, float y, float width, float height, float originX, float originY,
			float rotationRadians) {
		if (tex != null) {
			u = tex.getUVs()[0];
			v = tex.getUVs()[1];
			u2 = tex.getUVs()[2];
			v2 = tex.getUVs()[3];
		} else {
			u = -1;
			v = -1;
			u2 = -1;
			v2 = -1;
		}
		draw(tex, x, y, width, height, originX, originY, rotationRadians, u, v, u2, v2);
	}
	
	public void draw(Texture tex, float x, float y, float width, float height, float originX, float originY,
			float rotationRadians, float u, float v, float u2, float v2) {
		checkFlush(tex);
		r = color.getR();
		g = color.getG();
		b = color.getB();
		a = color.getA();
		if (rotationRadians != 0) {
			scaleX = 1f;// width/tex.getWidth();
			scaleY = 1f;// height/tex.getHeight();

			cx = originX * scaleX;
			cy = originY * scaleY;

			p1x = -cx;
			p1y = -cy;
			p2x = width - cx;
			p2y = -cy;
			p3x = width - cx;
			p3y = height - cy;
			p4x = -cx;
			p4y = height - cy;

			final float cos = (float) Math.cos(rotationRadians);
			final float sin = (float) Math.sin(rotationRadians);

			x1 = x + (cos * p1x - sin * p1y) + cx; // TOP LEFT
			y1 = y + (sin * p1x + cos * p1y) + cy;
			x2 = x + (cos * p2x - sin * p2y) + cx; // TOP RIGHT
			y2 = y + (sin * p2x + cos * p2y) + cy;
			x3 = x + (cos * p3x - sin * p3y) + cx; // BOTTOM RIGHT
			y3 = y + (sin * p3x + cos * p3y) + cy;
			x4 = x + (cos * p4x - sin * p4y) + cx; // BOTTOM LEFT
			y4 = y + (sin * p4x + cos * p4y) + cy;
		} else {
			x1 = x;
			y1 = y;

			x2 = x + width;
			y2 = y;

			x3 = x + width;
			y3 = y + height;

			x4 = x;
			y4 = y + height;
		}

		// top left, top right, bottom left
		vertex(x1, y1, r, g, b, a, u, v);
		vertex(x2, y2, r, g, b, a, u2, v);
		vertex(x4, y4, r, g, b, a, u, v2);

		// top right, bottom right, bottom left
		vertex(x2, y2, r, g, b, a, u2, v);
		vertex(x3, y3, r, g, b, a, u2, v2);
		vertex(x4, y4, r, g, b, a, u, v2);
	}


	public void fillRect(float x, float y, float width, float height) {
		draw(null, x, y, width, height);
	}

	public void drawRect(float x, float y, float width, float height) {
		drawRect(x, y, width, height, 1);
	}

	public void drawRect(float x, float y, float width, float height, float thickness) {
		draw(null, x, y, width, thickness);
		draw(null, x, y, thickness, height);
		draw(null, x + width - thickness, y, thickness, height);
		draw(null, x, y + height - thickness, width, thickness);
	}

	public void fill3DRect(float x, float y, float width, float height, float shadowOffset) {
		float[] cur = color.getArray();
		color.set(0, 0, 0, 1);
		fillRect(x + shadowOffset, y + shadowOffset, width, height);
		color.setFrom(cur);
		fillRect(x, y, width, height);
	}

	public void drawLine(float x1, float y1, float x2, float y2) {
		drawLine(x1, y1, x2, y2, 1);
	}

	public void drawLine(float x1, float y1, float x2, float y2, float thickness) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		float rad = (float) Math.atan2(dx, dy);
		draw(null, x1, y1, dist, thickness, 0, 0, rad);
	}
	
	public int getDrawCalls() {
		return drawcalls;
	}

	public int getVertexCount() {
		return vertexcount;
	}

	public boolean isDrawing() {
		return drawing;
	}

	// public void drawRegion(Texture tex, float srcX, float srcY, float srcWidth,
	// float srcHeight, float dstX,
	// float dstY) {
	// drawRegion(tex, srcX, srcY, srcWidth, srcHeight, dstX, dstY, srcWidth,
	// srcHeight);
	// }
	//
	// public void drawRegion(Texture tex, float srcX, float srcY, float srcWidth,
	// float srcHeight, float dstX, float dstY,
	// float dstWidth, float dstHeight) {
	// u = srcX / tex.getWidth();
	// v = srcY / tex.getHeight();
	// u2 = (srcX + srcWidth) / tex.getWidth();
	// v2 = (srcY + srcHeight) / tex.getHeight();
	// draw(tex, dstX, dstY, dstWidth, dstHeight, u, v, u2, v2);
	// }
	//
	// public void drawRegion(TextureRegion region, float srcX, float srcY, float
	// srcWidth, float srcHeight, float dstX,
	// float dstY) {
	// drawRegion(region, srcX, srcY, srcWidth, srcHeight, dstX, dstY, srcWidth,
	// srcHeight);
	// }
	//
	// public void drawRegion(TextureRegion region, float srcX, float srcY, float
	// srcWidth, float srcHeight, float dstX,
	// float dstY, float dstWidth, float dstHeight) {
	// drawRegion(region.getTexture(), region.getRegionX() + srcX,
	// region.getRegionY() + srcY, srcWidth, srcHeight,
	// dstX, dstY, dstWidth, dstHeight);
	// }

	// public void draw(Texture tex, float x, float y, float width, float height,
	// float originX, float originY,
	// float rotationRadians) {
	// draw(tex, x, y, width, height, originX, originY, rotationRadians,
	// tex.getUVs()[0], tex.getUVs()[1], tex.getUVs()[2],
	// tex.getUVs()[3]);
	// }

	// public void draw(Texture tex, float x, float y, float width, float height) {
	// draw(tex, x, y, width, height, tex.getUVs()[0], tex.getUVs()[1],
	// tex.getUVs()[2], tex.getUVs()[3]);
	// }

}
