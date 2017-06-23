package omnikryptec.test.saving;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import omnikryptec.logger.Logger;
import omnikryptec.util.AdvancedFile;

/**
 * XMLTest
 * @author Panzer1119
 */
public class XMLTest {
    
    private World world = null;
    
    public static final void main(String[] args) {
        try {
            DataMapSerializer dataMapSerializer = new DataMapSerializer();
            XMLTest test = new XMLTest();
            test.world = new World("Test_" + Math.random());
            Logger.log(test.world);
            dataMapSerializer.addObject(test.world);
            for(int i = 0; i < 0; i++) {
                dataMapSerializer.addObject(new World("Test_" + Math.random()));
            }
            HashMap<Class<?>, ArrayList<DataMap>> data_complete = dataMapSerializer.serialize();
            if(false) {
                Logger.log("data_complete.size() == " + data_complete.size());
                ArrayList<DataMap> data = data_complete.get(World.class);
                Logger.log("data.size() == " + data.size());
                for(DataMap d : data) {
                    Logger.log("DataMap found: " + d.getName());
                    World world = World.fromDataMap(d);
                    Logger.log("World unserialized: " + world);
                }
            } else {
                if(false) {
                    DataMapSerializer.unserialize(data_complete);
                    HashMap<Class<?>, ArrayList<DataMapSerializable>> xmlsc = dataMapSerializer.getClassesDataMapSerializables();
                    Logger.log("xmlsc.size() == " + xmlsc.size());
                    ArrayList<DataMapSerializable> xmls = xmlsc.get(World.class);
                    Logger.log("xmls.size() == " + xmls.size());
                    for(DataMapSerializable xml : xmls) {
                        Logger.log("Found XMLSerializable (" + xml.getClass().getName() + "): " + xml);
                    }
                } else {
                    /*
                    ArrayList<World> worlds = dataMapSerializer.getObjects(World.class);
                    Logger.log("worlds.size() == " + worlds.size());
                    for(World world : worlds) {
                        Logger.log("Found World: " + world);
                    }
                    */
                    AdvancedFile file = new AdvancedFile(new File("E:\\Daten\\NetBeans\\Projekte\\OmniKryptec-Engine\\temp"), "Test.xml");
                    //Logger.log("file == " + file);
                    //Logger.log("file.toFile() == " + file.toFile());
                    //Logger.log("file.createFile() == " + file.createFile());
                    dataMapSerializer.serialize("Welt_1", XMLSerializer.newInstance(), file);
                    Logger.log("file.exists() == " + file.exists());
                    HashMap<Class<?>, ArrayList<DataMapSerializable>> xmlsc = DataMapSerializer.unserializeToDDataMapSerializable(file, XMLSerializer.newInstance());
                }
            }
            System.exit(0);
        } catch (Exception ex) {
            Logger.logErr("Main error: " + ex, ex);
            System.exit(-1);
        }
    }
    
}
