package omnikryptec.test;

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
