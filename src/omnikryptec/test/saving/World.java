/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package omnikryptec.test.saving;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.entity.GameObject;
import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class World implements DataMapSerializable {

	private String name = "";
	private GameObject[] gameObjects = null;

	public World() {
		this("");
	}

	public World(String name) {
		this(name, null);
	}

	public World(String name, GameObject[] gameObjects) {
		this.name = name;
		this.gameObjects = gameObjects;
	}

        @Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GameObject[] getGameObjects() {
		return gameObjects;
	}

	public World setGameObjects(GameObject[] gameObjects) {
		this.gameObjects = gameObjects;
		return this;
	}

	public World save(File file) {
		ObjectManager.saveObjectToFile(this, file, true);
		return this;
	}

	@Override
	public String toString() {
		return String.format("World \"%s\" with %d GameObjects", name, (gameObjects != null ? gameObjects.length : 0));
	}

	public static World createInstance() {
		return new World();
	}

	public static World loadWorldFromFile(File file) {
		return ObjectManager.loadObjectFromFile(file, World.createInstance());
	}

    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", name);
        final ArrayList<Object> weights = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            weights.add((float) (Math.random() * 10.0));
            weights.add(weights.clone());
        }
        final HashMap<Object, Object> test = new HashMap<>();
        for(int i = 0; i < 3; i++) {
            test.put(name + i, i * 1040L);
            final HashMap<String, Long> test_2 = new HashMap<>();
            for(int z = 0; z < 3; z++) {
                test_2.put(name + z, z * 1040L);
            }
            test.put((i * 4), test_2);
        }
        data.put("weights", weights);
        data.put("test", test);
        data.put("array", new int[] {1, 2, 4545, 45});
        return data;
    }

    public static World fromDataMap(DataMap data) {
        Logger.log("Created World data.size() == " + data.size());
        for(String g : data.keySet()) {
            Logger.log(g + " " + data.get(g).getClass().getName() + " " + data.get(g));
        }
        return new World(data.get("name").toString());
    }

}
