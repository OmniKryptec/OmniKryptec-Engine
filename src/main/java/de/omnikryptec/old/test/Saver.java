/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.test;

import java.util.HashMap;

/**
 *
 * @author Panzer1119
 */
public class Saver {

    public static boolean saveToFile(Object object) {
	if (object instanceof Saveable) {
	    Saveable toSave = (Saveable) object;
	    final HashMap<String, Object> data = dataToHashMap(toSave.toData());
	    // Hier zu JSON?
	    // Und falls eines der Objects auch "Saveable" implementiert diese
	    // Funtkion hier nochmal auf das dann ausführen und immer so weiter
	    return true;
	} else {
	    return false;
	}
    }

    public static HashMap<String, Object> dataToHashMap(Object[] data) {
	if (data.length % 2 != 0) {
	    return null;
	}
	final HashMap<String, Object> hashMap = new HashMap<>();
	for (int i = 0; i < data.length - 1; i += 2) {
	    hashMap.put((String) data[i], data[i + 1]);
	}
	return hashMap;
    }

}
