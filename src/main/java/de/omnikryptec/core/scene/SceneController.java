package de.omnikryptec.core.scene;

public class SceneController {

    private Scene currentScene;

    public Scene getCurrentScene() {
        return currentScene;
    }

    public Scene setCurrentScene(Scene scene) {
        Scene old = currentScene;
        this.currentScene = scene;
        return old;
    }
}
