package de.omnikryptec.util.data;

import java.lang.reflect.Array;

public class FixedStack<T> {

	private T[] array;
	private int index = 0;

	public FixedStack(Class<T> clazz, int size) {
		array = (T[]) Array.newInstance(clazz, size);
	}

	public void push(T i) {
		array[index] = i;
		index++;
	}

	public T pop() {
		index--;
		return array[index];
	}

	public T top() {
		return array[index - 1];
	}

	public boolean isEmpty() {
		return index == 0;
	}

	public boolean isFull() {
		return index == array.length;
	}

	public int filledSize() {
		return index;
	}
	
	public int totalSize() {
		return array.length;
	}

	public void clear() {
		for (int i = 0; i < index; i++) {
			array[i] = null;
		}
		index = 0;
	}
}
