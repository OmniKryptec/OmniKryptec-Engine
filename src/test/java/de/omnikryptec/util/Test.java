package de.omnikryptec.util;

import de.omnikryptec.util.data.DynamicArray;

public class Test {

	public static void main(String[] args) {
		DynamicArray<Object> da = new DynamicArray<>();
		da.add(0, new Object());
		da.add(1, new Object());
		da.add(2, new Object());
		da.remove(2);
		da.remove(1);
		da.remove(0);
	}

}
