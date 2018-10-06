package de.omnikryptec.util;

import java.util.Iterator;

import de.omnikryptec.util.data.DynamicArray;

public class Test {

	public static void main(String[] args) {
		DynamicArray<Object> da = new DynamicArray<>();
		da.add(0, "1");
		da.add(1, "2");
		da.add(2, "3");
		da.remove(0);
		Iterator<Object> it = da.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
		da.remove(1);
		da.remove(2);
	}

}
