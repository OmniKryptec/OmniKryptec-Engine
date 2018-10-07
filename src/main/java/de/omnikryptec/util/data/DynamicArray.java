package de.omnikryptec.util.data;

public class DynamicArray<E> {

	private Object[] array;
	private int grow;
	
	public DynamicArray() {
		this(10, 2);
	}
	
	public DynamicArray(int initialSize, int grow) {
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(this == obj) {
			return true;
		}
		if(obj instanceof DynamicArray) {
			DynamicArray<?> other = (DynamicArray<?>)obj;
			if(other.size()!=this.size()) {
				return false;
			}
			for(int i=0; i<size(); i++) {
				if(!other.get(i).equals(get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
