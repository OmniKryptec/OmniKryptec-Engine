package de.omnikryptec.util.data;

public class SimpleDynamicArray<E> {

	private Object[] array;
	private int grow;
	
	public SimpleDynamicArray() {
		this(10, 2);
	}
	
	public SimpleDynamicArray(int initialSize, int grow) {
		this.array = new Object[grow];
		this.grow = grow;
	}
	
	public void set(int index, E e) {
		if(index>=array.length) {
			grow();
		}
		array[index] = e;
	}
	
	public E get(int index) {
		if(index<0||index>=size()) {
			return null;
		}
		return (E)array[index];
	}
	
	private void grow() {
		Object[] newArray = new Object[size() + grow];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}
	
	public int size() {
		return array.length;
	}
}
