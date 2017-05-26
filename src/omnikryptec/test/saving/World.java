/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package omnikryptec.test.saving;

import java.io.File;

import omnikryptec.entity.GameObject;

/**
 *
 * @author Panzer1119
 */
public class World {
    
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
        return String.format("World \"%s\" with %d GameObjects", name, gameObjects.length);
    }
    
    public static World createInstance() {
        return new World();
    }
    
    public static World loadWorldFromFile(File file) {
        return ObjectManager.loadObjectFromFile(file, World.createInstance());
    }
    
}
