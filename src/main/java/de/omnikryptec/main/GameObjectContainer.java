package de.omnikryptec.main;

import de.omnikryptec.gameobject.GameObject;

public interface GameObjectContainer<T extends GameObject> {
	
	public void addGameObject(T go, boolean added);

	public default void addGameObject(T go) {
		addGameObject(go, true);
	}
	
	public T removeGameObject(T go, boolean delete);
	
	public default T removeGameObject(T go) {
		return removeGameObject(go, true);
	}
	
	public int size();
	
	public default boolean isEmpty() {
		return size()==0;
	}
}
