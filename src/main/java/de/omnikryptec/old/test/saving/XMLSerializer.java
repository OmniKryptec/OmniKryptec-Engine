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

import static de.omnikryptec.old.util.SerializationUtil.cast;
import static de.omnikryptec.old.util.SerializationUtil.castArray;
import static de.omnikryptec.old.util.SerializationUtil.classForName;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.omnikryptec.old.util.logger.Logger;

/**
 * XMLSerializer
 *
 * @author Panzer1119
 */
public class XMLSerializer implements IDataMapSerializer {

    public static Format format = Format.getPrettyFormat();

    private final SAXBuilder saxBuilder = new SAXBuilder();
    private final XMLOutputter xmlOutput = new XMLOutputter();
    private Document document = null;
    private Element rootElement = null;

    @Override
    public final boolean serialize(String name, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, OutputStream outputStream) {
        try {
            rootElement = new Element(name);
            document = new Document(rootElement);
            for (Class<?> c : classesDataMaps.keySet()) {
                final Element element = new Element(Type.Class.name()).setAttribute(NAME, c.getName());
                final ArrayList<DataMap> dataMaps = classesDataMaps.get(c);
                dataMaps.stream().forEach((dataMap) -> {
                    processDataMapsSerialize(element, dataMap.getName(), null, dataMap, 0);
                });
                rootElement.addContent(element);
            }
            xmlOutput.setFormat(format);
            xmlOutput.output(document, outputStream);
            outputStream.close();
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while serializing HashMap<Class<?>, ArrayList<DataMap>>: " + ex, ex);
            return false;
        }
    }

    private final void processObjectSerialize(Element element, Object name, Object object, int depth, Type lastType, Type lastOperationType, Object... objects) {
        try {
            if (object != null && object.getClass().isArray()) {
                processArraySerialize(element, name, lastOperationType, object, depth + 1, objects);
            } else if (object != null && object instanceof DataMap) {
                final DataMap dataMap_temp = (DataMap) object;
                processDataMapsSerialize(element, name, lastOperationType, dataMap_temp, depth + 1);
            } else if (object != null && object instanceof List) {
                final List list = (List) object;
                processListSerialize(element, name, lastOperationType, list, depth + 1);
            } else if (object != null && object instanceof Map) {
                final Map map = (Map) object;
                processMapSerialize(element, name, lastOperationType,  map, depth + 1);
            } else {
                final Type usedType = (lastType != null ? lastType : Type.Data);
                if (usedType != Type.Data) {
                    element.addContent(new Element(usedType.name()).setText(usedType.serializeValue(object))
                            .setAttribute(usedType.nameAttribute(), usedType.serializeKey(name))
                            .setAttribute(CLASS, getClassName(object)));
                } else {
                    element.addContent(new Element(usedType.name()).setText(usedType.serializeValue(object))
                            .setAttribute(NAME, "" + name).setAttribute(CLASS, getClassName(object)));
                }
            }
        } catch (Exception ex) {
            Logger.logErr("Error while serializing: " + ex, ex);
        }
    }

    private final void processDataMapsSerialize(Element element, Object name, Type lastOperationType, DataMap dataMap, int depth) {
        final Element element_temp = new Element(Type.DataMap.name())
                .setAttribute(COUNT, Type.DataMap.serializeKey((dataMap != null ? "" + dataMap.size() : 0)))
                .setAttribute(CLASS, String.class.getName()).setAttribute(DEPTH, "" + (depth - 1));
        if((lastOperationType != null) && (lastOperationType == Type.DataMap || lastOperationType == Type.Map)) {
            element_temp.setAttribute(KEY, "" + name);
            if(dataMap != null) {
                element_temp.setAttribute(NAME, "" + dataMap.getName());
            }
        } else {
            element_temp.setAttribute(NAME, "" + name);
        }
        if (dataMap != null) {
            dataMap.keySet().stream().forEach((g) -> {
                final Object object = dataMap.get(g);
                processObjectSerialize(element_temp, g, object, depth, Type.Data, Type.DataMap);
            });
        }
        element.addContent(element_temp);
    }

