package omnikryptec.gameobject.particlesV2;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class ParticleFloatBuffer {

	private FloatBuffer[] buffers;
	private int read = 0;
	private int write = 1;
	private boolean openclmode = false;

	public ParticleFloatBuffer(int size) {
		buffers = new FloatBuffer[2];
		for (int i = 0; i < buffers.length; i++) {
			buffers[i] = BufferUtils.createFloatBuffer(size);
		}
	}

	public void resize(int newsize) {
		for (int i = 0; i < buffers.length; i++) {
			buffers[i].flip();
			float[] array = buffers[i].array();
			int limit = buffers[i].limit();
			buffers[i].clear();
			buffers[i] = BufferUtils.createFloatBuffer(newsize);
			buffers[i].put(array, 0, Math.min(newsize, limit));
		}
	}

	public void unreadyCL() {
		read++;
		read %= buffers.length;
		buffers[read].flip();
		write++;
		write %= buffers.length;
		openclmode = false;
	}

	public void readyCL() {
		openclmode = true;
		buffers[read].flip();
	}
	
	public boolean isOpenCLReady() {
		return openclmode;
	}
	
	public FloatBuffer getReadBuffer() {
		return buffers[read];
	}
	
	public FloatBuffer getWriteBuffer() {
		return openclmode?buffers[write]:buffers[read];
	}

	// private float[] stash;
	// private boolean stashing=false;
	// private FloatBuffer[] buffers;
	// private int readIndex, writeIndex;
	//
	// public ParticleFloatBuffer(int size, int singlesize) {
	// if(size<2) {
	// throw new IllegalArgumentException("The size of the FloatBufferBuffer must be
	// >= 2");
	// }
	// buffers = new FloatBuffer[size];
	// stash = new float[singlesize];
	// for(int i=0; i<buffers.length; i++) {
	// buffers[i] = BufferUtils.createFloatBuffer(singlesize);
	// }
	// reset();
	// }
	//
	//// public void put(float f) {
	//// buffers[writeIndex].put(f);
	//// }
	////
	//// public float get() {
	//// return buffers[readIndex].get();
	//// }
	//
	// public void put(int index, float f) {
	// if(stashing) {
	// stash[index] = f;
	// }else {
	// buffers[writeIndex].put(index, f);
	// }
	// }
	//
	// public float get(int index) {
	// return buffers[readIndex].get(index);
	// }
	//
	// /**
	// * if stashing is not enabled use only once after one advancement!
	// * @param index
	// * @param f
	// */
	// public void add(int index, float f) {
	// if(stashing) {
	// stash[index] += f;
	// }else {
	// put(index, get(index)+f);
	// }
	// }
	//
	// private void applyStash() {
	// if(stashing) {
	// for(int i=0; i<stash.length; i++) {
	// buffers[writeIndex].put(i, get(i)+stash[i]);
	// stash[i] = 0;
	// }
	// }
	// }
	//
	// public void advance(boolean enableStashing) {
	// applyStash();
	// readIndex++;
	// readIndex %= buffers.length;
	// writeIndex++;
	// writeIndex %= buffers.length;
	// buffers[readIndex].flip();
	// stashing = enableStashing;
	// }
	//
	// public FloatBuffer getWriteBuffer() {
	// return buffers[writeIndex];
	// }
	//
	// public FloatBuffer getReadBuffer() {
	// return buffers[readIndex];
	// }
	//
	// public void changeSize(int newSingleSize) {
	// for(int i=0; i<buffers.length; i++) {
	// buffers[i].flip();
	// float[] array = buffers[i].array();
	// int limit = buffers[i].limit();
	// buffers[i].clear();
	// buffers[i] = BufferUtils.createFloatBuffer(newSingleSize);
	// buffers[i].put(array, 0, Math.min(newSingleSize, limit));
	// }
	// float[] oldstash = stash;
	// stash = new float[newSingleSize];
	// System.arraycopy(oldstash, 0, stash, 0, Math.min(newSingleSize,
	// oldstash.length));
	// }
	//
	// public void reset() {
	// readIndex = -1;
	// writeIndex = 0;
	// advance(false);
	// }
	//
	// public void transferAll() {
	// buffers[readIndex].flip();
	// buffers[writeIndex].flip();
	// buffers[writeIndex].put(buffers[readIndex]);
	// }
}
