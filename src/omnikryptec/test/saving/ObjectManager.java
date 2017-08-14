/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package omnikryptec.test.saving;

import com.fasterxml.jackson.databind.ObjectMapper;

import omnikryptec.gameobject.GameObject;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class ObjectManager {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static AdvancedFile saveObjectToFile(Object object, AdvancedFile file, boolean override) {
        if (file == null || (file.exists() && !override)) {
            return null;
        }
        try {
            if (object == null) {
                file.delete();
                if (!override) {
                    file.createFile();
                }
            } else {
                if (!file.exists() || override) {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createFile();
                }
                mapper.writeValue(file.toFile(), object);
            }
            return file;
        } catch (Exception ex) {
            Logger.logErr("Error while saving object to file: " + ex, ex);
            return null;
        }
    }

    public static <T> T loadObjectFromFile(AdvancedFile file, T T) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        try {
            return (T) mapper.readValue(file.toFile(), T.getClass());
        } catch (Exception ex) {
            Logger.logErr("Error while loading object from file: " + ex, ex);
            return null;
        }
    }

    //FIXME fixen
    public static void main(String[] args) {
        try {
            TestClass obj = new TestClass("Troll", 22);
            AdvancedFile file_2 = new AdvancedFile("test_2.txt");
            saveObjectToFile(obj, file_2, true);
            TestClass temp = loadObjectFromFile(file_2, TestClass.createInstance());
            // Logger.log("Here:\n" + test);
            GameObject test_2 = new GameObject();
            //test_2.setPos(new Vector3f(0F, 1F, 2F));
            // Logger.log("Here 2:\n" +
            // mapper.writerWithDefaultPrettyPrinter().writeValueAsString(test_2));
            // new GameObject[] {test_2}
            GameObject[] test_array = new GameObject[10];
            for (int i = 0; i < test_array.length; i++) {
//                test_array[i] = new GameObject()
//                        .setPos(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
            }

            AdvancedFile file = new AdvancedFile("test_world.txt");
            World world = new World("Test Welt", test_array);
            Logger.log(world);
            world.save(file);

            World world_second = World.loadWorldFromFile(file);
            Logger.log(world_second);
            // saveGameObjects(test_array, new AdvancedFile("test_save.txt"), true);
            // Thread.sleep(1000);
        } catch (Exception ex) {
        }
    }

    public static class TestClass {

        private String test = "";
        private int test_2 = 0;

        public TestClass() {
            this("", 0);
        }

        public TestClass(String test, int test_2) {
            this.test = test;
            this.test_2 = test_2;
        }

        public String getTest() {
            return test;
        }

        public TestClass setTest(String test) {
            this.test = test;
            return this;
        }

        public int getTest_2() {
            return test_2;
        }

        public TestClass setTest_2(int test_2) {
            this.test_2 = test_2;
            return this;
        }

        public static TestClass createInstance() {
            return new TestClass();
        }

    }

}
