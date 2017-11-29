package omnikryptec.main;

import omnikryptec.gameobject.GameObject;

public interface GameObjectContainer<T extends GameObject> {
	
	public boolean addGameObject(T go);

	public T removeGameObject(T go, boolean delete);
	
	public default T removeGameObject(T go) {
		return removeGameObject(go, true);
	}
}
