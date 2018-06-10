package omnikryptec.gameobject.particlesV2;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class FloatBufferBuffer {

	private FloatBuffer[] buffers;
	//private boolean[] readMode;
	private int readIndex, writeIndex;

	public FloatBufferBuffer(int size, int singlesize) {
		if(size<2) {
			throw new IllegalArgumentException("The size of the FloatBufferBuffer must be >= 2");
		}
		buffers = new FloatBuffer[size];
		for(int i=0; i<buffers.length; i++) {
			buffers[i] = BufferUtils.createFloatBuffer(singlesize);
		}
		reset();
	}

	public void put(float f) {
		buffers[writeIndex].put(f);
	}

	public float get() {
		return buffers[readIndex].get();
	}

	public void put(int index, float f) {
		buffers[writeIndex].put(index, f);
	}

	public float get(int index) {
		return buffers[readIndex].get(index);
	}

	public void advance() {
		readIndex++;
		readIndex %= buffers.length;
		writeIndex++;
		writeIndex %= buffers.length;
		buffers[readIndex].flip();
		//readMode[readIndex] = true;
		//readMode[writeIndex] = false;
	}
	
	public FloatBuffer getWriteBuffer() {
		return buffers[writeIndex];
	}
	
	public FloatBuffer getReadBuffer() {
		return buffers[readIndex];
	}
	
	public void changeSize(int newsize) {
		for(int i=0; i<buffers.length; i++) {
			buffers[i].flip();
			float[] array = buffers[i].array();
			int limit = buffers[i].limit();
			buffers[i].clear();
			buffers[i] = BufferUtils.createFloatBuffer(newsize);
			buffers[i].put(array, 0, Math.min(newsize, limit));
		}
	}
	
	public void reset() {
		readIndex = -1;
		writeIndex = 0;
		advance();
	}
	
	public void transferAll() {
		buffers[readIndex].flip();
		buffers[writeIndex].flip();
		buffers[writeIndex].put(buffers[readIndex]);
	}
}
