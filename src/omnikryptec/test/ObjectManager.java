/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package omnikryptec.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import omnikryptec.logger.Logger;
import omnikryptec.storing.GameObject;

/**
 *
 * @author Panzer1119
 */
public class ObjectManager {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static File saveGameObjects(GameObject[] gameObjects, File file, boolean override) {
        if(file == null || (file.exists() && !override)) {
            return null;
        }
        try {
            if(gameObjects == null) {
                file.delete();
                if(!override) {
                    file.createNewFile();
                }
            } else {
                if(!file.exists()) {
                    file.createNewFile();
                }
                final FileOutputStream fos = new FileOutputStream(file);
                final BufferedOutputStream bos = new BufferedOutputStream(fos);
                for(GameObject go : gameObjects) {
                    mapper.writeValue(bos, go);
                }
                bos.close();
                fos.close();
            }
            return file;
        } catch (Exception ex) {
            Logger.logErr("Error while saving GameObject(s) to file: " + ex, ex);
            return null;
        }
    }
    
    public static void main(String[] args) {
        try {
            TestClass obj = new TestClass("Troll", 22);
            String test = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            Logger.log("Here:\n" + test);
            GameObject test_2 = new GameObject();
            saveGameObjects(new GameObject[] {test_2}, new File("test_save.txt"), true);
            //System.exit(0);
        } catch (Exception ex) {
        }
    }
    
    public static class TestClass {
        
        private String test = "";
        private int test_2 = 0;
        
        public TestClass(String test, int test_2) {
            this.test = test;
            this.test_2 = test_2;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public int getTest_2() {
            return test_2;
        }

        public void setTest_2(int test_2) {
            this.test_2 = test_2;
        }
        
    }
    
}