    private final void processListSerialize(Element element, Object name, Type lastOperationType, List list, int depth) {
        final Element element_temp = new Element(Type.List.name())
                .setAttribute(COUNT, Type.List.serializeKey((list != null ? "" + list.size() : 0)))
                .setAttribute(CLASS, Integer.class.getName()).setAttribute(DEPTH, "" + (depth - 1));
        if((lastOperationType != null) && (lastOperationType == Type.DataMap || lastOperationType == Type.Map)) {
            element_temp.setAttribute(KEY, "" + name);
        } else {
            element_temp.setAttribute(NAME, "" + name);
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                final Object object = list.get(i);
                processObjectSerialize(element_temp, i, object, depth, Type.ListData, Type.List);
            }
        }
        element.addContent(element_temp);
    }

    private final void processMapSerialize(Element element, Object name, Type lastOperationType, Map map, int depth) {
        final Element element_temp = new Element(Type.Map.name())
                .setAttribute(COUNT, Type.Map.serializeKey((map != null ? "" + map.size() : 0)))
                .setAttribute(DEPTH, "" + (depth - 1));
        if((lastOperationType != null) && (lastOperationType == Type.DataMap || lastOperationType == Type.Map)) {
            element_temp.setAttribute(KEY, "" + name);
        } else {
            element_temp.setAttribute(NAME, "" + name);
        }
        Object temp = null;
        boolean foundType = true;
        if (map != null) {
            for (Object key : map.keySet()) {
                if (foundType && temp != null && key != null && temp.getClass() != key.getClass()) {
                    temp = null;
                    foundType = false;
                }
                if (foundType) {
                    temp = key;
                }
                final Object value = map.get(key);
                processObjectSerialize(element_temp, key, value, depth, Type.MapData, Type.Map);
            }
        }
        element_temp.setAttribute(CLASS, getClassName(temp));
        element.addContent(element_temp);
    }

    private final void processArraySerialize(Element element, Object name, Type lastOperationType, Object array, int depth, Object... objects) {
        final Element element_temp = new Element(Type.Array.name()).setAttribute(DEPTH,
                "" + (depth - 1));
        if((lastOperationType != null) && (lastOperationType == Type.DataMap || lastOperationType == Type.Map)) {
            element_temp.setAttribute(KEY, "" + name);
        } else {
            element_temp.setAttribute(NAME, "" + name);
        }
        Element arrayElement = null;
        int arrayDepth = 1;
        if (objects.length == 2) {
            if (objects[1] != null && objects[1] instanceof Integer) {
                arrayDepth = (Integer) objects[1];
            }
            if (objects[0] != null && objects[0] instanceof Element) {
                arrayElement = (Element) objects[0];
            } else {
                arrayElement = element_temp;
                arrayDepth = 1;
            }
        } else {
            arrayElement = element_temp;
            arrayDepth = 1;
        }
        if (arrayElement != null) {
            arrayElement.setAttribute(DIMENSIONS, "" + arrayDepth);
            arrayDepth++;
        }
        if (array != null) {
            final Class<?> c = array.getClass();
            if (c.isArray()) {
                if (c == byte[].class) {
                    final byte[] array_temp = (byte[]) array;
                    element_temp.setAttribute(CLASS, Byte.class.getName()).setAttribute(COUNT, "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == short[].class) {
                    final short[] array_temp = (short[]) array;
                    element_temp.setAttribute(CLASS, Short.class.getName()).setAttribute(COUNT, "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == int[].class) {
                    final int[] array_temp = (int[]) array;
                    element_temp.setAttribute(CLASS, Integer.class.getName()).setAttribute(COUNT,
                            "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == long[].class) {
                    final long[] array_temp = (long[]) array;
                    element_temp.setAttribute(CLASS, Long.class.getName()).setAttribute(COUNT, "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == char[].class) {
                    final char[] array_temp = (char[]) array;
                    element_temp.setAttribute(CLASS, Character.class.getName()).setAttribute(COUNT,
                            "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == float[].class) {
                    final float[] array_temp = (float[]) array;
                    element_temp.setAttribute(CLASS, Float.class.getName()).setAttribute(COUNT, "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == double[].class) {
                    final double[] array_temp = (double[]) array;
                    element_temp.setAttribute(CLASS, Double.class.getName()).setAttribute(COUNT,
                            "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else if (c == boolean[].class) {
                    final boolean[] array_temp = (boolean[]) array;
                    element_temp.setAttribute(CLASS, Boolean.class.getName()).setAttribute(COUNT,
                            "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                } else {
                    final Object[] array_temp = (Object[]) array;
                    element_temp.setAttribute(CLASS, Object.class.getName()).setAttribute(COUNT,
                            "" + array_temp.length);
                    for (int i = 0; i < array_temp.length; i++) {
                        final Object object = array_temp[i];
                        processObjectSerialize(element_temp, i, object, depth, Type.ArrayData, Type.Array, arrayElement,
                                arrayDepth);
                    }
                }
            } else {
                processObjectSerialize(element, name, name, depth - 1, Type.Data, Type.Array);
            }
        }
        element.addContent(element_temp);
    }

    private static final String getClassName(Object object) {
        if (object == null) {
            return Object.class.getName();
        }
        return object.getClass().getName();
    }

    @Override
    public final HashMap<Class<?>, ArrayList<DataMap>> deserialize(InputStream inputStream) {
        try {
            final HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps = new HashMap<>();
            document = saxBuilder.build(inputStream);
            rootElement = document.getRootElement();
            rootElement.getChildren().stream().forEach((element) -> {
                processElementUnserialize(element, classesDataMaps, null);
            });
            return classesDataMaps;
        } catch (Exception ex) {
            Logger.logErr("Error while unserializing InputStream: " + ex, ex);
            return null;
        }
    }

    private final void processElementUnserialize(Element element, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps,
            ArrayList<DataMap> dataMaps) {
        try {
            if (element.getName().equals(Type.Class.name())) {
                processClassUnserialize(element, classesDataMaps);
            } else if (element.getName().equals(Type.DataMap.name())) {
                final DataMap dataMap = new DataMap(element.getAttributeValue(NAME));
                processDataMapsUnserialize(element, dataMap);
                if (dataMaps != null) {
                    dataMaps.add(dataMap);
                }
            }
        } catch (Exception ex) {
            Logger.logErr("Error while processing object unserializing: " + ex, ex);
        }
    }

    private final void processClassUnserialize(Element element, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps) {
        final Class<?> c = classForName(element.getAttributeValue(NAME));
        ArrayList<DataMap> dataMaps = classesDataMaps.get(c);
        if (dataMaps == null) {
            dataMaps = new ArrayList<>();
            classesDataMaps.put(c, dataMaps);
        }
        for (Element element_temp : element.getChildren()) {
            processElementUnserialize(element_temp, classesDataMaps, dataMaps);
        }
    }

    private final void processDataMapsUnserialize(Element element, DataMap dataMap) {
        element.getChildren().stream().forEach((element_temp) -> {
            processElementInDataMapUnserialize(element_temp, dataMap);
        });
    }

    private final void processElementInDataMapUnserialize(Element element, DataMap dataMap) {
        try {
            Class<?> c = null;
            if (element.getName().equals(Type.Array.name())) {
                dataMap.put(element.getAttributeValue(NAME), processArrayUnserialize(element));
            } else if (element.getName().equals(Type.Data.name())) {
                c = classForName(element.getAttributeValue(CLASS));
                dataMap.put(element.getAttributeValue(NAME), cast(c, element.getText()));
            } else if (element.getName().equals(Type.DataMap.name())) {
                final DataMap dataMap_temp = new DataMap(element.getAttributeValue(NAME));
                processDataMapsUnserialize(element, dataMap_temp);
                dataMap.put(element.getAttributeValue(KEY), dataMap_temp);
            } else if (element.getName().equals(Type.List.name())) {
                final HashMap<Integer, Object> collectorMap = new HashMap<>();
                processListUnserialize(element, collectorMap);
                final List list = new ArrayList<>();
                collectorMap.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                    list.add(collectorMap.get(i));
                });
                collectorMap.clear();
                dataMap.put(element.getAttributeValue(KEY), list);
            } else if (element.getName().equals(Type.Map.name())) {
                final Map map = new HashMap();
                processMapUnserialize(element, map);
                dataMap.put(element.getAttributeValue(KEY), map);
            }
        } catch (Exception ex) {
            Logger.logErr("Error while processing data maps while unserializing: " + ex, ex);
        }
    }

    private final Object[] processArrayUnserialize(Element element) {
        final int count = Integer.parseInt(element.getAttributeValue(COUNT));
        final Class<?> c = classForName(element.getAttributeValue(CLASS));
        Object[] array = new Object[count];
        element.getChildren().stream().forEach((element_temp) -> {
            processDataInArrayUnserialize(element_temp, array);
        });
        return castArray(array, c);
    }

    private final void processDataInArrayUnserialize(Element element, Object[] array) {
        try {
            int index = -1;
            Class<?> c = null;
            if (element.getName().equals(Type.Array.name())) {
                index = Integer.parseInt(element.getAttributeValue(NAME));
                array[index] = processArrayUnserialize(element);
            } else if (element.getName().equals(Type.ArrayData.name())) {
                index = Integer.parseInt(element.getAttributeValue(Type.ArrayData.nameAttribute()));
                c = classForName(element.getAttributeValue(CLASS));
                array[index] = cast(c, element.getText());
            } else if (element.getName().equals(Type.DataMap.name())) {
                index = Integer.parseInt(element.getAttributeValue(NAME));
                final DataMap dataMap_temp = new DataMap(element.getAttributeValue(NAME));
                processDataMapsUnserialize(element, dataMap_temp);
                array[index] = dataMap_temp;
            } else if (element.getName().equals(Type.List.name())) {
                index = Integer.parseInt(element.getAttributeValue(NAME));
                final HashMap<Integer, Object> collectorMap = new HashMap<>();
                processListUnserialize(element, collectorMap);
                final List list = new ArrayList<>();
                collectorMap.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                    list.add(collectorMap.get(i));
                });
                collectorMap.clear();
                array[index] = list;
            } else if (element.getName().equals(Type.Map.name())) {
                index = Integer.parseInt(element.getAttributeValue(NAME));
                final Map map = new HashMap();
                processMapUnserialize(element, map);
                array[index] = map;
            }
        } catch (Exception ex) {
            Logger.logErr("Error while processing array: " + ex, ex);
        }
    }

    private final void processListUnserialize(Element element, HashMap<Integer, Object> collectorMap) {
        element.getChildren().stream().forEach((element_temp) -> {
            try {
                final Class<?> c = classForName(element_temp.getAttributeValue(CLASS));
                Integer index = -1;
                if (element.getName().equals(Type.Array.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    collectorMap.put(index, processArrayUnserialize(element));
                } else if (element_temp.getName().equals(Type.ListData.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(Type.ListData.nameAttribute()));
                    collectorMap.put(index, cast(c, element_temp.getText()));
                } else if (element_temp.getName().equals(Type.DataMap.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    final DataMap dataMap_temp = new DataMap("" + index);
                    processDataMapsUnserialize(element_temp, dataMap_temp);
                    collectorMap.put(index, dataMap_temp);
                } else if (element_temp.getName().equals(Type.List.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    final HashMap<Integer, Object> collectorMap_temp = new HashMap<>();
                    processListUnserialize(element_temp, collectorMap_temp);
                    final List list = new ArrayList<>();
                    collectorMap_temp.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                        list.add(collectorMap_temp.get(i));
                    });
                    collectorMap_temp.clear();
                    collectorMap.put(index, list);
                } else if (element_temp.getName().equals(Type.Map.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    final Map map = new HashMap();
                    processMapUnserialize(element_temp, map);
                    collectorMap.put(index, map);
                }
            } catch (Exception ex) {
                Logger.logErr("Error while processing list while unserializing: " + ex, ex);
            }
        });
    }

    private final void processMapUnserialize(Element element, Map map) {
        final Class<?> c_key = classForName(element.getAttributeValue(CLASS));
        element.getChildren().stream().forEach((element_temp) -> {
            try {
                final Class<?> c_value = classForName(element_temp.getAttributeValue(CLASS));
                Object key = null;
                if (element.getName().equals(Type.Array.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(NAME));
                    map.put(key, processArrayUnserialize(element));
                } else if (element_temp.getName().equals(Type.MapData.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(Type.MapData.nameAttribute()));
                    map.put(key, cast(c_value, element_temp.getText()));
                } else if (element_temp.getName().equals(Type.DataMap.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(KEY));
                    final DataMap dataMap_temp = new DataMap(element.getAttributeValue(NAME));
                    processDataMapsUnserialize(element_temp, dataMap_temp);
                    map.put(key, dataMap_temp);
                } else if (element_temp.getName().equals(Type.List.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(NAME));
                    final HashMap<Integer, Object> collectorMap = new HashMap<>();
                    processListUnserialize(element_temp, collectorMap);
                    final List list = new ArrayList<>();
                    collectorMap.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                        list.add(collectorMap.get(i));
                    });
                    collectorMap.clear();
                    map.put(key, list);
                } else if (element_temp.getName().equals(Type.Map.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(NAME));
                    final Map map_temp = new HashMap();
                    processMapUnserialize(element_temp, map_temp);
                    map.put(key, map_temp);
                }
            } catch (Exception ex) {
                Logger.logErr("Error while processing map while unserializing: " + ex, ex);
            }
        });
    }

    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String CLASS = "class";
    public static final String DEPTH = "depth";
    public static final String DIMENSIONS = "dimensions";
    public static final String KEY = "key";

    public static enum Type {
        Array       ("", "", ""),
        ArrayData   ("%s", "%s", "index"),
        Class       ("", "", ""),
        Data        ("", "%s", ""),
        DataMap     ("%s", "", ""),
        List        ("%s", "", ""),
        ListData    ("%s", "%s", "index"),
        Map         ("", "", ""),
        MapData     ("%s", "%s", "key");

        private final String serializeKey;
        private final String serializeValue;
        private final String nameAttribute;

        Type(String serializeKey, String serializeValue, String nameAttribute) {
            this.serializeKey = serializeKey;
            this.serializeValue = serializeValue;
            this.nameAttribute = nameAttribute;
        }

        public String serializeKey(Object data) {
            return String.format(serializeKey, data);
        }

        public String serializeValue(Object data) {
            return String.format(serializeValue, data);
        }

        public String nameAttribute() {
            return nameAttribute;
        }

    }

    public static final XMLSerializer newInstance() {
        return new XMLSerializer();
    }

}
