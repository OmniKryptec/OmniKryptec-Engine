/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.main;

import de.omnikryptec.old.gameobject.GameObject;

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
	return size() == 0;
    }
}
