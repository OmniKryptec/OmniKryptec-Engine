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

package de.omnikryptec.old.test.saving;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.util.SerializationUtil;
import de.omnikryptec.old.util.logger.Commands;
import de.omnikryptec.old.util.logger.Logger;

/**
 * XMLTest
 *
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
            for (int i = 0; i < 0; i++) {
                dataMapSerializer.addObject(new World("Test_" + Math.random()));
            }
            HashMap<Class<?>, ArrayList<DataMap>> data_complete = dataMapSerializer.serialize();
            if (false) {
                Logger.log("data_complete.size() == " + data_complete.size());
                ArrayList<DataMap> data = data_complete.get(World.class);
                Logger.log("data.size() == " + data.size());
                for (DataMap d : data) {
                    Logger.log("DataMap found: " + d.getName());
                    World world = World.newInstanceFromDataMap(d);
                    Logger.log("World unserialized: " + world);
                }
            } else if (false) {
                DataMapSerializer.deserialize(data_complete);
                HashMap<Class<?>, ArrayList<DataMapSerializable>> xmlsc = dataMapSerializer
                        .getClassesDataMapSerializables();
                Logger.log("xmlsc.size() == " + xmlsc.size());
                ArrayList<DataMapSerializable> xmls = xmlsc.get(World.class);
                Logger.log("xmls.size() == " + xmls.size());
                for (DataMapSerializable xml : xmls) {
                    Logger.log("Found XMLSerializable (" + xml.getClass().getName() + "): " + xml);
                }
            } else {
                /*
					 * ArrayList<World> worlds =
					 * dataMapSerializer.getObjects(World.class);
					 * Logger.log("worlds.size() == " + worlds.size());
					 * for(World world : worlds) { Logger.log("Found World: " +
					 * world); }
                 */
                Matrix3f matrix3f = new Matrix3f();
                Matrix4f matrix4f = new Matrix4f();
                Vector3f vector3f = new Vector3f(1, 2, 3);
                Vector2f vector2f = new Vector2f(1, 2);
                String temp = "";
                Logger.log((temp = vector2f.toString()));
                Logger.log((vector2f = SerializationUtil.stringToVector2f(temp)));
                AdvancedFile file = AdvancedFile.fileOfPath("E:\\Daten\\NetBeans\\Projekte\\OmniKryptec-Engine\\temp").addPaths("Test.xml");
                // Logger.log("file == " + file);
                // Logger.log("file.toFile() == " + file.toFile());
                // Logger.log("file.createAdvancedFile() == " + file.createAdvancedFile());
                dataMapSerializer.serialize("Welt_1", XMLSerializer.newInstance(), file.createOutputstream(false));
                Logger.log("file.exists() == " + file.exists());
                HashMap<Class<?>, ArrayList<DataMapSerializable>> xmlsc = dataMapSerializer.deserializeToDataMapSerializable(file.createInputStream(), XMLSerializer.newInstance());
            }
            Commands.COMMANDEXIT.run("-java");
        } catch (Exception ex) {
            Logger.logErr("Main error: " + ex, ex);
            Commands.COMMANDEXIT.run("-java");
        }
    }

}
