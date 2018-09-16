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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omnikryptec.test.saving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.gameobject.GameObject;
import de.omnikryptec.util.logger.Logger;

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

    public World save(AdvancedFile file) {
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

    public static World loadWorldFromFile(AdvancedFile file) {
        return ObjectManager.loadObjectFromFile(file, World.createInstance());
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", name);
        final ArrayList<Object> weights = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            weights.add((float) (Math.random() * 10.0));
            weights.add(weights.clone());
        }
        final HashMap<Object, Object> test = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            test.put(name + i, i * 1040L);
            final HashMap<String, Long> test_2 = new HashMap<>();
            for (int z = 0; z < 3; z++) {
                test_2.put(name + z, z * 1040L);
            }
            test.put((i * 4), test_2);
        }
        data.put("weights", weights);
        data.put("test", test);
        int[] array_1d = new int[]{1, 2, 4545, 45};
        int[][] array_2d = new int[][]{array_1d, array_1d};
        int[][][] array_3d = new int[][][]{array_2d, array_2d};
        data.put("array_1d", array_1d);
        data.put("array_2d", array_2d);
        data.put("array_3d", array_3d);
        return data;
    }

    public static World newInstanceFromDataMap(DataMap data) {
        return new World().fromDataMap(data);
    }

    @Override
    public World fromDataMap(DataMap data) {
        Logger.log("Created World data.size() == " + data.size());
        for (String g : data.keySet()) {
            Object object = data.get(g);
            String text = "" + object;
            Class<?> c = object.getClass();
            if (c.isArray()) {
                text = Arrays.deepToString((Object[]) object);
            }
            Logger.log(g + " " + c.getName() + " " + text);
        }
        Logger.log(((Object[]) ((Object[]) ((Object[]) data.get("array_3d"))[0])[0]).getClass());
        setName(data.getString("name"));
        return this;
    }

}
